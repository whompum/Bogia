package com.younivibes.bogia.GameEngine.Synchronizers

import android.util.Log
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.GameEngine.Requests.MoveRequest

/**
 * Created by bryan on 2/22/2018.
 */
class BoardServerSynchronizer(g: GameRoom): BoardSynchronizer(g) {

    var boardSyncedCallback: OnBoardSynchronized? = null

    constructor(g: GameRoom, onBoardSynchronized: OnBoardSynchronized): this(g){
        boardSyncedCallback = onBoardSynchronized
    }


    /**
     * The super implementation checks if it can sync
     */
    override fun sync(request: MoveRequest): Boolean {

        val canSetPiece = super.sync(request)

        if( canSetPiece && boardSyncedCallback != null)
            boardSyncedCallback?.onBoardSynchronized(request)

    return canSetPiece
    }
}