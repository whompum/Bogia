package com.younivibes.bogia.Players.AI

import android.os.Message

/**
 * Created by bryan on 3/13/2018.
 */
interface DecisionResult {
    fun onDecisionMade(result: Message)
}