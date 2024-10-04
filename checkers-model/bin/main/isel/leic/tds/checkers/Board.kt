package isel.leic.tds.checkers

const val BOARD_DIM = 8
/** Indicates the maximum number of moves possible in a game*/
const val MAX_MOVES = BOARD_DIM * BOARD_DIM * 2

sealed class Board(val positions: List<Position> = initPos, val turn: Player?, val currentMoves: Int = 0) {
    fun serialize(): String {
        val klassName = this::class.simpleName
        val lastPlayer = this.turn?.symbol
        val posStr = positions.joinToString("\n") { it.serialize() }
        return "$klassName\n$lastPlayer\n${this.currentMoves}\n$posStr"
    }
    abstract fun play(from: Square, to:Square, player: Player): Board
}

fun String.deserializeToBoard(): Board {
    val lines = this.split("\n")
    val kind = lines[0]
    val turn = lines[1]
    val currentMoves = lines[2].toInt()
    val positions = lines.drop(3).map { it.deserializeToPosition() }
    return when (kind) {
        BoardRun::class.simpleName -> BoardRun(positions, turn.toPlayer(), currentMoves)
        BoardDraw::class.simpleName -> BoardDraw(positions)
        BoardWinner::class.simpleName -> BoardWinner(positions, turn.toPlayer())
        else -> { throw IllegalArgumentException("There is no board type for input $kind")}
    }
}

class BoardDraw(pos: List<Position>): Board(pos, Player.WHITE) {
    override fun play(from: Square, to: Square, player: Player): Board {
        throw IllegalStateException("This game has already finished with a draw.")
    }
}

class BoardWinner(pos: List<Position>, val winner: Player?) : Board(pos, winner) {
    override fun play(from: Square, to:Square, player: Player): Board {
        throw IllegalStateException("The player $winner won this game.")
    }
}

class BoardRun(
    pos: List<Position> = initPos,
    turn: Player? = Player.WHITE,
    currentMoves: Int = 0
): Board(pos, turn, currentMoves) {
    override fun play(from: Square, to: Square, player: Player): Board {
        var auxPlayer = turn?.turn() /** Var used to not change the turn when needed */
        require(turn == player) { "Not your turn" }
        require(to.black) {"Invalid Move"}
        val fromPos = from.toPosition(this)
        val toPos = to.toPosition(this)
        require(fromPos.player == turn){ "Position $from does not have your piece" }
        require(toPos.player == null){ "Position $to is already occupied" }
        val newPos = movePiece(fromPos, toPos, this)
        /** Filter the positions removing the squares altered, so that we can add them posteriorly */
        val newList = positions.filter { it.square != newPos.first.square && it.square != newPos.second.square && it.square != newPos.third?.square }.toMutableList()
        newList.add(newPos.first)
        newList.add(newPos.second)
        var newBoard = this
        val moves = currentMoves + 1
        /** If newPos.third != null then a piece was captured, so we need to add the square of that piece, with the
                parameter sqr = null */
        if (newPos.third != null){
            newList += newPos.third!!
            auxPlayer = turn.turn()
            newBoard = BoardRun(newList, auxPlayer, moves)
            if (mandatoryCaptures(to.toPosition(newBoard), newBoard).any{it.first.square == to}) auxPlayer = turn
        }
        return when {
            checkWinner(newBoard) -> BoardWinner(newList, turn)
            checkDraw(newBoard) -> BoardDraw(newList)
            else -> BoardRun(newList, auxPlayer, moves)
        }
    }
    private fun checkWinner(board: Board): Boolean{
        val enemyPieces = board.positions.filter { it.player == this.turn?.turn() }
        return enemyPieces.isEmpty()
    }
    private fun checkDraw(board: Board): Boolean{
        val validPositions = board.positions.filter { it.player == this.turn?.turn() }
        var validMoves = 0
        validPositions.forEach { if (possibleMoves(it, board).isNotEmpty()) validMoves++ }
        return this.currentMoves + 1 == MAX_MOVES || validMoves == 0

    }
}
