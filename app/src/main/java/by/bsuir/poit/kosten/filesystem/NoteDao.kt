package by.bsuir.poit.kosten.filesystem

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.provider.ContactsContract.Directory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.bsuir.poit.kosten.Note
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Executors

private const val TAG = "FILESYSTEM-NOTEDAO"

class NoteDao private constructor(var context: Context) {

    private val executor = Executors.newSingleThreadExecutor()
    private val notesData = MutableLiveData<List<Note>>()
    fun getNotes(): LiveData<List<Note>>
    {
        val fList = context.getExternalFilesDir(null)?.listFiles()
        executor.execute {
            val notes = MutableList<Note>(0) { _: Int -> Note() }
            fList?.forEach { file ->
                var fin: FileInputStream? = null
                try {
                    fin = FileInputStream(file)
                    val note = readNote(fin)?:Note()
                    notes.add(note)
                }catch (ex:IOException){
                    Log.e(TAG, ex.message?:"")
                }finally {
                    try{
                        fin?.close();
                    }
                    catch(ex:IOException){
                        Log.e(TAG, ex.message?:"")
                    }
                }
            }
            notesData.postValue(notes)
        }
        return notesData
    }
    fun getNote(id: UUID): LiveData<Note?>
    {
        val noteData = MutableLiveData<Note?>()
        val fList = context.getExternalFilesDir(null)?.listFiles()
        executor.execute {
            fList?.forEach { file ->
                var fin: FileInputStream? = null
                try {
                    fin = FileInputStream(file)
                    val note = readNote(fin)?:Note()
                    if(note.id == id){
                        noteData.postValue(note)
                        return@execute
                    }
                }catch (ex:IOException){
                    Log.e(TAG, ex.message?:"")
                }finally {
                    try{
                        fin?.close();
                    }
                    catch(ex:IOException){
                        Log.e(TAG, ex.message?:"")
                    }
                }
            }
        }
        return noteData
    }

    private fun getExternalPath(note: Note):File{
        return File(context.getExternalFilesDir(null), note.id.toString() + ".txt")
    }

    fun updateNote(note: Note)
    {
        val notes = notesData.value!!.toMutableList()
        notes[notes.indexOfFirst { n -> n.id == note.id }] = note
        notesData.postValue(notes)
        writeNote(note)
    }

    fun insertNote(note: Note)
    {
        notesData.postValue(notesData.value!!.plus(note))
        writeNote(note)
    }

    fun deleteNote(note: Note)
    {
        val notes = notesData.value!!.toMutableList()
        notes.removeAt(notes.indexOfFirst { n -> n.id == note.id })
        notesData.postValue(notes)
        try {
            getExternalPath(note).delete()
        }catch (ex:IOException){
            Log.e(TAG, ex.message?:"")
        }

    }

    private fun writeNote(note: Note){
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(getExternalPath(note))
            fos.write(note.toBytes())
        }catch (ex: IOException){
            Log.e(TAG, ex.message?:"")
        }finally {
            fos?.close()
        }
    }

    private fun readNote(fin:FileInputStream?):Note?{
        var note:Note? = null
        var sIn :Scanner? = null
        try{
            sIn = Scanner(fin)
            var tokens = arrayOf("","","","","")
            var i = 0;
            var lines = 0;
            while(sIn.hasNextLine()){
                if(i == 3){
                    var lines = sIn.nextLine().toInt()
                    while(lines > 0 && sIn.hasNextLine()){
                        tokens[i] += sIn.nextLine()
                        lines--
                    }
                }
                else
                    tokens[i] = sIn.nextLine()
                i++
            }
            note = Note( NoteTypeConverters.toUUID(tokens[0])?:UUID.randomUUID(),
                tokens[1],
                NoteTypeConverters.toDate(tokens[2])?:Date(),
                tokens[3],
                tokens[4].toBoolean())
        }catch (ex: IOException){
            Log.e(TAG, ex.message?:"")
        }finally {
            try{
                sIn?.close()
            }
            catch(ex:IOException){
                Log.e(TAG, ex.message?:"")
            }
        }

        return note
    }

    companion object{
        private var INSTANCE: NoteDao? = null
        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = NoteDao(context)
            }
        }
        fun get(): NoteDao {
            return INSTANCE ?:
            throw IllegalStateException("NoteDao must be initialized")
        }
    }
}