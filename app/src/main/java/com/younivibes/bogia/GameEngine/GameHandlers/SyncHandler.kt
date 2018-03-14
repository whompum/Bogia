package com.younivibes.bogia.GameEngine.GameHandlers

import com.younivibes.bogia.GameEngine.Requests.Request

/**
 * Created by bryan on 2/20/2018.
 */
interface SyncHandler {
    fun handleRequest(req: Request)
}