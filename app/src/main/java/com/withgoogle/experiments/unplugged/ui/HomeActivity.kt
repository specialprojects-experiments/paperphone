package com.withgoogle.experiments.unplugged.ui

import android.Manifest
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.api.services.tasks.TasksScopes
import com.withgoogle.experiments.unplugged.PaperPhoneApp
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.calendar.CalendarDataSource
import com.withgoogle.experiments.unplugged.data.integrations.contacts.ContactsDataSource
import com.withgoogle.experiments.unplugged.data.integrations.tasks.TasksDataSource
import com.withgoogle.experiments.unplugged.data.integrations.weather.WeatherDataSource
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.model.Location
import com.withgoogle.experiments.unplugged.ui.calendar.CalendarModule
import com.withgoogle.experiments.unplugged.ui.calendar.CalendarSelectorActivity
import com.withgoogle.experiments.unplugged.ui.contacts.ContactListActivity
import com.withgoogle.experiments.unplugged.ui.contacts.ContactsModule
import com.withgoogle.experiments.unplugged.ui.maps.MapsActivity
import com.withgoogle.experiments.unplugged.ui.maps.MapsModule
import com.withgoogle.experiments.unplugged.ui.paperapps.PaperAppList
import com.withgoogle.experiments.unplugged.ui.pdf.ContactlessModule
import com.withgoogle.experiments.unplugged.ui.pdf.FrontModule
import com.withgoogle.experiments.unplugged.ui.pdf.NotesModules
import com.withgoogle.experiments.unplugged.ui.paperapps.PaperAppModule
import com.withgoogle.experiments.unplugged.ui.pdf.PhotoModule
import com.withgoogle.experiments.unplugged.ui.pdf.PlaceHolderModule
import com.withgoogle.experiments.unplugged.ui.print.PdfActionSelectionActivity
import com.withgoogle.experiments.unplugged.ui.tasks.TaskListActivity
import com.withgoogle.experiments.unplugged.ui.tasks.TasksModule
import com.withgoogle.experiments.unplugged.ui.weather.WeatherList
import com.withgoogle.experiments.unplugged.ui.weather.WeatherModule
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.util.bindView
import com.withgoogle.experiments.unplugged.util.weatherFormat
import timber.log.Timber

class HomeActivity : AppCompatActivity() {
    private val ACCOUNT_REQUEST = 0x2
    private val PERMISSION_READ_CALENDAR_REQUEST = 0x3
    private val PERMISSION_READ_CONTACTS_REQUEST = 0x4
    private val PERMISSION_READ_EXTERNAL_STORAGE_REQUEST = 0x5
    private val PERMISSION_LOCATION_REQUEST = 0x6
    private val PERMISSION_LOCATION_REQUEST_WEATHER = 0x7
    private val PICK_IMAGE_REQUEST = 0x8

    private val calendarView by bindView<ModuleView>(R.id.calendar)
    private val contactsView by bindView<ModuleView>(R.id.contacts)
    private val mapsView by bindView<ModuleView>(R.id.maps)
    private val notesView by bindView<ModuleView>(R.id.notes)
    private val tasksView by bindView<ModuleView>(R.id.tasks)
    private val photosView by bindView<ModuleView>(R.id.photos)
    private val weatherView by bindView<ModuleView>(R.id.weather)
    private val contactlessView by bindView<ModuleView>(R.id.contactless)
    private val paperAppsView by bindView<ModuleView>(R.id.paper_apps)

    private val moduleInfoView by lazy {
        findViewById<TextView>(R.id.module_info)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupItem(calendarView, R.string.item_calendar) {
            calendarPick()
        }

        setupItem(contactsView, R.string.item_contacts) {
            contactsPick()
        }

        setupItem(mapsView, R.string.item_maps) {
            mapsPick()
        }

        setupItem(notesView, R.string.notes)

        setupItem(tasksView, R.string.item_tasks) {
            tokenForTasks()
        }

        setupItem(photosView, R.string.photos) {
            pickPhoto()
        }

        setupItem(weatherView, R.string.item_weather) {
            weatherPick()
        }

        setupItem(paperAppsView, R.string.item_paperapps) {
            paperAppPick()
        }

        setupItem(contactlessView, R.string.item_contactless)

        findViewById<TextView>(R.id.selected_account).setOnClickListener {
            pickAccount()
        }

        findViewById<Button>(R.id.generatePdf).setOnClickListener {
            pdfAction()
        }

        setupHandlers()
        setupStateObservers()
    }

    private fun paperAppPick() {
        startActivity(Intent(this, PaperAppList::class.java))
    }

    private fun setupItem(moduleView: ModuleView, @StringRes resId: Int, longPressCallback: ((View) -> Unit)? = null) {
        with(moduleView) {
            val name = getString(resId)

            setText(name[0].toString(), name)

            val phoneApp = PaperPhoneApp.obtain(this@HomeActivity)

            val preference = phoneApp.preferenceMap[moduleView.id]

            setOnClickListener {
                preference?.let {
                    if (!it.isSet) {
                        longPressCallback?.let {
                            longPressCallback.invoke(moduleView)
                        }

                        it.set(true)
                    } else {
                        if(moduleView.isChecked) {
                            phoneApp.modulesInfoMap[moduleView.id]?.let { res ->
                                moduleInfoView.text = getString(res)
                            }
                        }
                    }
                }
            }

            longPressCallback?.let {
                setOnLongPress(View.OnLongClickListener { view ->
                    longPressCallback.invoke(view)
                    true
                })
            }

        }
    }

