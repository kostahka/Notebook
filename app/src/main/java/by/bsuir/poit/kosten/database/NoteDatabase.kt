package by.bsuir.poit.kosten.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.bsuir.poit.kosten.Note

@Database(entities = [ Note::class], version = 1)
@TypeConverters(NoteTypeConverters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao() : NoteDao
}