package pt.isel.ui

import checkers.*
import isel.leic.tds.checkers.*
import storage.*
import java.io.*
import kotlin.test.*

class UiTest {
    val serializer = object : StringSerializer<Board>{
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) = input.deserializeToBoard()
    }

    val fs = FileStorage<String, Board>("Output", serializer){ BoardRun() }

    @Test fun `Check command action returning null finishes the readCommands loop`(){
        redirectInOut(listOf()) {
            readCommandsOop(mapOf(
                "EXIT" to CmdExitOop,
                "PLAY" to CmdPlayOop(fs),
                "START" to CmdStartOop(fs),
                "REFRESH" to CmdRefreshOop(fs)
            ))
        }
    }

    @Test fun `Check invalid command`() {
        val lines = redirectInOut(listOf("dummy", "exit")) {
            readCommandsOop(mapOf(
                "EXIT" to CmdExitOop
            ))
        }
        assertEquals("Invalid command", lines[0])
        lines.forEach { println(it) }
    }

    fun redirectInOut(stmts: List<String>, block: () -> Unit) : List<String> {
        /**
         * First store the standard input and output and
         * redirect to memory.
         */
        val oldOut = System.out
        val oldIn = System.`in`
        val mem = ByteArrayOutputStream()
        System.setOut(PrintStream(mem))
        System.setIn(ByteArrayInputStream("dummy\nexit\n".toByteArray()))
        /**
         * Execute given block.
         */
        block()
        /**
         * Restore standard input and output and return the resulting output lines.
         */
        System.setOut(oldOut)
        System.setIn(oldIn)
        return mem
            .toString()
            .split(System.lineSeparator())
            .map { if(it.startsWith("> ")) it.drop(2) else it }
    }
}