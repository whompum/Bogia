package com.younivibes.bogia.GameEngine.Synchronizers

import com.younivibes.bogia.GameEngine.Requests.MoveRequest

/**
 * Created by bryan on 2/21/2018.
 */
interface OnBoardSynchronized {

    fun onBoardSynchronized(req: MoveRequest)

}