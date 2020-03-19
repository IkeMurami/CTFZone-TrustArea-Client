package com.zfr.ctfzoneclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

const val PACKAGE_ID = BuildConfig.APPLICATION_ID

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.finish()
    }
}
