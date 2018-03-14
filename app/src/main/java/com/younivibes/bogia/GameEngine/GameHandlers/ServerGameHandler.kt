package com.younivibes.bogia.GameEngine.GameHandlers

import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Synchronizers.GameBoardSynchronizer
import com.younivibes.bogia.GameEngine.Requests.Request

/**
 * Created by bryan on 2/21/2018.
 * Server-sided interface to the game. It is this object that recieves data from a server
 * and then passes it to a synchronizerGame object that actually handles the data syncing on the
 * users device
 */
class ServerGameHandler internal constructor(private val synchronizerGame: GameBoardSynchronizer): SyncHandler {

    override fun handleRequest(req: Request) {
        if(req is MoveRequest)
            synchronizerGame.sync(req)
    }
}