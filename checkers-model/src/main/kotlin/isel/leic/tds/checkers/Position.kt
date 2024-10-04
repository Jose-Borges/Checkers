package isel.leic.tds.checkers

/** Contains all the initial positions of a board*/
val initPos = Square.values.filter { it.black }.map {
    if (it.row.index <= 2) Position(it, Player.BLACK)
    else if (it.row.index >= 5) Position(it, Player.WHITE)
    else Position(it, null)
}

/**
 * Associates a Square with the correspondent Player, or with null if there isn't one
 * @param square Square of the board
 * @param player Player assigned to [square]
 * @param symbol Char corresponding to the [player] symbol, turning to uppercase according to the [king] property
 * @property king Boolean val getter that checks if the piece in this Position is a king or not
 * */
class Position(val square: Square, val player: Player?, val symbol: Char? = player?.symbol) {
    val king get() = symbol?.isUpperCase() ?: false
    /** Returns the position as a String in an understandable way */
    fun serialize() = "${square.row.number}${square.column.symbol};$symbol"
}

/**
 * Turns the king property as true in the given [Position]
 * @receiver Position to change its symbol parameter
 * @return Position with the symbol parameter changed to uppercase
 * */
fun Position.makeKing() = Position(this.square, this.player, this.symbol?.uppercaseChar())

/**
 * Searches the Board positions for the position with the square passed as a receiver
 * @receiver Square to search for in the positions of the board
 * @param board Board to search for the square
 * @return Position containing a square equals to the one passed as a receiver
 * */
fun Square.toPosition(board: Board) = board.positions.first{ it.square == this}

/**
 * Searches the Board positions for the position with the square passed in the receiver as a String
 * @receiver String containing the board to search
 * @param board Board to search for the square
 * @return Position containing a square equals to the one passed in the receiver as a String
 * */
fun String.toPosition(board: Board) = this.toSquareOrNull()?.toPosition(board)

/**
 * Turns a String into a Position
 * @receiver String to turn into a Position
 * @return Position with the parameters passed in the receiver
 * */
fun String.deserializeToPosition() : Position {
    val sqr = "${this[0]}${this[1]}".toSquareOrNull()
    require(sqr != null){ "No valid square with passed parameters ${this[0]}${this[1]}" }
    var pos = Position(sqr, this[3].toString().toPlayer())
    if(this[3].isUpperCase()) pos = pos.makeKing()
    return pos
}