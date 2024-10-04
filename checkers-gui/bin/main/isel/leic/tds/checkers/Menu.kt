package isel.leic.tds.checkers

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlin.system.exitProcess

/**
 * Draws the game's menu
 * @param game GameState of the game being drawn
 * */
@Composable
fun FrameWindowScope.CheckersMenu(game: GameState) {
    val (newGameDialog, setNewGameDialog) = remember { mutableStateOf(false) }
    if(newGameDialog)
        DialogInput({ name ->
            game.startGame(name)
            setNewGameDialog(false)
        }) {
            setNewGameDialog(false)
        }
    MenuBar {
        Menu("Checkers") {
            Item("New", onClick = { setNewGameDialog(true) })
            Item("Refresh", onClick = {game.refresh()}, enabled = game.board != null)
            Item("Exit", onClick = { exitProcess(0) })
        }
        Menu("Options"){
            CheckboxItem(
                "Show targets",
                checked = game.highlight.value,
                onCheckedChange = {game.highlight.value = it}
            )
            CheckboxItem(
                "Auto-Refresh",
                checked = game.autoRefresh.value,
                onCheckedChange = {game.autoRefresh.value = it}
            )
        }
    }
}

/**
 * Draws a textField and receives the game's name
 * @param onStart Lambda function that receives the name of the game
 * @param onCancel Lambda function that closes the textField
 * */
@Composable
fun DialogInput(onStart: (String)->Unit, onCancel: ()->Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "Insert a name for the game",
    state = DialogState( height = Dp.Unspecified, width = 350.dp)
) {
    val (name, setName) = remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(name, onValueChange = { setName(it) }, label = { Text("name") } )
        /** Group choice for the aesthetics of the board */
        require(name.length <= 5){"Name must have less than 5 characters"}
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (name.isNotBlank()) onStart(name)
            } ) { Text("Start")}
            Button(onClick = onCancel) { Text("Cancel")}
        }
    }
}