package com.younivibes.bogia.GameEngine.GameEngines

import com.younivibes.bogia.GameEngine.GameHandlers.SyncHandler
import com.younivibes.bogia.GameEngine.Requests.Request

/**
 *
 */
abstract class UserSyncServerBridge(val syncHandler: SyncHandler) {


    protected val syncResultListeners = HashSet<OnSyncStateChange>(1)

    /**
     * Callback for when certain states of server syncing. E.G.
     * SYNC_FAILED
     * NO_INTERGE
     * SYNC_SUCCESS
     * CONNECT_TO_PLAYER
     * etc
     */
    fun registerOnRemoteSyncResult(syncStateChange: OnSyncStateChange){
        this.syncResultListeners.add(syncStateChange)
    }


    /**
     * Gives the request to a SyncHandler
     * who is defined by the client of this object
     */
    open fun handleRequestFromServer(req: Request){
        syncHandler.handleRequest(req)
    }


    /**
     * Sends a request to the server (Writing to the opponent)
     * The whole point of this class is to allow a client to choose
     * where the data goes to/comes from. Thus this method is abstract
     * to allow client concrete definitions
     */
    abstract fun sendRequestToServer(req: Request)


    /**Tries to connect to server, then post a result
     * */
    abstract fun tryConnect()

}