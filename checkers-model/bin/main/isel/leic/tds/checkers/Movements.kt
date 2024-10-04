package isel.leic.tds.checkers

import kotlin.math.abs

/**
 * General function to move a piece
 * Calls other functions to verify certain requirements
 * @param from Position of the piece to be moved
 * @param to Position to where the piece is to be moved
 * @param board Board used in current game
 * @return Triple with positions that changed during the movement, the third element is null if no capture occurred
 * */
fun movePiece(from: Position, to: Position, board: Board): Triple<Position, Position, Position?> {
    val capture = mandatoryCaptures(from, board)
    require(capture.isEmpty() || capture.any {it == Pair(from, to)}) { "There's a mandatory capture at ${capture[0].first.square}" }
    require(if (from.king) validKingMove(from,to, board) else validPieceMove(from, to, board)) { "Invalid move" }
    var captured: Position? = null
    if (abs(to.square.column.index - from.square.column.index) >= 2) captured = capturePiece(from, to, board)
    var toPos = Position(to.square, from.player, from.symbol)
    val fromPos = Position(from.square, null)
    if (toPos.player == Player.WHITE && toPos.square.row.index == 0 || toPos.player == Player.BLACK && toPos.square.row.index == BOARD_DIM - 1){
        toPos = toPos.makeKing()
    }
    return Triple(fromPos, toPos, captured)
}

/**
 * Checks if the move is valid for a piece
 * @param from Position of the piece to be moved
 * @param to Position to where the piece is to be moved
 * @param board Board used in current game
 * @return Boolean that indicates wether the move is valid or not for a piece
 * */
fun validPieceMove(from: Position, to: Position, board: Board): Boolean{
    val rowDiff = from.square.row.index - to.square.row.index
    val colDiff = from.square.column.index - to.square.column.index
    if (from.player == Player.BLACK) {
        if (to.square.row.index == from.square.row.index + 1 && abs(to.square.column.index - from.square.column.index) == 1) return true
    } else {
        if (to.square.row.index == from.square.row.index - 1 && abs(to.square.column.index - from.square.column.index) == 1) return true
    }
    if (abs(to.square.column.index - from.square.column.index) == 2 && abs(rowDiff) == abs(colDiff)){
        val i = if (rowDiff < 0) -1 else 1
        val j = if (colDiff > 0) -1 else 1
        return "${from.square.row.number + i}${from.square.column.symbol + j}".toPosition(board)?.player == from.player?.turn()
    }
    return false
}

/**
 * Checks if the move is valid for a king
 * @param from Position of the piece to be moved
 * @param to Position to where the piece is to be moved
 * @param board Board used in current game
 * @return Boolean that indicates wether the move is valid or not for a king
 * */
fun validKingMove(from: Position, to: Position, board: Board): Boolean{
    val rowDiff = from.square.row.index - to.square.row.index
    val colDiff = from.square.column.index - to.square.column.index
    if (abs(rowDiff) == abs(colDiff)){
        if (abs(rowDiff) == 1) return true
        var i = if (rowDiff < 0) -1 else 1
        var j = if (colDiff > 0) -1 else 1
        while (abs(i) < abs(rowDiff) - 1){
            if ("${from.square.row.number + i}${from.square.column.symbol + j}".toPosition(board)?.player != null) return false
            i += if (rowDiff < 0) -1 else 1
            j += if (colDiff > 0) -1 else 1
        }
        if ("${from.square.row.number + i}${from.square.column.symbol + j}".toPosition(board)?.player == from.player)
            return false
        i += if (rowDiff < 0) -1 else 1
        j += if (colDiff > 0) -1 else 1
        val desirableSquare = "${from.square.row.number + i}${from.square.column.symbol + j}".toSquareOrNull()
        if(desirableSquare != to.square) return false
        return true
    }
    return false
}

/**
 * Captures a piece, if needed
 * @param from Position of the piece to be moved
 * @param to Position to where the piece is to be moved
 * @param board Board used in current game
 * @return Position to be captured, returns null if there's no piece to be captured
 * */
fun capturePiece(from: Position, to: Position, board: Board): Position? {
    var position: Position? = null
    when {
        from.square.row.index > to.square.row.index && from.square.column.index > to.square.column.index ->{
            position = board.positions.first{it.square == Square(to.square.row.index + 1, to.square.column.index + 1) }
        }
        from.square.row.index > to.square.row.index && from.square.column.index < to.square.column.index ->{
            position = board.positions.first{it.square == Square(to.square.row.index + 1, to.square.column.index - 1)}
        }
        from.square.row.index < to.square.row.index && from.square.column.index > to.square.column.index ->{
            position = board.positions.first{it.square == Square(to.square.row.index - 1, to.square.column.index + 1)}
        }
        from.square.row.index < to.square.row.index && from.square.column.index < to.square.column.index -> {
            position = board.positions.first{it.square == Square(to.square.row.index - 1, to.square.column.index - 1)}
        }
    }
    return  if(position?.player != null) Position(position.square, null)
            else null
}

/**
 * Checks mandatory captures on the board
 * @param from Position of the piece to be moved
 * @param board Board used in current game
 * @return MutableList<Pair<Position, Position>> the first element of the Pair being the position where a piece
 *          has a mandatory capture and the second element the position of the piece to be captured
 * */
