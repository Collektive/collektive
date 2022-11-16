package stack

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PathTest {
    private val path: Path = PathImpl()

    @Test
    fun createEmptyPath() {
        val emptyList: List<String> = emptyList()
        assertEquals(emptyList, path.currentList())
        assertEquals(emptyList.toString(), path.current())
    }

    @Test
    fun addTokenToPath() {
        path.addToken(Token.NEIGHBOURING)
        assertTrue(path.currentList().contains(Token.NEIGHBOURING.toString()))
    }
}
