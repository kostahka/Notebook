package by.bsuir.poit.kosten

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class NoteDetailViewModel() : ViewModel() {
    private val noteRepository = NoteRepository.get()
    private val noteIdLiveData = MutableLiveData<UUID>()

    var noteLiveData: LiveData<Note?> = Transformations.switchMap(noteIdLiveData){
        noteId ->
        noteRepository.getCrime(noteId)
    }

    fun loadNote(noteId: UUID){
        noteIdLiveData.value = noteId
    }

    fun saveNote(note: Note, isFile:Boolean){
        noteRepository.updateNote(note, isFile)
    }
}