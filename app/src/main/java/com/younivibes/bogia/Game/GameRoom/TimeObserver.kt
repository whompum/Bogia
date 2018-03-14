package com.younivibes.bogia.Game.GameRoom

/**
 * Created by bryan on 2/23/2018.
 */
interface TimeObserver {
    fun newMatchTimer(timeLeft: Long)
    fun newCountDown(timeLeft: Long)
}