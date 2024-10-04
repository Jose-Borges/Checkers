package isel.leic.tds.checkers

class Square private constructor(var row: Row, var column: Column) {

    companion object {
        val values = (Row.values).flatMap {r -> Column.values.map { Square(r, it)} }
        operator fun invoke(r: Row, c: Column) = values.first {it.column == c && it.row == r}
        operator fun invoke(r: Int, c: Int) = values.first {r.indexToRow() == it.row && c.indexToColumn() == it.column}
    }
    override fun toString() = "${row.number}${column.symbol}"

    val black get()= (row.index % 2 != 0 && column.index % 2 == 0) || (row.index % 2 == 0 && column.index % 2 != 0)
}

fun String.toSquareOrNull() = Square.values.firstOrNull{it.toString() == this}
