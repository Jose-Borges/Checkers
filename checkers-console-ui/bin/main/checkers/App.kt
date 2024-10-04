package checkers

import isel.leic.tds.checkers.*
import storage.*

import org.litote.kmongo.KMongo
import pt.isel.MongoStorage

fun main(){

    val serializer = object : StringSerializer<Board>{
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) = input.deserializeToBoard()
    }

    //Local storage
    //val fs = FileStorage<String, Board>("Output", serializer){ BoardRun() }

    //DB storage
    val connectionString = "mongodb+srv://G-05:fmEu3WflGh42otui@cluster0.hkjjzxn.mongodb.net/?retryWrites=true&w=majority"
    val db = KMongo.createClient(connectionString).getDatabase("checkers")
    val fs = MongoStorage("TDS", db, serializer) { BoardRun() }

    readCommandsOop(mapOf(
        "EXIT" to CmdExitOop,
        "PLAY" to CmdPlayOop(fs),
        "START" to CmdStartOop(fs),
        "REFRESH" to CmdRefreshOop(fs)
    ))
}

