package isel.leic.tds.checkers

class Row private constructor(val number: Int) {

    companion object {
        operator fun invoke(number: Int) = number.toRowOrNull()
        val values = (BOARD_DIM downTo 1).map { Row(it) }
    }

    val index get() = values.indexOf(this)
}

fun Int.toRowOrNull() = Row.values.firstOrNull{it.number == this}

fun Int.indexToRow() = Row.values.elementAt(this)