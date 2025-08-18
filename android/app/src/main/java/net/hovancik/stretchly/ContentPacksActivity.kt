package net.hovancik.stretchly

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContentPacksActivity : AppCompatActivity() {

    private lateinit var packsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_packs)

        initializeViews()
        loadContentPacks()
    }

    private fun initializeViews() {
        packsRecyclerView = findViewById(R.id.packsRecyclerView)
        packsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadContentPacks() {
        val allPacks = ContentPackManager.getAllPacks()
        val adapter = ContentPackAdapter(allPacks) { packId, enabled ->
            ContentPackManager.enablePack(packId, enabled)
        }
        packsRecyclerView.adapter = adapter
    }
}

class ContentPackAdapter(
    private val packs: List<ContentPack>,
    private val onPackToggled: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ContentPackAdapter.ContentPackViewHolder>() {

    class ContentPackViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.packNameTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.packDescriptionTextView)
        val enableSwitch: Switch = view.findViewById(R.id.packEnableSwitch)
        val statsTextView: TextView = view.findViewById(R.id.packStatsTextView)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ContentPackViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content_pack, parent, false)
        return ContentPackViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentPackViewHolder, position: Int) {
        val pack = packs[position]
        
        holder.nameTextView.text = pack.name
        holder.descriptionTextView.text = pack.description
        
        val statsText = """
            Microbreak Ideas: ${pack.microbreakIdeas.size}
            Break Ideas: ${pack.breakIdeas.size}
            Guided Routines: ${pack.guidedRoutines.size}
        """.trimIndent()
        holder.statsTextView.text = statsText
        
        holder.enableSwitch.isChecked = pack.isEnabled
        holder.enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            onPackToggled(pack.id, isChecked)
        }
    }

    override fun getItemCount() = packs.size
}
