package io.github.elisatronetti

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * The component registrar registers the alignment generation extension if
 * the compiler plugin is enabled.
 */
@AutoService(ComponentRegistrar::class)
class AlignmentComponentRegistrar(
    private val defaultEnabled: Boolean,
) : ComponentRegistrar {

  @Suppress("unused") // Used by service loader
  constructor() : this(
      defaultEnabled = true,
  )

  override fun registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
      val enabled = configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED, defaultEnabled)
      if (enabled) {
          IrGenerationExtension.registerExtension(project, AlignmentIrGenerationExtension())
      }
  }
}
