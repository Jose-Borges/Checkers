package pt.isel.ui

import isel.leic.tds.checkers.*
import java.io.*
import kotlin.test.*

class ErrorTests {
    val testFolder = "Output"
    val dummyId = 712535

    @BeforeTest fun setup() {
        val f = File("$testFolder/$dummyId.txt")
        if(f.exists()) f.delete()
    }

    @Test fun `We cannot instantiate a Position with coordinates out of board size`() {
        val ex = assertFailsWith<IndexOutOfBoundsException> {
            Square(-1, 0)
        }
        assertEquals("Index -1 out of bounds for length $BOARD_DIM", ex.message)
    }

    @Test fun `When same player play twice it throws an IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(4,1), Player.WHITE)
                .play(Square(5,3), Square(4,2), Player.WHITE)
        }
        assertEquals("Not your turn", ex.message)
    }

    @Test fun `Playing to white square`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(4,0), Player.WHITE)
        }
        assertEquals("Invalid Move", ex.message)
    }

    @Test fun `Playing twice on same position throws IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(4,1), Player.WHITE)
                .play(Square(2,1), Square(3,2), Player.BLACK)
                .play(Square(5,2), Square(4,1), Player.WHITE)
        }
        assertEquals("Position 4b is already occupied", ex.message)
    }

    @Test fun `Playing on same position throws IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(5,0), Player.WHITE)

        }
        assertEquals("Position 3a is already occupied", ex.message)
    }

    @Test fun `Playing on occupied position by enemy piece throws IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(4,1), Player.WHITE)
                .play(Square(2,1), Square(3,2), Player.BLACK)
                .play(Square(4,1), Square(3,2), Player.WHITE)
        }
        assertEquals("Position 5c is already occupied", ex.message)
    }

    @Test fun `Player already won game IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalStateException> {
            BoardWinner(initPos, Player.WHITE)
                .play(Square(5,2), Square(4,1), Player.WHITE)

        }
        assertEquals("The player WHITE won this game.", ex.message)
    }

    @Test fun `Game is already finished with a draw IllegalArgumentException`() {
        val ex = assertFailsWith<IllegalStateException> {
            BoardDraw(initPos)
                .play(Square(5,2), Square(4,3), Player.WHITE)
        }
        assertEquals("This game has already finished with a draw.", ex.message)
    }

    @Test fun `Mandatory capture check`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5,0), Square(4,1), Player.WHITE)
                .play(Square(2,3), Square(3,2), Player.BLACK)
                .play(Square(4,1), Square(3,0), Player.WHITE)
        }
        assertEquals("There's a mandatory capture at 4b", ex.message)
    }

    @Test fun `Consecutive take`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                    .play(Square(5,0), Square(4,1), Player.WHITE)
                    .play(Square(2,3), Square(3,4), Player.BLACK)
                    .play(Square(5,6), Square(4,7), Player.WHITE)
                    .play(Square(2,1), Square(3,2), Player.BLACK)
                    .play(Square(4,1), Square(2,3), Player.WHITE)
                    .play(Square(2,7), Square(3,6), Player.BLACK)
        }
        assertEquals("Not your turn", ex.message)
    }

    @Test fun `Game Draw`() {
        val ex = assertFailsWith<IllegalStateException> {
            var board = BoardRun()
                .play(Square(5, 0), Square(4, 1), Player.WHITE)
                .play(Square(2, 3), Square(3, 4), Player.BLACK)
                .play(Square(4, 1), Square(3, 0), Player.WHITE)
                .play(Square(3, 4), Square(4, 3), Player.BLACK)
                .play(Square(5, 2), Square(3, 4), Player.WHITE)
                .play(Square(2, 5), Square(4, 3), Player.BLACK)
                .play(Square(5, 4), Square(3, 2), Player.WHITE)
                .play(Square(2, 1), Square(4, 3), Player.BLACK)
                .play(Square(6, 3), Square(5, 4), Player.WHITE)
                .play(Square(4, 3), Square(5, 2), Player.BLACK)
                .play(Square(6, 1), Square(4, 3), Player.WHITE)
                .play(Square(2, 7), Square(3, 6), Player.BLACK)
                .play(Square(5, 6), Square(4, 7), Player.WHITE)
                .play(Square(1, 2), Square(2, 1), Player.BLACK)
                .play(Square(4, 7), Square(2, 5), Player.WHITE)
                .play(Square(1, 6), Square(3, 4), Player.BLACK)
                .play(Square(3, 4), Square(5, 2), Player.BLACK)
                .play(Square(3, 0), Square(1, 2), Player.WHITE)
                .play(Square(0, 1), Square(2, 3), Player.BLACK)
                .play(Square(5, 4), Square(4, 5), Player.WHITE)
                .play(Square(1, 0), Square(2, 1), Player.BLACK)
                .play(Square(7, 0), Square(6, 1), Player.WHITE)
                .play(Square(5, 2), Square(7, 0), Player.BLACK)
                .play(Square(4, 5), Square(3, 4), Player.WHITE)
                .play(Square(7, 0), Square(2, 5), Player.BLACK)
                .play(Square(7, 2), Square(6, 1), Player.WHITE)
                .play(Square(2, 5), Square(7, 0), Player.BLACK)
                .play(Square(6, 5), Square(5, 6), Player.WHITE)
                .play(Square(7, 0), Square(4, 3), Player.BLACK)
                .play(Square(5, 6), Square(4, 7), Player.WHITE)
                .play(Square(2, 1), Square(3, 0), Player.BLACK)
                .play(Square(4, 7), Square(3, 6), Player.WHITE)
                .play(Square(4, 3), Square(2, 1), Player.BLACK)
                .play(Square(3, 6), Square(2, 7), Player.WHITE)
                .play(Square(0, 5), Square(1, 6), Player.BLACK)
                .play(Square(2, 7), Square(0, 5), Player.WHITE)
                .play(Square(0, 7), Square(1, 6), Player.BLACK)
                .play(Square(0, 5), Square(2, 7), Player.WHITE)
                .play(Square(2, 1), Square(1, 2), Player.BLACK)
                .play(Square(2, 7), Square(1, 6), Player.WHITE)
                .play(Square(1, 2), Square(0, 1), Player.BLACK)
                .play(Square(1, 6), Square(0, 7), Player.WHITE)
                .play(Square(0, 1), Square(1, 2), Player.BLACK)
                .play(Square(0, 7), Square(1, 6), Player.WHITE)
            repeat(25){
                board = board
                    .play(Square(1, 2), Square(0, 1), Player.BLACK)
                    .play(Square(1, 6), Square(0, 7), Player.WHITE)
                    .play(Square(0, 1), Square(1, 2), Player.BLACK)
                    .play(Square(0, 7), Square(1, 6), Player.WHITE)}
        }
        assertEquals("This game has already finished with a draw.", ex.message)
    }
    @Test fun `Game Winner`() {
        val board = BoardRun()
            .play(Square(5, 0), Square(4, 1), Player.WHITE)
            .play(Square(2, 3), Square(3, 4), Player.BLACK)
            .play(Square(4, 1), Square(3, 0), Player.WHITE)
            .play(Square(3, 4), Square(4, 3), Player.BLACK)
            .play(Square(5, 2), Square(3, 4), Player.WHITE)
            .play(Square(2, 5), Square(4, 3), Player.BLACK)
            .play(Square(5, 4), Square(3, 2), Player.WHITE)
            .play(Square(2, 1), Square(4, 3), Player.BLACK)
            .play(Square(6, 3), Square(5, 4), Player.WHITE)
            .play(Square(4, 3), Square(5, 2), Player.BLACK)
            .play(Square(6, 1), Square(4, 3), Player.WHITE)
            .play(Square(2, 7), Square(3, 6), Player.BLACK)
            .play(Square(5, 6), Square(4, 7), Player.WHITE)
            .play(Square(1, 2), Square(2, 1), Player.BLACK)
            .play(Square(4, 7), Square(2, 5), Player.WHITE)
            .play(Square(1, 6), Square(3, 4), Player.BLACK)
            .play(Square(3, 4), Square(5, 2), Player.BLACK)
            .play(Square(3, 0), Square(1, 2), Player.WHITE)
            .play(Square(0, 1), Square(2, 3), Player.BLACK)
            .play(Square(5, 4), Square(4, 5), Player.WHITE)
            .play(Square(1, 0), Square(2, 1), Player.BLACK)
            .play(Square(7, 0), Square(6, 1), Player.WHITE)
            .play(Square(5, 2), Square(7, 0), Player.BLACK)
            .play(Square(4, 5), Square(3, 4), Player.WHITE)
            .play(Square(7, 0), Square(2, 5), Player.BLACK)
            .play(Square(7, 2), Square(6, 1), Player.WHITE)
            .play(Square(2, 5), Square(7, 0), Player.BLACK)
            .play(Square(6, 5), Square(5, 4), Player.WHITE)
            .play(Square(2, 1), Square(3, 2), Player.BLACK)
            .play(Square(6, 7), Square(5, 6), Player.WHITE)
            .play(Square(7, 0), Square(4, 3), Player.BLACK)
            .play(Square(7, 4), Square(6, 3), Player.WHITE)
            .play(Square(4, 3), Square(6, 5), Player.BLACK)
            .play(Square(6, 5), Square(4, 7), Player.BLACK)
            .play(Square(7, 6), Square(6, 5), Player.WHITE)
            .play(Square(4, 7), Square(7, 4), Player.BLACK)
            .play(Square(7, 4), Square(5, 2), Player.BLACK)
        assertEquals(Player.BLACK, (board as BoardWinner).winner)
        val ex = assertFailsWith<IllegalStateException> {
            board.play(Square(4,3), Square(3,4), Player.WHITE)
        }
        assertEquals("The player BLACK won this game.", ex.message)
    }
}
