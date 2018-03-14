package com.younivibes.bogia.GameEngine.Synchronizers

import android.util.Log
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.Players.AI.AI

/**
 * Created by bryan on 2/21/2018.
 * Make this behavior d efault, then allow extension
 * if i  want a class to also write to a server.
 */
open class BoardSynchronizer(val gameRoom: GameRoom): GameBoardSynchronizer {
    override fun sync(request: MoveRequest): Boolean {
        val canSetPiece = gameRoom.canSetPiece(request)

        if(request.player.userId != AI.AI_ID)
            Log.i("TURN_FIX", "PLAYER B CAN TOUCH THE BOARD: ${canSetPiece}")

        if(canSetPiece)
            gameRoom.setPiece(request)

        return canSetPiece
    }
}