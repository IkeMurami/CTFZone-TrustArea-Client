package com.zfr.ctfzoneclient.receivers.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zfr.ctfzoneclient.service.view.UserService
import com.zfr.ctfzoneclient.service.view.EXTRA_USER_NAME

class UserBroadcastReceiver : BroadcastReceiver() {

    // action: "com.zfr.ctfzoneclient.MANAGE_USER"
    override fun onReceive(context: Context, intent: Intent) {
        // Get User_name and start service
        val user_name = intent.getStringExtra(EXTRA_USER_NAME)
        UserService.startActionGetUser(context, user_name)
    }
}
