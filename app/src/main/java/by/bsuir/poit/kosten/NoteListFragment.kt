package by.bsuir.poit.kosten

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.UUID

private const val COLUMNS = 2
private const val DATE_FORMAT = "EEE, MMM, dd, hh:mm:ss"

class NoteListFragment : Fragment() {

    private lateinit var noteRecyclerView:RecyclerView
    private var adapter: NoteAdapter? = NoteAdapter(emptyList())

    interface Callbacks{
        fun onNoteSelected(noteId:UUID)
    }

    private var callbacks: Callbacks? = null

    private val noteListViewModel: NoteListViewModel by lazy {
        ViewModelProvider(this).get(NoteListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()

        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_note_list, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_note ->{
                val note = Note()
                noteListViewModel.addNote(note)
                callbacks?.onNoteSelected(note.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)

        noteRecyclerView = view.findViewById(R.id.note_recycler_view)
        noteRecyclerView.layoutManager = GridLayoutManager(context, COLUMNS)

        noteRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteListViewModel.noteListLiveData.observe(
            viewLifecycleOwner,
            Observer { pair : Pair<List<Note>, List<Note>> ->
                val dbNotes = pair.first
                val fileNotes = pair.second
                updateUI(dbNotes.plus(fileNotes))
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NoteListFragment().apply {

            }
    }

    private fun updateUI(notes: List<Note>){
        adapter = NoteAdapter(notes)
        noteRecyclerView.adapter = adapter
    }

    private inner class NoteHolder(view: View):ViewHolder(view),OnClickListener{
        val titleTextView: TextView = itemView.findViewById(R.id.note_title)
        val dateCharsTextView: TextView = itemView.findViewById(R.id.note_date_chars)
        val infoTextView: TextView = itemView.findViewById(R.id.note_info)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        private lateinit var note: Note

        init{
            itemView.setOnClickListener(this)
            buttonDelete.setOnClickListener {
                noteListViewModel.deleteNote(note)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(note: Note){
            this.note = note
            titleTextView.text = note.title
            dateCharsTextView.text = DateFormat.format(DATE_FORMAT, note.date).toString()
            val info = note.info.replace("\n", " ").replace("\r", " ")
            if(info.length > 34)
                infoTextView.text = info.substring(0, 33) + "..."
            else
                infoTextView.text = info
        }

        override fun onClick(v: View?) {
            callbacks?.onNoteSelected(note.id)
        }
    }

    private inner class NoteAdapter(var notes: List<Note>):RecyclerView.Adapter<NoteHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
            val view = layoutInflater.inflate(R.layout.list_item_note, parent, false)
            return NoteHolder(view)
        }

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            val note = notes[position]

            holder.bind(note)
        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }
}