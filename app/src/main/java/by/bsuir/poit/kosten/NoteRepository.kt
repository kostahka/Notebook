package by.bsuir.poit.kosten

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.Database
import androidx.room.Room
import by.bsuir.poit.kosten.database.NoteDao
import by.bsuir.poit.kosten.database.NoteDatabase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "notedatabase"

class NoteRepository private constructor(context: Context){
    private val database : NoteDatabase = Room.databaseBuilder(
        context.applicationContext,
        NoteDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val noteDao = database.noteDao()
    private val noteDaoFile = by.bsuir.poit.kosten.filesystem.NoteDao.get()

    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): MediatorLiveData<Pair<List<Note>, List<Note>>>
    {
        val dbNotes = noteDao.getNotes()
        val fileNotes = noteDaoFile.getNotes()

        val medNotes = MediatorLiveData<Pair<List<Note>, List<Note>>>()
        medNotes.addSource(dbNotes){
            if(medNotes.value == null)
                medNotes.value = Pair(List<Note>(0) { _ -> Note () },
                    List<Note>(0) { _ -> Note () })

            medNotes.value = Pair(it, medNotes.value!!.second)
        }
        medNotes.addSource(fileNotes){
            if(medNotes.value == null)
                medNotes.value = Pair(List<Note>(0) { _ -> Note () },
                    List<Note>(0) { _ -> Note () })

            medNotes.value = Pair(medNotes.value!!.first, it)
        }

        return medNotes
    }
    fun getCrime(id: UUID): MediatorLiveData<Note?>{
        val dbNote = noteDao.getNote(id)
        val fileNote = noteDaoFile.getNote(id)

        val medNotes = MediatorLiveData<Note?>()
        medNotes.addSource(dbNote){
            if(it != null)
                medNotes.value = it
        }
        medNotes.addSource(fileNote){
            if(it != null)
                medNotes.value = it
        }

        return medNotes
    }

    fun updateNote(note: Note, isFile:Boolean){
        executor.execute {
            if(note.isFile){
                if(!isFile)
                {
                    noteDao.deleteNote(note)
                    noteDaoFile.insertNote(note)
                }
                else
                    noteDaoFile.updateNote(note)
            }else{
                if(isFile){
                    noteDaoFile.deleteNote(note)
                    noteDao.insertNote(note)
                }else
                    noteDao.updateNote(note)
            }
        }
    }

    fun insertNote(note: Note){
        executor.execute {
            if(note.isFile)
                noteDaoFile.insertNote(note)
            else
                noteDao.insertNote(note)
        }
    }

    fun deleteNote(note: Note){
        executor.execute {
            if(note.isFile)
                noteDaoFile.deleteNote(note)
            else
                noteDao.deleteNote(note)
        }
    }

    companion object{
        private var INSTANCE: NoteRepository? = null
        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = NoteRepository(context)
            }
        }
        fun get(): NoteRepository {
            return INSTANCE ?:
            throw IllegalStateException("NoteRepository must be initialized")
        }
    }
}