    private fun weatherPick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, WeatherList::class.java))
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_LOCATION_REQUEST_WEATHER
            )
        }
    }

    private fun mapsPick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, MapsActivity::class.java))
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_LOCATION_REQUEST
            )
        }
    }

    private fun pickPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun contactsPick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSION_READ_CONTACTS_REQUEST)
            }
        } else {
            startActivity(Intent(this, ContactListActivity::class.java))
        }
    }

    private fun calendarPick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CALENDAR),
                    PERMISSION_READ_CALENDAR_REQUEST)
            }
        } else {
            startActivity(Intent(this, CalendarSelectorActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_CALENDAR_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this, CalendarSelectorActivity::class.java))
                }
                return
            }

            PERMISSION_READ_CONTACTS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this, ContactListActivity::class.java))
                }
                return
            }
            PERMISSION_READ_EXTERNAL_STORAGE_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
                }
                return
            }
            PERMISSION_LOCATION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this, MapsActivity::class.java))
                }
                return
            }
            PERMISSION_LOCATION_REQUEST_WEATHER -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this, WeatherList::class.java))
                }
                return
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setupStateObservers() {
        AppState.account.observe(this, Observer { account ->
            val selectedAccountView = findViewById<TextView>(R.id.selected_account)
            selectedAccountView.text = account.toString()
        })

        AppState.currentDate.observe(this, Observer { date ->
            val selectedDateView = findViewById<TextView>(R.id.selected_date)
            selectedDateView.text = date.weatherFormat()
        })

        AppState.firstName.observe(this, Observer { name ->
            val personNameView = findViewById<TextView>(R.id.person_name)
            personNameView.text = "$name's phone"
        })
    }

    private fun setupHandlers() {
        findViewById<TextView>(R.id.person_name).setOnClickListener {
            startActivity(Intent(this, NameSettingActivity::class.java))
        }
    }

    private val callback = AccountManagerCallback<Bundle> {
        val token = it.result.getString(AccountManager.KEY_AUTHTOKEN)

        PaperPhoneApp.obtain(this).taskToken = token

        startActivity(Intent(this, TaskListActivity::class.java))

        Timber.d("Tasks token: $token")
    }

    private fun tokenForTasks() {
        val selectedAccount = AppState.account.value

        selectedAccount?.let { account ->
            val systemAccount = AccountManager.get(this).getAccountsByType(account.accountType).first { it.name == account.accountName }

            AccountManager.get(this).getAuthToken(systemAccount, "oauth2:${TasksScopes.TASKS_READONLY}", Bundle(), this, callback, null)
        }
    }

    private fun pickAccount() {
        val selectedAccount = AppState.account.value?.let { account ->
            AccountManager.get(this).getAccountsByType(account.accountType).firstOrNull { it.name == account.accountName }
        }

        val intent = AccountManager.newChooseAccountIntent(selectedAccount, null, arrayOf("com.google"), null, null, null,
            null)
        startActivityForResult(intent, ACCOUNT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                val accountName = it.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val accountType = it.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)

                AppState.account.value = Account(accountName, accountType)
                PaperPhoneApp.obtain(this).accountPreference.set("$accountName|$accountType")
            }
        } else if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                Timber.d(it.dataString)
                AppState.photoUri.value = it.data
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun pdfAction() {
        AppState.modules.value =
            Triple(
                listOf(
                getModuleOrEmpty(contactsView, ContactsModule(ContactsDataSource.contacts.toList())),
                getModuleOrEmpty(calendarView, CalendarModule(CalendarDataSource.ordered)),
                getModuleOrEmpty(tasksView, TasksModule(TasksDataSource.ordered)),
                getModuleOrEmpty(
                    photosView,
                    AppState.photoUri.value?.let { PhotoModule(this@HomeActivity, it) } ?: PlaceHolderModule()),
                getModuleOrEmpty(notesView, NotesModules()),
                FrontModule(AppState.firstName.value?.let { it } ?: ""),
                getModuleOrEmpty(
                    weatherView,
                    WeatherModule(WeatherDataSource.forecasts, AppState.currentDate.value!!)
                ), getModuleOrEmpty(paperAppsView,
                        PaperAppModule(AppState.paperAppRes.value ?: R.drawable.pigeon_origami), !contactlessView.isChecked)
            ),
                if (mapsView.isChecked) MapsModule(this@HomeActivity,
                    origin = AppState.origin.value?.let { it } ?: Location(51.5101243, -0.136357),
                    destination = AppState.destination.value?.let { it } ?: Location(51.5223533, -0.1314477)) else null,
                if (contactlessView.isChecked) ContactlessModule() else null
            )

        startActivity(Intent(this, PdfActionSelectionActivity::class.java))
    }

    private fun getModuleOrEmpty(moduleView: ModuleView, pdfModule: PdfModule, drawDots: Boolean = true): PdfModule {
        return if (moduleView.isChecked) {
            pdfModule
        } else {
            PlaceHolderModule(drawDots)
        }
    }
}
