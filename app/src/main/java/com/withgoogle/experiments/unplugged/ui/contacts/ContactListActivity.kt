package com.withgoogle.experiments.unplugged.ui.contacts

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.provider.ContactsContract
import android.widget.Button
import androidx.recyclerview.widget.ItemTouchHelper
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.contacts.ContactsImporter
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.withgoogle.experiments.unplugged.util.bindView

class ContactListActivity: AppCompatActivity() {
    private val PICK_CONTACT = 0x2

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val moduleView by bindView<ModuleView>(R.id.module)

    private val addView by bindView<Button>(R.id.add)

    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        setupRecyclerView()

        moduleView.setText("C", "Contacts")
        moduleView.isChecked = true

        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel::class.java)

        addView.setOnClickListener {
            val contactPickerIntent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            startActivityForResult(contactPickerIntent, PICK_CONTACT)
        }

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }

        contactsViewModel.allContacts.observe(this, Observer { contacts ->
            contacts?.let {
                contactsAdapter.changeData(it)

                addView.visibility = if (it.size != 7) View.VISIBLE else View.INVISIBLE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { it ->
                val contact = ContactsImporter(this).forUri(it)
                contact?.let { newContact ->
                    contactsViewModel.insert(newContact)
                }

                checkContactsCount()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private val contactsAdapter = ContactListAdapter()


    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(contactsAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private inner class SwipeToDeleteCallback(val contactListAdapter: ContactListAdapter)
        : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val background = ColorDrawable(Color.parseColor("#1d1d1d"))

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val itemView = viewHolder.itemView
            val backgroundCornerOffset = 20

            val icon = ResourcesCompat.getDrawable(recyclerView.context.resources, R.drawable.ic_delete_white_24dp, null)

            val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
            val iconBottom = iconTop + icon.intrinsicHeight

            when {
                dX > 0 -> {
                    val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
                    val iconRight = itemView.left + iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                        )
                    }
                dX < 0 -> {
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom
                    )
                }

                else -> background.setBounds(0, 0, 0, 0)
            }

            background.draw(c)
            icon.draw(c)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition

            val contact = contactListAdapter[position]
            contactsViewModel.delete(contact.id)
        }
    }

    fun checkContactsCount() {
        // Max number of contacts
        // addView.visibility = if (contactsAdapter.itemCount != 6) View.VISIBLE else View.INVISIBLE
    }
}

