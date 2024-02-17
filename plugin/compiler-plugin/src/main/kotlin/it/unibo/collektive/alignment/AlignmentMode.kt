package it.unibo.collektive.alignment

sealed interface AlignmentMode

data object DebugMode : AlignmentMode

data object PrototypeMode : AlignmentMode

data class ReleaseMode(val password: String) : AlignmentMode
