package by.bsuir.poit.kosten

import android.app.Application
import by.bsuir.poit.kosten.filesystem.NoteDao

class NotesIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NoteDao.initialize(this)
        NoteRepository.initialize(this)
    }
}