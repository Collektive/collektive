package it.unibo.collektive

sealed interface CompilerPluginOption

data object DebugMode : CompilerPluginOption

data object PrototypeMode : CompilerPluginOption

data class ReleaseMode(val key: String) : CompilerPluginOption
