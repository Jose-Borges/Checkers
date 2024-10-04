package isel.leic.tds.checkers

const val ASCII_INC = 96

class Column private constructor(val symbol: Char) {

    companion object{
        operator fun invoke(symbol: Char) = symbol.toColumnOrNull()
        val values = (1 .. BOARD_DIM).map{Column((it + ASCII_INC).toChar())}
    }

    val index get() = values.indexOf(this)
}

fun Char.toColumnOrNull() = Column.values.firstOrNull{it.symbol == this}

fun Int.indexToColumn() = Column.values.elementAt(this)