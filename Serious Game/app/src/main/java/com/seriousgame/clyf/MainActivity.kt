package com.seriousgame.clyf

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.seriousgame.clyf.auth.SignIn
import com.seriousgame.clyf.guest.GuestDataActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_settings.view.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_main)

        creator.setOnClickListener {
            val intent = Intent(this, SignIn::class.java) //define intent - F.
            startActivity(intent) //takes you to SignIn activity - F.
        }

        guest.setOnClickListener {
            val intent = Intent(this, GuestDataActivity::class.java) //define intent - F.
            startActivity(intent) //takes you to SignIn activity - F.
        }

        settings.setOnClickListener {
            //popup creation - F.
            val dialogBuilderSettings : AlertDialog.Builder
            val dialogSettings : AlertDialog?
            val viewSettings = LayoutInflater.from(this).inflate(R.layout.popup_settings, null, false)
            dialogBuilderSettings = AlertDialog.Builder(this).setView(viewSettings)
            dialogSettings = dialogBuilderSettings.create()
            dialogSettings.show()

            val selectLanguage = viewSettings.select_langauge
            selectLanguage.setOnClickListener {
                showChangeLanguageDialog()
                dialogSettings.dismiss()
            }
        }

    }

    private fun showChangeLanguageDialog() {
        val listItem = arrayOf("English", "Italian", "French", "German", "Spanish")
        val mBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose Language...")
        mBuilder.setSingleChoiceItems(listItem, -1) { dialogInterface, i ->
            if (i == 0) {
                setLocale("en")
                recreate()
            }
            if (i == 1) {
                setLocale("it")
                recreate()
            }
            if (i == 2) {
                setLocale("fr")
                recreate()
            }
            if (i == 3) {
                setLocale("de")
                recreate()
            }
            if (i == 4) {
                setLocale("es")
                recreate()
            }
            dialogInterface.dismiss()
        }
        val mDialog : AlertDialog = mBuilder.create()
        mDialog.show()
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)

        val editor : SharedPreferences.Editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("MyLanguage", language)
        editor.apply()
    }

    private fun loadLocale(){
        val prefs : SharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language : String? = prefs.getString("MyLanguage", "")
        if (language != null) {
            setLocale(language)
        }
    }



}