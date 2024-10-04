package isel.leic.tds.checkers

import storage.*

/**
 * data class GameAsync contains the necessary information of a game
 * @property name Name of the game
 * @property board Board of the game
 * @property player Player that is currently playing
 * */
data class GameAsync(
    val name: String,
    val board: Board = BoardRun(),
    val player: Player = Player.WHITE
)

/**
 * Starts a game
 * @param name Name of the game to be created, if it doesn't exist already
 * @param storage Storage used to store the game
 * @return GameAsync with the desired name and correspondent board (standard board if it's a new game)
 * */
suspend fun startGame(name: String, storage: StorageAsync<String, Board>) : GameAsync {
    val board = storage.load(name) ?: return GameAsync(name, storage.new(name), Player.WHITE)
    if(board.currentMoves <= 1) return GameAsync(name, board, Player.BLACK)
    return GameAsync(name, storage.new(name), Player.WHITE)
}

/**
 * Executes a play in an async game
 * @param storage Storage used to store the game
 * @param from Position of the piece to be moved
 * @param to Position to where the piece is to be moved
 * @return GameAsync containing the new board after the play
 * */
suspend fun GameAsync.play(storage: StorageAsync<String, Board>, from: Square, to: Square) : GameAsync {
    val board = board.play(from,to, this.player)
    storage.save(name, board)
    return this.copy(board = board)
}
