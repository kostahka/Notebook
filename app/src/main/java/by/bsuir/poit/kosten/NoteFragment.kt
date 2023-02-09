package by.bsuir.poit.kosten

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

private const val ARG_NOTE_ID = "note_id"
private const val DATE_FORMAT = "EEE, MMM, dd, hh:mm:ss"

class NoteFragment : Fragment() {

    private lateinit var note: Note
    private var isFileNote: Boolean = false

    private lateinit var titleField: EditText
    private lateinit var infoField: EditText
    private lateinit var dateTextView: TextView
    private lateinit var isFileCheckBox: CheckBox

    private val noteDetailViewModel: NoteDetailViewModel by lazy {
        ViewModelProvider(this).get(NoteDetailViewModel ::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteId: UUID = arguments?.getSerializable(ARG_NOTE_ID) as UUID

        noteDetailViewModel.loadNote(noteId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        titleField = view.findViewById(R.id.note_name)
        infoField = view.findViewById(R.id.note_info)
        dateTextView = view.findViewById(R.id.note_date_chars)
        isFileCheckBox = view.findViewById(R.id.note_in_file)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteDetailViewModel.noteLiveData.observe(
            viewLifecycleOwner,
            Observer { note ->
                note?.let {
                    this.note = note
                    isFileNote = note.isFile
                    updateUI()
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        noteDetailViewModel.saveNote(note, isFileNote)
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                note.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        val infoWatcher = object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                note.info = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        titleField.addTextChangedListener(titleWatcher)
        infoField.addTextChangedListener(infoWatcher)
        infoField.setHorizontallyScrolling(false)
        isFileCheckBox.setOnClickListener { _->
            note.isFile = !note.isFile
        }
    }

    private fun updateUI(){
        titleField.setText(note.title)
        infoField.setText(note.info)
        dateTextView.text = DateFormat.format(DATE_FORMAT, note.date).toString()
        isFileCheckBox.isChecked = note.isFile
    }

    companion object {
        @JvmStatic
        fun newInstance(id:UUID):NoteFragment{
            val args = Bundle().apply{
                putSerializable(ARG_NOTE_ID, id)
            }
            return NoteFragment().apply {
                arguments = args
            }
        }
    }
}