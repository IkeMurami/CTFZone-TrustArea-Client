package ctfz.trustarea.client.service.data

import android.content.Intent
import ctfz.trustarea.client.network.data.SolutionNetworkEntity

private const val EXTRA_TASK_ID     = "TASK_ID"
private const val EXTRA_SOLUTION    = "SOLUTION"


fun Intent.asSolutionNetworkEntity(): SolutionNetworkEntity {
    return SolutionNetworkEntity(
        task_id = this.getIntExtra(EXTRA_TASK_ID, 0),
        solution = this.getStringExtra(EXTRA_SOLUTION)!!
    )
}


fun SolutionNetworkEntity.asIntent(intent: Intent): Intent {
    intent.apply {
        putExtra(EXTRA_TASK_ID, task_id)
        putExtra(EXTRA_SOLUTION, solution)
    }

    return intent
}