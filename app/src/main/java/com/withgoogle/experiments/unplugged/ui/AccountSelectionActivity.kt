package com.withgoogle.experiments.unplugged.ui

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.prefs.StringPreference
import com.withgoogle.experiments.unplugged.model.Account

private const val ACCOUNT_REQUEST = 0x2

class AccountSelectionActivity : AppCompatActivity() {
    private lateinit var accountPreference: StringPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_selection)

        findViewById<Button>(R.id.pick_account).setOnClickListener {
            pickAccount()
        }

        accountPreference = StringPreference(PreferenceManager.getDefaultSharedPreferences(this), "gaccount")
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
                accountPreference.set("$accountName|$accountType")

                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}