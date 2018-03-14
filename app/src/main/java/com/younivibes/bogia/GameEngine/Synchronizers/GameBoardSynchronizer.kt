package com.younivibes.bogia.GameEngine.Synchronizers

import com.younivibes.bogia.GameEngine.Requests.MoveRequest

/**
 * Created by bryan on 2/21/2018.
 * A Synchronizer object is an object that handles the literal syncing of data
 */
interface GameBoardSynchronizer : Synchronizer {
    fun sync(request: MoveRequest): Boolean
}