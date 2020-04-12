package com.zfr.ctfzoneclient.service.data

import android.content.Intent
import com.zfr.ctfzoneclient.network.data.TaskNetworkEntity

private const val EXTRA_DESCRIPTION = "DESCRIPTION"
private const val EXTRA_CHALLENGE   = "CHALLENGE"
private const val EXTRA_REWARD      = "REWARD"
private const val EXTRA_ID          = "ID"

private const val EXTRA_TASKS       = "TASKS"


fun Intent.asTaskNetworkEntity(): TaskNetworkEntity {

    return TaskNetworkEntity(
        task_id     = this.getIntExtra(EXTRA_ID, 0),
        description = this.getStringExtra(EXTRA_DESCRIPTION)!!,
        challenge   = this.getStringExtra(EXTRA_CHALLENGE)!!,
        reward      = this.getStringExtra(EXTRA_REWARD)!!
    )

}


fun TaskNetworkEntity.asIntent(intent: Intent): Intent {
    intent.apply {
        putExtra(EXTRA_DESCRIPTION, description)
        putExtra(EXTRA_CHALLENGE, challenge)
        putExtra(EXTRA_REWARD, reward)
        putExtra(EXTRA_ID, task_id)
    }

    return intent
}

fun List<TaskNetworkEntity>.asIntent(intent: Intent): Intent {
    intent.putExtra(EXTRA_TASKS, ArrayList(this))

    return intent
}