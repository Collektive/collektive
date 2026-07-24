/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import kotlin.reflect.KClass
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM9

internal fun KClass<out Annotation>.isErrorLevelOptInMarker(): Boolean {
    var isOptInMarker = false
    var errorLevel = true
    java.getResourceAsStream("/${java.name.replace('.', '/')}.class")?.use { resource ->
        ClassReader(resource).accept(
            object : ClassVisitor(ASM9) {
                override fun visitAnnotation(annotationDescriptor: String, visible: Boolean): AnnotationVisitor? =
                    if (annotationDescriptor == "Lkotlin/RequiresOptIn;") {
                        isOptInMarker = true
                        object : AnnotationVisitor(ASM9) {
                            override fun visitEnum(name: String, levelDescriptor: String, value: String) {
                                if (name == "level") {
                                    errorLevel = value == "ERROR"
                                }
                            }
                        }
                    } else {
                        null
                    }
            },
            ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES,
        )
    }
    return isOptInMarker && errorLevel
}
