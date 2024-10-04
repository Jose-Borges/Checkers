package pt.isel.ui

import checkers.*
import isel.leic.tds.checkers.*
import storage.*
import java.io.File
import kotlin.test.*

class GameTests {

    val testFolder = "Output"
    val dummyId = 712535

    private val serializer = object : StringSerializer<Board> {
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) = input.deserializeToBoard()
    }

    @BeforeTest fun setup() {
        val f = File("$testFolder/$dummyId.txt")
        if(f.exists()) f.delete()
    }

    private val fs = FileStorage<String, Board>("Output", serializer){ BoardRun() }

    @Test fun `Play a valid move`(){
        val game = startGame("test", fs)
        val plays = listOf("3a", "4b")
        game.play(fs,plays)
        assertEquals('w', Square(4,1).toPosition(game.board).symbol)
    }

    @Test fun `Playing a valid capture`(){
        val game = startGame("test", fs)
        var plays = listOf("3a", "4b")
        game.play(fs,plays)
        plays = listOf("6d", "5c")
        game.play(fs, plays)
        plays = listOf("4b", "6d")
        game.play(fs, plays)
        assertEquals('w', Square(2,3).toPosition(game.board).symbol)
        assertEquals(null, Square(3,2).toPosition(game.board).symbol)
    }

    @Test fun `Playing without squares`(){
        val ex = assertFailsWith<IllegalArgumentException> {
            val game = startGame("test", fs)
            val plays = emptyList<String>()
            game.play(fs,plays)
        }
        assertEquals("No arguments were passed", ex.message)
    }

    @Test fun `Playing with a non black square`(){
        val ex = assertFailsWith<IllegalArgumentException> {
            val game = startGame("test", fs)
            val plays = listOf("2a", "4b")
            game.play(fs,plays)
        }
        assertEquals("Invalid square 2a", ex.message)
    }

    @Test fun `Playing to a non black square`(){
        val ex = assertFailsWith<IllegalArgumentException> {
            val game = startGame("test", fs)
            val plays = listOf("3a", "4a")
            game.play(fs,plays)
        }
        assertEquals("Invalid square 4a", ex.message)
    }
}
