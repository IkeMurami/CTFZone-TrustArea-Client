package com.zfr.ctfzoneclient.service.data

import android.content.Intent
import com.zfr.ctfzoneclient.network.data.SolutionNetworkEntity

private const val EXTRA_TASK_ID     = "TASK_ID"
private const val EXTRA_SOLUTION    = "SOLUTION"


fun Intent.asSolutionNetworkEntity(): SolutionNetworkEntity {
    return SolutionNetworkEntity(
        taskId = this.getIntExtra(EXTRA_TASK_ID, 0),
        solution = this.getStringExtra(EXTRA_SOLUTION)!!
    )
}


fun SolutionNetworkEntity.asIntent(intent: Intent): Intent {
    intent.apply {
        putExtra(EXTRA_TASK_ID, taskId)
        putExtra(EXTRA_SOLUTION, solution)
    }

    return intent
}