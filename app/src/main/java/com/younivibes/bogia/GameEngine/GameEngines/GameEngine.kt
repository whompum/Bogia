package com.younivibes.bogia.GameEngine.GameEngines

import android.util.Log
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler
import com.younivibes.bogia.GameEngine.GameHandlers.ServerGameHandler
import com.younivibes.bogia.GameEngine.GameHandlers.SyncHandler
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Synchronizers.BoardServerSynchronizer
import com.younivibes.bogia.GameEngine.Synchronizers.BoardSynchronizer
import com.younivibes.bogia.GameEngine.Synchronizers.OnBoardSynchronized
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/20/2018.
 *
 * Responsibility
 * Sync two users GameBoards together
 *
 * This class is abstract so clients can extetnd it to define their own source of data.
 * E.G. If two users are playing locally (same device, or with an AI) we'll extend this class
 * so we can define our own UserServiceBridge object, that fetches data from a local sqlite, or
 * SharedPreference file, and writes to their as well.
 *
 */
abstract class GameEngine(val o: OnSyncServerInit, val us: Player, val them: Player) {

    /**
     * Client Interface to the actual game. We hand is a local synchronizer object
     * A synchronizer object is someone who handles the actual syncing of data.  E.G.
     * actually speaks to the GameBoard
     */
    private val gameHandler: ClientGameHandler
    protected val serviceBridgeSync: UserSyncServerBridge

    private val gameRoom: GameRoom

    init {
        val playerOne = if (us.pieceType == PieceType.PLAYER_ONE) us else them
        val playerTwo = if (us.pieceType == PieceType.PLAYER_TWO) us else them

        gameRoom = GameRoom(playerOne, playerTwo)

        gameHandler = ClientGameHandler(BoardServerSynchronizer(gameRoom, BoardSyncedListener()), us)

        gameRoom.registerObservable(gameHandler)
        gameRoom.registerTimeObservable(gameHandler)//Should the GameEngine be aware of time as well?

        serviceBridgeSync = fetchSyncServiceBridge(ServerGameHandler(BoardSynchronizer(gameRoom)))
        serviceBridgeSync.registerOnRemoteSyncResult(fetchOnSyncChangedListener())
        serviceBridgeSync.registerOnRemoteSyncResult(gameHandler)
        serviceBridgeSync.tryConnect()
    }


    /**
     * Notifies the client that the connection is ready,
     * and returns the game interface
     */
    protected fun onConnected(){
        o.onServerInitialized(gameHandler)
        /**
         * Realistically, i should send a ping to the other users server
         * and recieve a result when ready, then start the match
         */
        gameRoom.play()
    }

    protected abstract fun fetchSyncServiceBridge(s: SyncHandler): UserSyncServerBridge
    protected abstract fun fetchOnSyncChangedListener(): OnSyncStateChange


    /**
     * Callback when our board is synced
     * Will sync to server
     */
    private inner class BoardSyncedListener: OnBoardSynchronized{
        override fun onBoardSynchronized(req: MoveRequest) {
            serviceBridgeSync.sendRequestToServer(req)
        }
    }

}










