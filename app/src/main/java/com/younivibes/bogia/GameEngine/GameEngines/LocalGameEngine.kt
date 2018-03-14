package com.younivibes.bogia.GameEngine.GameEngines

import com.younivibes.bogia.Players.Player

import com.younivibes.bogia.GameEngine.GameHandlers.SyncHandler
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Requests.Request

/**
 * Created by bryan on 2/21/2018.
 *
 * Local Game Synchronizer for games to be synchronized locally (AI / OnePhonePVP)
 *
 */
class LocalGameEngine(_o: OnSyncServerInit, us: Player, them: Player): GameEngine(_o, us, them) {


    override fun fetchSyncServiceBridge(s: SyncHandler): UserSyncServerBridge {
        return LocalSyncServerBridge(s)
    }

    override fun fetchOnSyncChangedListener(): OnSyncStateChange {
        return object: OnSyncStateChange{
            override fun onConnected() {
                this@LocalGameEngine.onConnected()
            }
        }
    }

    /**
     * Uses the SharedPreference API as the Middle Man for the clients
     */
    private inner class LocalSyncServerBridge(s: SyncHandler): UserSyncServerBridge(s), LocalGameDataServer.ServerDataHandler {

        val LGDS = LocalGameDataServer.getInstance()

        init {
            LGDS.registerToClientList(LocalGameDataServer.T(us, them, this))
        }

        override fun handleData(req: MoveRequest) {
            handleRequestFromServer(req)
        }

        override fun sendRequestToServer(req: Request) {
            LGDS.sendData(req as MoveRequest)
        }

        override fun tryConnect() {
            for(syncListeners in syncResultListeners) {
                syncListeners.onConnected( )
            }
        }
    }


}




























