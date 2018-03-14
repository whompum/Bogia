package com.younivibes.bogia.Game.GameRoom

/**
 * Created by bryan on 2/24/2018.
 */
interface TimeListener {

    fun onFinished()
    fun onTick(millis: Long)
}