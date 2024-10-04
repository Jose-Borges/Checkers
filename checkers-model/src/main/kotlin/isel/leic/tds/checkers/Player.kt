package isel.leic.tds.checkers

/**
 * Contains the two possible players
 * @param symbol Char corresponding to the symbol associated with the player
 * */
enum class Player(val symbol: Char){
    BLACK('b'), WHITE('w');
    /** Returns the opposite player to the one passed as a receiver*/
    fun turn() = if(this == WHITE) BLACK else WHITE
}

/** Returns a player with a symbol correspondent to the string passed as a receiver */
fun String.toPlayer(): Player? {
    return Player.values().find { it.symbol == this.lowercase()[0] }
}