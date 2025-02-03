/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.codegen

import it.unibo.collektive.utils.logging.warn
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.FqName

class SerializationObjectGeneration(private val logger: MessageCollector) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) {
        val visitedMembers = mutableSetOf<FqName>()
        moduleFragment.acceptChildren(VisitDeclarationWithAnnotation(logger), visitedMembers)
        logger.warn("Declarations with @Serializable:\n${visitedMembers.joinToString("\n")}")
    }

    private class VisitDeclarationWithAnnotation(private val logger: MessageCollector) :
        IrVisitor<Unit, MutableSet<FqName>>() {
        override fun visitElement(
            element: IrElement,
            data: MutableSet<FqName>
        ) {
            element.acceptChildren(this, data)
        }

        override fun visitDeclaration(declaration: IrDeclarationBase, data: MutableSet<FqName>) {
            if (declaration.annotations.any { it.type.classFqName == FqName("kotlinx.serialization.Serializable") }) {
                val symbolFqName = when (val symbol = declaration.symbol) {
                    is IrClassifierSymbol -> symbol.defaultType.classFqName
                    else -> null
                }
                symbolFqName?.let {
                    logger.warn(declaration.parent.dumpKotlinLike())
                    data.add(it)
                }
            }
            super.visitDeclaration(declaration, data)
        }
    }
}
