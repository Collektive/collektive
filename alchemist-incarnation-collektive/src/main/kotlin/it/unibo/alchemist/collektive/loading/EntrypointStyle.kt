package it.unibo.alchemist.collektive.loading

internal sealed interface EntrypointStyle
internal data class LoadFromSource(
    val name: String,
    val sourceSets: List<String>,
    val code: String,
    val entrypoint: String,
) : EntrypointStyle

internal data class LoadFromInline(val name: String, val code: String, val entrypoint: String) : EntrypointStyle
internal data class LoadFromEntrypoint(val entrypoint: String) : EntrypointStyle
