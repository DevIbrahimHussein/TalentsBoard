package com.example.talentsboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class Authentication : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val fragment = Login()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment)
        transaction.commit()
        // to hide keyboard while starting the activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }
}
