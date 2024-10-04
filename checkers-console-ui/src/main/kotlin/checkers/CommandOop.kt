package checkers

import isel.leic.tds.checkers.*
import storage.Storage

interface CommandOop<T> {
    fun action(game: T?, args: List<String>) : T?
    fun show(game: T)
    val syntax : String
}

fun <T> readCommandsOop(cmds: Map<String, CommandOop<T>>) {
    var model: T? = null
    while(true) {
        print("> ")
        val input = readln()
        val words = input.trim().split(' ')
        val cmd = cmds[words[0].uppercase()]
        if(cmd == null) {
            println("Invalid command")
            continue
        }
        try {
            model = cmd.action(model, words.drop(1))
            if(model == null) break
            cmd.show(model)
        } catch (e: Exception) {
            println(e.message)
            println(cmd.syntax)
        }
    }
}

object CmdExitOop : CommandOop<Game> {
    override fun action(game: Game?, args: List<String>) = null
    override fun show(game: Game) {}
    override val syntax: String get() = "exit"
}

class CmdStartOop(private val storage: Storage<String, Board>) : CommandOop<Game> {
    override fun show(game: Game) = printBoard(game.board, game.player)
    override val syntax: String get() = "start <name>"
    override fun action(game: Game?, args: List<String>) = startGame(args[0], storage)
}

class CmdRefreshOop(private val storage: Storage<String, Board>) : CommandOop<Game> {
    override fun show(game: Game) = printBoard(game.board, game.player)
    override val syntax: String get() = "refresh"
    override fun action(game: Game?, args: List<String>): Game {
        require(game != null)
        val b = storage.load(game.name)
        require(b != null)
        return game.copy(board = b)
    }
}

class CmdPlayOop(private val storage: Storage<String, Board>) : CommandOop<Game> {
    override fun show(game: Game) = printBoard(game.board, game.player)
    override val syntax: String get() = "play <from> <to>"
    override fun action(game: Game?, args: List<String>): Game {
        require(game != null) {"You should start a game to initialize a Board before start playing"}
        return game.play(storage, args)
    }
}

fun printBoard(board: Board, player: Player){
    val sepLine = "  +---------------+"
    println("${sepLine}\t Turn=${board.turn?.symbol}")
    Row.values.forEach { r ->
        print("${r.number} |")
        Column.values.forEach { c ->
            val square = Square(r, c)
            when(square.black){
                true -> if (c.symbol == 'h') print("${square.toPosition(board).symbol ?: "-"}") else print(square.toPosition(board).symbol?.plus(" ") ?: "- ")
                false -> if (c.symbol == 'h') print(" ") else print("  ")
            }
        }
        if(r.number == 8) println("|\t Player = $player")
        else println("|")
    }
    println(sepLine)
    print("   ")
    Column.values.forEach {
        print("${it.symbol} ")
    }
    println()
    if(board is BoardWinner)
        board.winner.apply{ println("Player ${board.winner} wins.")}
}