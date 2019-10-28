package com.withgoogle.experiments.unplugged.ui

import android.Manifest
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.api.services.tasks.TasksScopes
import com.withgoogle.experiments.unplugged.PaperPhoneApp
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.calendar.CalendarDataSource
import com.withgoogle.experiments.unplugged.data.integrations.tasks.TasksDataSource
import com.withgoogle.experiments.unplugged.data.integrations.weather.WeatherDataSource
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.model.Contact
import com.withgoogle.experiments.unplugged.model.Location
import com.withgoogle.experiments.unplugged.ui.calendar.CalendarModule
import com.withgoogle.experiments.unplugged.ui.calendar.CalendarSelectorActivity
import com.withgoogle.experiments.unplugged.ui.contactless.ContactlessActivity
import com.withgoogle.experiments.unplugged.ui.contactless.ContactlessModule
import com.withgoogle.experiments.unplugged.ui.contacts.ContactListActivity
import com.withgoogle.experiments.unplugged.ui.contacts.ContactsModule
import com.withgoogle.experiments.unplugged.ui.maps.MapsActivity
import com.withgoogle.experiments.unplugged.ui.maps.MapsModule
import com.withgoogle.experiments.unplugged.ui.notes.NotesActivity
import com.withgoogle.experiments.unplugged.ui.notes.NotesModules
import com.withgoogle.experiments.unplugged.ui.paperapps.PaperAppList
import com.withgoogle.experiments.unplugged.ui.paperapps.PaperAppModule
import com.withgoogle.experiments.unplugged.ui.pdf.FrontModule
import com.withgoogle.experiments.unplugged.ui.pdf.PlaceHolderModule
import com.withgoogle.experiments.unplugged.ui.photos.PhotoModule
import com.withgoogle.experiments.unplugged.ui.photos.PhotosActivity
import com.withgoogle.experiments.unplugged.ui.print.PdfActionSelectionActivity
import com.withgoogle.experiments.unplugged.ui.tasks.TaskListActivity
import com.withgoogle.experiments.unplugged.ui.tasks.TasksModule
import com.withgoogle.experiments.unplugged.ui.weather.WeatherList
import com.withgoogle.experiments.unplugged.ui.weather.WeatherModule
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.util.bindView
import com.withgoogle.experiments.unplugged.util.weatherFormat
import timber.log.Timber
import java.io.IOException

class HomeActivity : AppCompatActivity() {
    private val ACCOUNT_REQUEST = 0x2
    private val PERMISSION_READ_CALENDAR_REQUEST = 0x3
    private val PERMISSION_READ_CONTACTS_REQUEST = 0x4
    private val PERMISSION_READ_EXTERNAL_STORAGE_REQUEST = 0x5
    private val PERMISSION_LOCATION_REQUEST = 0x6
    private val PERMISSION_LOCATION_REQUEST_WEATHER = 0x7

    private val calendarView by bindView<ModuleView>(R.id.calendar)
    private val contactsView by bindView<ModuleView>(R.id.contacts)
    private val mapsView by bindView<ModuleView>(R.id.maps)
    private val notesView by bindView<ModuleView>(R.id.notes)
    private val tasksView by bindView<ModuleView>(R.id.tasks)
    private val photosView by bindView<ModuleView>(R.id.photos)
    private val weatherView by bindView<ModuleView>(R.id.weather)
    private val contactlessView by bindView<ModuleView>(R.id.contactless)
    private val paperAppsView by bindView<ModuleView>(R.id.paper_apps)

    private lateinit var homeViewModel: HomeViewModel

    private val contacts: ArrayList<Contact> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        homeViewModel.allContacts.observe(this, Observer {
            contacts.clear()
            contacts.addAll(it)
        })

        setupItem(calendarView, R.string.item_calendar) {
            calendarPick()
        }

        setupItem(contactsView, R.string.item_contacts) {
            contactsPick()
        }

        setupItem(mapsView, R.string.item_maps) {
            mapsPick()
        }

        setupItem(notesView, R.string.notes) {
            startActivity(Intent(this@HomeActivity, NotesActivity::class.java))
        }

        setupItem(tasksView, R.string.item_tasks) {
            tokenForTasks()
        }

        setupItem(photosView, R.string.item_photos) {
            pickPhoto()
        }

        setupItem(weatherView, R.string.item_weather) {
            weatherPick()
        }

