package by.bsuir.poit.kosten.filesystem

import androidx.room.TypeConverter
import java.util.*

class NoteTypeConverters {
    companion object{
        fun toDate(time: String?): Date?{
            return time?.let{
                Date(it.toLong())
            }
        }

        fun toUUID(uuid: String?): UUID? {
            return UUID.fromString(uuid)
        }
    }

}