package isel.leic.tds.checkers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import storage.StringSerializer

@Composable
fun initGame(){
    Box(
        modifier = Modifier.size((9* CELL_SIZE).dp, (9* CELL_SIZE).dp),
        contentAlignment = Alignment.Center
    ){
        Text("You must start a game first", fontStyle = FontStyle(1))
    }
}

object BoardSerializer : StringSerializer<Board> {
    override fun write(obj: Board) = obj.serialize()
    override fun parse(input: String) = input.deserializeToBoard()
}

/**
 * Draws the board given as a parameter
 * @param board Board used in the current game
 * @param game Game to be drawn
 * @param onCellClick Lambda function that receives two Positions as parameters
 * */
@Composable
fun BoardView(board: Board?, game: GameState, onCellClick: (Position, Position) -> Unit){
    val from: MutableState<Position?> = remember { mutableStateOf(null) }
    val to: MutableState<Position?> = remember { mutableStateOf(null) }
    val color = Color(204, 172, 149)
    Column {
        Row{
            Text("Game = ${game.name}",
                modifier = Modifier.size((CELL_SIZE*2).dp, (CELL_SIZE/2).dp).background(color),
                fontSize = 1.5.em,
                textAlign = TextAlign.Center,
                lineHeight = (CELL_SIZE*0.4).sp
            )
            Text(
                "Player = ${game.player}             Turn = ${board?.turn}",
                modifier = Modifier.size((CELL_SIZE*7).dp, (CELL_SIZE/2).dp).background(color),
                fontSize = 1.5.em,
                textAlign = TextAlign.Center,
                lineHeight = (CELL_SIZE*0.4).sp
            )
        }
        repeat(BOARD_DIM) { line ->
            Row {
                val p = if (game.player == Player.WHITE) BOARD_DIM - line else line + 1
                Text(
                    p.toString(), modifier = Modifier.size((CELL_SIZE/2).dp, CELL_SIZE.dp).background(color),
                    fontSize = 2.em,
                    textAlign = TextAlign.Center,
                    lineHeight = (CELL_SIZE*0.8).sp
                )
                repeat(BOARD_DIM) { col ->
                    val l = if(game.player == Player.WHITE) line else BOARD_DIM - line - 1
                    val position = board?.positions?.find { it.square == Square(l, col) }
                    val symbol = position?.symbol ?: ' '
                    val moves = if (game.highlight.value) possibleMoves (from.value, board!!) else emptyList()
                    Cell(symbol.toString(), Square(l, col), moves, from.value) { sqr ->
                        if (board != null && board.turn == game.player) {
                            if (from.value == null) {
                                if (sqr.toPosition(board).player == game.player) {
                                    from.value = sqr.toPosition(board)
                                }
                            } else if (to.value == null) {
                                if (sqr.toPosition(board).player == game.player) {
                                    from.value = sqr.toPosition(board)
                                } else {
                                    to.value = sqr.toPosition(board)
                                    val fromPos = from.value
                                    val toPos = to.value
                                    require(fromPos != null && toPos != null)
                                    onCellClick(fromPos, toPos)
                                    from.value = null
                                    to.value = null
                                }
                            }
                        }
                    }
                }
                Text(
                    "",
                    modifier = Modifier.size((CELL_SIZE/2).dp, CELL_SIZE.dp).background(color),
                    fontSize = 0.65.em,
                    textAlign = TextAlign.Left,
                    lineHeight = 25.sp
                )
            }
        }
    }
    DrawSymbols()
}

/** Draws the letter coordinates of the board */
@Composable
fun DrawSymbols(){
    val color = Color(204, 172, 149)
    Row{
        Text(
            "",
            modifier = Modifier.size((CELL_SIZE/2).dp, (CELL_SIZE/2).dp).background(color),
            fontSize = 0.65.em,
            textAlign = TextAlign.Left,
            lineHeight = 25.sp
        )
        repeat(BOARD_DIM) {line ->
            Text(
                "${Column.values[line].symbol}",
                modifier = Modifier.size(CELL_SIZE.dp, (CELL_SIZE/2).dp).background(color),
                fontSize = 2.em,
                textAlign = TextAlign.Center,
                lineHeight = (CELL_SIZE*0.4).sp
            )
        }
        Text(
            "",
            modifier = Modifier.size((CELL_SIZE/2).dp, (CELL_SIZE/2).dp).background(color),
            fontSize = 0.65.em,
            textAlign = TextAlign.Left,
            lineHeight = 25.sp
        )
    }
}

/**
 * Draws a cell according to a Position and it's properties
 * @param symbol String containing the Position's player
 * @param square Square to be drawn
 * @param moves List<Pair<Position,Position>> that in the first parameter of the Pair contains the position of the piece
 *                  that can be moved and in the second parameter, the position to where it can be moved, if possible
 * @param from Position selected by the player
 * @param onCellClick Lambda function that receives a Square as a parameter
 * */
@Composable
fun Cell(symbol: String, square: Square, moves: List<Pair<Position,Position>>, from: Position?, onCellClick: (Square) -> Unit){
    val color = if(moves.any{it.second.square == square}) Color(204, 73, 2)
    else Color(102, 51, 0)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if(square.black)
            Modifier
                .size(CELL_SIZE.dp)
                .background(color)
                .clickable(onClick = { onCellClick(square) })
                .border(4.dp, if(from?.square == square) Color(255, 129, 3)/*Color(255, 255,0)*/ else color)
        else
            Modifier
                .size(CELL_SIZE.dp)
                .background(Color(255, 204, 153))
    ){
        if (square.black && symbol != " "){
            val name = when(symbol){
                "w" -> "white.png"
                "b" -> "black.png"
                "W" -> "white_q.png"
                else -> "black_q.png"
            }
            Image(
                painter = painterResource(name),
                contentDescription = name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Draws a Dialog Window with the given [msg]
 * @param msg Message to be shown in the Dialog Window
 * @param onClose Lambda function that closes the Dialog Window
 * */
@Composable
fun DialogMessage(msg: String?, onClose: () -> Unit) {
    if (msg != null)
        Dialog(
            title = "",
            onCloseRequest = onClose,
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize.Unspecified
            )
        ) {
            Text(msg)
        }
}
