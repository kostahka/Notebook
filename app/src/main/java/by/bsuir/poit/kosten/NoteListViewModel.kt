package by.bsuir.poit.kosten

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

class NoteListViewModel : ViewModel() {
    private val noteRepository = NoteRepository.get()
    val noteListLiveData : MediatorLiveData<Pair<List<Note>, List<Note>>> = noteRepository.getCrimes()

    fun addNote(note: Note){
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note){
        noteRepository.deleteNote(note)
    }
}