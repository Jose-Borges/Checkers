package isel.leic.tds.checkers

import storage.*

import androidx.compose.runtime.*
import kotlinx.coroutines.*

/**
 * class GameState contains the necessary information of a game
 * @param scope Coroutine used for the game
 * @param storage Storage used to store the game
 * */
class GameState(private val scope: CoroutineScope, private val storage: StorageAsync<String, Board>) {
    private var jobRefresh: Job? = null
    private val gameState: MutableState<GameAsync?> = mutableStateOf(null)
    private val messageState: MutableState<String?> = mutableStateOf(null)

    /**Boolean variable to check if option Auto refresh is active */
    val autoRefresh = mutableStateOf(true)
    /** Boolean variable to check if option Highlight is active */
    val highlight = mutableStateOf(true)

    /** Public variables available from extern functions */
    val board get() = gameState.value?.board
    val player get() = gameState.value?.player
    val message get() = messageState.value
    val name get() = gameState.value?.name

    /**
     * Starts a game
     * @param name Name of the game
     * */
    fun startGame(name: String) {
        scope.launch {
            gameState.value = startGame(name, storage)
            jobRefresh = scope.launch {
                val (game, setGame) = gameState
                if (game != null) {
                    while (true) {
                        delay(500)
                        val newBoard = storage.load(game.name)
                        if (newBoard != null && newBoard.positions != game.board.positions && autoRefresh.value){
                            setGame(game.copy(board = newBoard))
                        }
                    }
                }
            }
        }
    }

    /**Refreshes the visual position of the pieces in the game*/
    fun refresh(){
        jobRefresh = scope.launch {
            val (game, setGame) = gameState
            val newBoard = game?.let{ storage.load(it.name)}
            setGame(newBoard?.let { game.copy(board = it) })
        }
    }

    fun dismissMessage() {
        messageState.value = null
    }

    /**
     * Performs a play
     * @param from Position of the piece to be moved
     * @param to Position to where the piece is to be moved
     * */
    fun play(from: Position, to: Position) {
        val (game, setGame) = gameState
        if(game == null) {
            messageState.value = "You should start a new Game before playing!"
            return
        }
        scope.launch {
            try {
                val newGame = game.play(storage, from.square, to.square)
                setGame(newGame)
                messageState.value = boardMessage(newGame.board)
            } catch (ex: Exception) {
                messageState.value = ex.message
                val newBoard = storage.load(game.name)
                if(newBoard != null && newBoard.positions != game.board.positions)
                    setGame(game.copy(board = newBoard))
            }
        }
    }

    /**
     * Shows a message with the result of the game when it ends
     * @param board Board of the game
     * @return String of the message to be shown or null if there is no message to show
     * */
    private fun boardMessage(board: Board): String? {
        return when(board) {
            is BoardWinner -> "Game finished with winner ${board.winner}"
            is BoardDraw -> "Game finished with a draw!"
            is BoardRun -> null
        }
    }
}
