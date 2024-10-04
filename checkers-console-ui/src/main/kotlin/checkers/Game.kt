package checkers

import isel.leic.tds.checkers.*
import storage.Storage

/**
 * data class Game contains the necessary information of a game
 * @property name Name of the game
 * @property board Board of the game
 * @property player Player that is currently playing
 * */
data class Game(
    val name: String,
    val board: Board = BoardRun(),
    val player: Player = Player.WHITE
)

/**
 * Starts a game
 * @param name Name of the game to be created, if it doesn't exist already
 * @param storage Storage used to store the game
 * @return Game with the desired name and correspondent board (standard board if it's a new game)
 * */
fun startGame(name: String, storage: Storage<String, Board>) : Game {
    val board = storage.load(name) ?: return Game(name, storage.new(name), Player.WHITE)
    if(board.currentMoves <= 1) return Game(name, board, Player.BLACK)
    storage.delete(name)
    return Game(name, storage.new(name), Player.WHITE)
}

/**
 * Performs a play
 * @param storage Storage used to store the game
 * @param args Arguments introduced by the user containing the desired squares to perform a play
 * @return Game containing the new board after the play
 * */
fun Game.play(storage: Storage<String, Board>, args: List<String>) : Game {
    require(args.isNotEmpty()){ "No arguments were passed" }
    val from = args[0].toSquareOrNull()
    require(from != null && from.black){ "Invalid square ${args[0]}" }
    val to = args[1].toSquareOrNull()
    require(to != null && to.black){ "Invalid square ${args[1]}" }
    return board.play(from,to, this.player).also { storage.save(name,it) }.let { copy(board = it) }
}
