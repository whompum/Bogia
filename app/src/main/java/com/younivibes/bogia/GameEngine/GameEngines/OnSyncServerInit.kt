package com.younivibes.bogia.GameEngine.GameEngines

import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler

/**
 * Created by bryan on 2/20/2018.
 */
interface OnSyncServerInit {

    fun onServerInitialized(g: ClientGameHandler)

}