fun mandatoryCaptures(from: Position, board: Board): MutableList<Pair<Position, Position>>{
    val validPositions = board.positions.filter {it.square.black && it.player == from.player}
    val possibleDiagonals = mutableListOf<Pair<Position, Position>>()
    validPositions.forEach{ pos->
        diagonal(pos, 1, -1, possibleDiagonals, board)
        diagonal(pos, 1, 1, possibleDiagonals, board)
        diagonal(pos, -1, 1, possibleDiagonals, board)
        diagonal(pos, -1, -1, possibleDiagonals, board)
    }
    return possibleDiagonals
}

/**
 * Checks if there's any piece in the chosen diagonal that's a mandatory capture for the chosen piece
 * @param pos Position chosen to check for mandatory captures
 * @param rowDiff Int storing the horizontal difference of the diagonal
 * @param colDiff Int storing vertical difference of the diagonal
 * @param list MutableList<Pair<Position, Position>>
 * @param board Board used in current game
 * */
fun diagonal(pos: Position, rowDiff:Int, colDiff:Int, list: MutableList<Pair<Position, Position>>, board: Board){
    var pos2 = "${pos.square.row.number+rowDiff}${pos.square.column.symbol+colDiff}".toPosition(board) ?: return
    if(pos.king == false){
        if (pos2.player != null && pos2.symbol?.lowercaseChar() == pos.player?.turn()?.symbol){
            val pos3 = "${pos2.square.row.number+rowDiff}${pos2.square.column.symbol+colDiff}".toPosition(board) ?: return
            if(pos3.player == null) list.add(Pair(pos, pos3))
        }
    } else{
        while (true){
            val pos3 = "${pos2.square.row.number + rowDiff}${pos2.square.column.symbol + colDiff}".toPosition(board) ?: return
            if (pos2.player != null && pos2.symbol?.lowercaseChar() != pos.symbol?.lowercaseChar()){
                if(pos3.player == null) {
                    list.add(Pair(pos, pos3))
                    return
                }
                else return
            }
            pos2 = pos3
        }
    }
}

/**
 * Verifies the possible moves, calling the appropriate function for the type of piece(piece or king)
 * @param pos Position selected by the player to check the possible moves
 * @param board Board used in current game
 * @return List<Pair<Position, Position>> where the first element of a Pair references the position selected by the
 *          player and the second element references a possible move
 * */
fun possibleMoves(pos: Position?, board: Board): List<Pair<Position, Position>> {
    if (pos == null) return emptyList()
    var positions = mandatoryCaptures(pos, board)
    if (positions.isNotEmpty()) {
        val temp = positions.filter { it.first.square == pos.square }
        /**Checking if mandatory moves are from the piece selected*/
        return temp.ifEmpty { emptyList() }
    }
    positions = if (pos.king) possibleKingMoves(pos, board)
                else possiblePieceMoves(pos, board)
    return positions
}

/**
 * Verifies the possible moves of a piece
 * @param pos Position selected by the player to check the possible moves
 * @param board Board used in the current game
 * @return MutableList<Pair<Position, Position>> where the first element of a Pair references the position selected by
 *          the player and the second element references a possible move
 * */
fun possiblePieceMoves(pos: Position, board: Board): MutableList<Pair<Position, Position>>{
    val positions = mutableListOf<Pair<Position, Position>>()
    val inc = if (pos.player == Player.BLACK) -1 else 1
    val pos1 = "${pos.square.row.number + inc}${pos.square.column.symbol + 1}".toPosition(board)
    if (pos1 != null && pos1.player == null) positions.add(Pair(pos, pos1))
    else if (pos1?.player == pos.player!!.turn()) {
        val pos2 = "${pos.square.row.number + (2 * inc)}${pos.square.column.symbol + 2}".toPosition(board)
        if (pos2 != null && pos2.player == null) positions.add(Pair(pos, pos2))
    }
    val pos3 = "${pos.square.row.number + inc}${pos.square.column.symbol - 1}".toPosition(board)
    if (pos3 != null && pos3.player == null) positions.add(Pair(pos, pos3))
    else if (pos3?.player == pos.player!!.turn()) {
        val pos4 = "${pos.square.row.number + (2 * inc)}${pos.square.column.symbol - 2}".toPosition(board)
        if (pos4 != null && pos4.player == null) positions.add(Pair(pos, pos4))
    }
    return positions
}

/**
 * Verifies the possible moves of a king
 * @param pos Position selected by the player to check the possible moves
 * @param board Board used in the current game
 * @return MutableList<Pair<Position, Position>> where the first element of a Pair references the position selected by the
 *          player and the second element references a possible move
 * */
fun possibleKingMoves(pos: Position, board: Board): MutableList<Pair<Position, Position>> {
    val validPositions = board.positions.filter { it.square.black }
    val possibleMoves = mutableListOf<Pair<Position, Position>>()
    for (p in validPositions){
        if(p.player == null){
            val colDiff = pos.square.column.index -p.square.column.index
            val rowDiff = pos.square.row.index - p.square.row.index
            if (abs(rowDiff) == abs(colDiff)){
                if (abs(rowDiff) == 1) possibleMoves.add(Pair(pos, p))
                var i = if (rowDiff < 0) -1 else 1
                var j = if (colDiff > 0) -1 else 1
                var add = true
                while (abs(i) <= abs(rowDiff) - 1){
                    val temp = "${pos.square.row.number + i}${pos.square.column.symbol + j}".toPosition(board)
                    if (temp?.player != null) add = false
                    i += if (rowDiff < 0) -1 else 1
                    j += if (colDiff > 0) -1 else 1
                }
                if (add) possibleMoves.add(Pair(pos, p))
            }
        }
    }
    return possibleMoves
}