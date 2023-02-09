package by.bsuir.poit.kosten.database

import androidx.lifecycle.LiveData
import androidx.room.*
import by.bsuir.poit.kosten.Note
import java.util.UUID

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getNotes(): LiveData<List<Note>>
    @Query("SELECT * FROM note WHERE id=(:id)")
    fun getNote(id: UUID): LiveData<Note?>

    @Update
    fun updateNote(note: Note)

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}