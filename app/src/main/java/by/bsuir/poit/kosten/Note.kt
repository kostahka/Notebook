package by.bsuir.poit.kosten

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(@PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var info: String = "",
    var isFile: Boolean = false){
    fun toBytes():ByteArray{
        return ("$id\n$title\n${date.time}\n${info.lines().size}\n$info\n$isFile").toByteArray()
    }
}