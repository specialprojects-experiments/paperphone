package com.withgoogle.experiments.unplugged.ui.paperapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.ui.adapters.BindableAdapter
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.util.bindView

class PaperAppList: AppCompatActivity() {
    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val moduleView by bindView<ModuleView>(R.id.module)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_selector)

        setupRecyclerView()
        moduleView.setText("P", "Paper Apps")
        moduleView.isChecked = true

        loadPaperApps()

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }

    private val paperAppsAdapter = PaperAppsAdapter { resId ->
        AppState.paperAppRes.value = resId
    }

    private val paperApps = mapOf(
        R.drawable.conversion_charts to "Conversion Charts",
        R.drawable.knots to "KNOTS",
        R.drawable.italian_phrasebook to "ITALIAN PHRASEBOOK",
        R.drawable.sign_language to "SIGN LANGUAGE",
        R.drawable.multiplication_table to "MULTIPLICATION TABLE",
        R.drawable.recipe to "RECIPE",
        R.drawable.pigeon_origami to "ORIGAMI PIGEON",
        R.drawable.sudoku to "SUDOKU",
        R.drawable.maze to "MAZE",
        R.drawable.word_of_the_day to "WORD OF THE DAY",
        R.drawable.anagram to "ANAGRAM",
        R.drawable.riddle to "RIDDLE",
        R.drawable.typography to "TYPOGRAPHY"
    )

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = paperAppsAdapter
    }

    private fun loadPaperApps() {
        val resId = AppState.paperAppRes.value

        val paperAppsDrawables = resources.obtainTypedArray(R.array.paper_apps_res)
        val paperAppsNames = resources.getStringArray(R.array.papar_apps_names)

        if (resId != null) {
            paperAppsAdapter.changeData(paperAppsNames.mapIndexed { index, s ->
                val key = paperAppsDrawables.getResourceId(index, -1)

                PaperAppsModel(
                    resId = key,
                    name = s,
                    checked = resId == key)
            })
        } else {
            paperAppsAdapter.changeData(paperAppsNames.mapIndexed { index, s ->
                val key = paperAppsDrawables.getResourceId(index, -1)

                PaperAppsModel(
                    resId = key,
                    name = s,
                    checked = false)
            })
        }

        paperAppsDrawables.recycle()
    }

    inner class PaperAppsAdapter(val itemClickListener: ((item: Int) -> Unit)): BindableAdapter<PaperAppsModel, BindableAdapter.ViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)

            return LevelViewHolder(inflater.inflate(R.layout.list_item_2, viewGroup, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder as LevelViewHolder
            holder.bind(this[position])
        }

        inner class LevelViewHolder(itemView: View) : BindableAdapter.ViewHolder(itemView) {
            fun bind(item: PaperAppsModel) = with(itemView) {
                val title = findViewById<TextView>(R.id.first)
                findViewById<View>(R.id.second).visibility = View.GONE
                val checkBox = findViewById<CheckBox>(R.id.check)

                title.text = item.name
                checkBox.isChecked = item.checked

                setOnClickListener {
                    if (!item.checked) {
                        clearChecked()
                        item.checked = true

                        itemClickListener.invoke(item.resId)

                        notifyDataSetChanged()
                    }
                }
            }
        }

        fun clearChecked() {
            items.forEach { it.checked = false }
        }
    }
}