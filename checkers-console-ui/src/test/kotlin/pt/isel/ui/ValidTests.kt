package pt.isel.ui

import isel.leic.tds.checkers.*
import java.io.*
import kotlin.test.*

class ValidTests {
    val testFolder = "Output"
    val dummyId = 712535

    @BeforeTest fun setup() {
        val f = File("$testFolder/$dummyId.txt")
        if(f.exists()) f.delete()
    }

    @Test fun `Valid move`() {
        val board = BoardRun()
            .play(Square(5,0), Square(4,1), Player.WHITE)
        assertEquals('w', Square(4,1).toPosition(board).symbol)
    }

    @Test fun `Mandatory capture done`() {
        val board = BoardRun()
            .play(Square(5,0), Square(4,1), Player.WHITE)
            .play(Square(2,3), Square(3,2), Player.BLACK)
            .play(Square(4,1), Square(2,3), Player.WHITE)
        assertEquals(null, Square(3,2).toPosition(board).player)
        assertEquals(Player.WHITE, Square(2,3).toPosition(board).player)
    }
    @Test fun `Mandatory capture with wrong piece`(){
        val board = BoardRun()
            .play(Square(5,0),Square(4,1), Player.WHITE)
            .play(Square(2,1),Square(3,0), Player.BLACK)
            .play(Square(5,4),Square(4,3), Player.WHITE)
            .play(Square(2,3),Square(3,2), Player.BLACK)
            .play(Square(4,3),Square(2,1), Player.WHITE)
        assertEquals(Player.WHITE, Square(2,1).toPosition(board).player)
        assertEquals(null, Square(3,2).toPosition(board).player)
    }

    @Test fun `Double take for white piece`(){
        val board = BoardRun()
            .play(Square(5, 0), Square(4, 1), Player.WHITE)
            .play(Square(2, 3), Square(3, 4), Player.BLACK)
            .play(Square(5, 4), Square(4, 5), Player.WHITE)
            .play(Square(2, 7), Square(3, 6), Player.BLACK)
            .play(Square(4, 5), Square(2, 7), Player.WHITE)
        assertEquals(Player.WHITE, Square(2,7).toPosition(board).player)
        assertEquals(null, Square(3,6).toPosition(board).player)
    }

    @Test fun `Two pieces can capture at same time`(){
        val board = BoardRun()
            .play(Square(5, 0), Square(4, 1), Player.WHITE)
            .play(Square(2, 3), Square(3, 4), Player.BLACK)
            .play(Square(5, 4), Square(4, 5), Player.WHITE)
            .play(Square(2, 1), Square(3, 2), Player.BLACK)
            .play(Square(4, 5), Square(2, 3), Player.WHITE)
        assertEquals(Player.WHITE, Square(2,3).toPosition(board).player)
        assertEquals(null, Square(3,4).toPosition(board).player)
    }

    @Test fun `Consecutive take`() {
        val board = BoardRun()
                .play(Square(5,0), Square(4,1), Player.WHITE)
                .play(Square(2,3), Square(3,4), Player.BLACK)
                .play(Square(5,6), Square(4,7), Player.WHITE)
                .play(Square(2,1), Square(3,2), Player.BLACK)
                .play(Square(4,1), Square(2,3), Player.WHITE)
                .play(Square(2,3), Square(4,5), Player.WHITE)
        assertEquals(Player.WHITE, Square(4,5).toPosition(board).player)
        assertEquals(null, Square(3,2).toPosition(board).player)
        assertEquals(null, Square(3,4).toPosition(board).player)
    }
}
