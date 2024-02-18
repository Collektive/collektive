package it.unibo.alchemist.collektive

import it.unibo.alchemist.model.Position
import it.unibo.collektive.compiler.util.md5
import it.unibo.collektive.compiler.util.toBase32
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.text.RegexOption.DOT_MATCHES_ALL

/**
 * Compiler for Collektive programs in Alchemist.
 */
object CollektiveCompilerForAlchemist {

    private const val SOURCES = "loadSources"
    private const val ENTRY_POINT = "collektive"
    private fun regexForCurlyBraces(blockName: String) = Regex("$blockName\\s*\\s*\\{(.*)}", DOT_MATCHES_ALL)
    private val sourcesRegex = regexForCurlyBraces(SOURCES)
    private val entryPointRegex = regexForCurlyBraces(ENTRY_POINT)

    private fun String.sourceSets(): List<File> = sourcesRegex.matchEntire(this)
        ?.groupValues
        ?.get(1)
        ?.splitToSequence(',', ';', '\n', '\r')
        ?.filter { it.isNotBlank() }
        ?.map { File(it.trim()) }
        ?.onEach { require(it.exists()) { "File $it does not exist" } }
        ?.toList()
        .orEmpty()

    /**
     * Loads a Collektive program from a string.
     */
    fun <P : Position<P>> loadFrom(inputText: String): CollektiveAlchemistProgram<P> {
        val sourcesMatch = sourcesRegex.find(inputText)
        val sources = sourcesMatch?.groupValues?.get(2)
        val entryPoint = entryPointRegex.find(inputText)?.groupValues?.get(2)
        val error = when {
            sources == null && entryPoint == null -> listOf("No", "or", "at least")
            sources != null && entryPoint != null -> listOf("Both", "and", "at most")
            else -> emptyList()
        }
        if (error.isNotEmpty()) {
            val (noBoth, orAnd, atLeastAtMost) = error
            error(
                """
                $noBoth sources $orAnd entrypoint found in input text, $atLeastAtMost one must be specified with EITHER:
                    $SOURCES {
                        /path/to/source/folder1
                        /path/to/source/folder2
                        ...
                    }
                    your program
                OR, ALTERNATIVELY:
                    package ...
                    import ...
                    fun ...
                    fun ...
                    $ENTRY_POINT {
                        your program
                    }
                """.trimIndent(),
            )
        }
        val program: String = entryPoint ?: inputText.substring(
            requireNotNull(sourcesMatch) { "Bug in ${this::class.simpleName}" }.range.last,
        ).trim()
        val destinationFile = createTempDirectory("alchemist-collektive")
            .resolve("Collektive${program.md5().toBase32()}")
        destinationFile.writeText(program)
        val sourcePaths = sources?.sourceSets().orEmpty() + destinationFile
        println(sourcePaths)
        TODO()
    }
}
