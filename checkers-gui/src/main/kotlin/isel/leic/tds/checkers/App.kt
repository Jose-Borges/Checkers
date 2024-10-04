package isel.leic.tds.checkers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import storage.*
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

const val CELL_SIZE = 80
fun main() = application {
    //DB Storage
    val connectionString = "mongodb+srv://G-05:fmEu3WflGh42otui@cluster0.hkjjzxn.mongodb.net/?retryWrites=true&w=majority"
    val db = KMongo.createClient(connectionString).coroutine.getDatabase("checkers")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Checkers",
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize.Unspecified
        )
    ) {
        val scope = rememberCoroutineScope()
        val game = remember {
            GameState(
                scope,
                //FileStorageAsync("Output", BoardSerializer) { BoardRun() } //Local Storage
                MongoStorageAsync("TDS", db, BoardSerializer){BoardRun()} //DB Storage
            )
        }
        CheckersMenu(game)
        Column {
            if(game.board == null) initGame()
            else{
                BoardView(game.board, game, game::play)
                DialogMessage(game.message, game::dismissMessage)
            }
        }
    }
}