        setupItem(paperAppsView, R.string.item_paperapps) {
            paperAppPick()
        }

        setupItem(contactlessView, R.string.item_contactless) {
            startActivity(Intent(this@HomeActivity, ContactlessActivity::class.java))
        }

        findViewById<TextView>(R.id.selected_account).setOnClickListener {
            pickAccount()
        }

        findViewById<Button>(R.id.generatePdf).setOnClickListener {
            pdfAction()
        }

        with(findViewById<ImageView>(R.id.help)) {
            (parent as View).post {
                val rect = Rect()
                getHitRect(rect)
                rect.top -= 100
                rect.left -= 100
                rect.bottom += 100
                rect.right += 100
                (parent as View).touchDelegate = TouchDelegate(rect, this)
            }

            setOnClickListener {
                startActivity(Intent(this@HomeActivity, HelpActivity::class.java))
            }
        }

        setupHandlers()
        setupStateObservers()
    }



    private fun paperAppPick() {
        startActivity(Intent(this, PaperAppList::class.java))
    }

    private fun setupItem(moduleView: ModuleView, @StringRes resId: Int, onPressed: ((View) -> Unit)? = null) {
        with(moduleView) {
            val name = getString(resId)

            setText(name[0].toString(), name)

            setOnClickListener {
                if (isChecked) {
                    onPressed?.invoke(it)
                }
            }
        }
    }

    private fun weatherPick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, WeatherList::class.java))
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissionRationale(R.string.item_weather, R.string.location_permission_rationale)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_LOCATION_REQUEST_WEATHER
                )
            }
        }
    }

    private fun mapsPick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, MapsActivity::class.java))
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissionRationale(R.string.item_maps, R.string.location_permission_rationale)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_LOCATION_REQUEST
                )
            }
        }
    }

    private fun pickPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, PhotosActivity::class.java))
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissionRationale(R.string.item_photos, R.string.storage_permission_rationale)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ_EXTERNAL_STORAGE_REQUEST
                )
            }
        }
    }

    private fun requestPermissionRationale(title: Int, rationaleRes: Int) {
        startActivity(Intent(this, PermissionActivity::class.java).apply {
            putExtra("title_res", title)
            putExtra("rationale_res", rationaleRes)
        })
    }

    private fun contactsPick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                requestPermissionRationale(R.string.item_contacts, R.string.contacts_permission_rationale)
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
                requestPermissionRationale(R.string.item_calendar, R.string.calendar_permission_rationale)
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
                    startActivity(Intent(this, PhotosActivity::class.java))
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
        try {
            val token = it.result.getString(AccountManager.KEY_AUTHTOKEN)

            PaperPhoneApp.obtain(this).taskToken = token

            startActivity(Intent(this, TaskListActivity::class.java))

            Timber.d("Tasks token: $token")
        } catch (e: OperationCanceledException) {
            Timber.e(e,"Operation canceled")
        } catch (e: AuthenticatorException) {
            Timber.e(e, "Authenticator failed")
        } catch (e: IOException) {
            Timber.e(e,"Unable to connect")
        }
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
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun pdfAction() {
        AppState.modules.value =
            Triple(
                listOf(
                getModuleOrEmpty(contactsView, ContactsModule(contacts)),
                getModuleOrEmpty(calendarView, CalendarModule(CalendarDataSource.ordered)),
                getModuleOrEmpty(tasksView, TasksModule(TasksDataSource.ordered)),
                getModuleOrEmpty(
                    photosView,
                    AppState.photoUri.value?.let {
                        PhotoModule(it)
                    } ?: PlaceHolderModule()),
                getModuleOrEmpty(notesView, NotesModules()),
                FrontModule(AppState.firstName.value?.let { it } ?: ""),
                getModuleOrEmpty(
                    weatherView,
                    WeatherModule(WeatherDataSource.forecasts, AppState.currentDate.value!!)
                ), getModuleOrEmpty(paperAppsView,
                        PaperAppModule(AppState.paperAppRes.value ?: R.drawable.pigeon_origami), !contactlessView.isChecked)
            ),
                if (mapsView.isChecked) MapsModule(this@HomeActivity,
                    origin = AppState.origin.value?.let { it } ?: Location(51.5101243, -0.136357, ""),
                    destination = AppState.destination.value?.let { it } ?: Location(51.5223533, -0.1314477, "")) else null,
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
