/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.java

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

private sealed interface CachedAnnotation {
    val annotation: KClass<out Annotation>?
}
private object NoAnnotation : CachedAnnotation {
    override val annotation: KClass<out Annotation>? = null
}

@JvmInline
private value class AnnotationPresent(override val annotation: KClass<out Annotation>) : CachedAnnotation

private val annotationClassCache = ConcurrentHashMap<String, CachedAnnotation>()

@Suppress("UNCHECKED_CAST")
private fun descriptorToAnnotationClass(descriptor: String): KClass<out Annotation>? {
    val cachedAnnotation = annotationClassCache.computeIfAbsent(descriptor) {
        runCatching {
            AnnotationPresent(
                Class.forName(
                    descriptor.substring(1, descriptor.length - 1).replace('/', '.'),
                ).kotlin as KClass<out Annotation>,
            )
        }.getOrNull() ?: NoAnnotation
    }
    return cachedAnnotation.annotation
}

internal fun Method.annotationsFromBytecode(): Sequence<KClass<out Annotation>> =
    declaringClass.getResourceAsStream("/${declaringClass.name.replace('.', '/')}.class")?.use { resource ->
        val targetDescriptor = Type.getMethodDescriptor(this)
        buildSet {
            ClassReader(resource).accept(
                object : ClassVisitor(ASM9) {
                    override fun visitMethod(
                        access: Int,
                        name: String,
                        candidateDescriptor: String,
                        signature: String?,
                        exceptions: Array<out String>?,
                    ): MethodVisitor? = when {
                        name == this@annotationsFromBytecode.name && candidateDescriptor == targetDescriptor ->
                            object : MethodVisitor(ASM9) {
                                override fun visitAnnotation(
                                    annotationDescriptor: String,
                                    visible: Boolean,
                                ): AnnotationVisitor? {
                                    if (annotationDescriptor.startsWith("L") && annotationDescriptor.endsWith(";")) {
                                        add(descriptorToAnnotationClass(annotationDescriptor))
                                    }
                                    return null
                                }
                            }
                        else -> null
                    }
                },
                ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES,
            )
        }.asSequence().filterNotNull()
    }.orEmpty()
