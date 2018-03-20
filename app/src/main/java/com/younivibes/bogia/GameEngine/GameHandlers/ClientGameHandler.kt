package com.younivibes.bogia.GameEngine.GameHandlers

import android.support.annotation.NonNull
import android.util.Log
import com.younivibes.bogia.Game.GameRoom.*
import com.younivibes.bogia.GameEngine.GameEngines.OnSyncStateChange
import com.younivibes.bogia.GameEngine.GameInterface
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Synchronizers.GameBoardSynchronizer
import com.younivibes.bogia.GameEngine.Requests.Request
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/21/2018.
 * Is the client sided game interface. E.G. any/all data the client wants to get/send
 * is handled through this guy. He does this by calling bindCallbacks() which registers a reciever
 * on the changing states of the GameBoard
 */
class ClientGameHandler internal constructor(private val synchronizer: GameBoardSynchronizer, val us: Player):
        GameObserver,
        TimeObserver,
        SyncHandler,
        OnSyncStateChange{

    var gameInterface: GameInterface? = null

    var isGameActive: Boolean = false

    /**
     * Parses the Request type is called by the client in this instance
     * Then asks the Synchronizer to sync their board
     */
    override fun handleRequest(req: Request){
        if(req is MoveRequest)
            synchronizer.sync(req)
    }

    override fun onConnected() {
        gameInterface?.onConnected()
    }

    fun bindCallbacks(@NonNull gameInterface: GameInterface){
        this.gameInterface = gameInterface
    }

    override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int) {
        gameInterface?.onPieceFlipped(newCurrentPlayer, lastPlayer, index)
    }

    override fun onGameStateChanged(newState: Int) {
        if(newState == GameRoom.GAME_STATE_STARTED)
            isGameActive = true
        if(newState == GameRoom.GAME_STATE_END)
            isGameActive = false
        gameInterface?.onGameStateChanged(newState)
    }

    override fun gameWon(winResult: Referee.WinResult, loser: Player) {
        gameInterface?.gameWon(winResult, loser)
    }

    override fun gameTied() {
        gameInterface?.gameTied()
    }

    override fun onBoardChanged(board: Array<GamePiece>) {
        gameInterface?.onBoardChanged(board)
    }

    override fun newMatchTimer(timeLeft: Long) {
        gameInterface?.newMatchTimer(timeLeft)
    }

    override fun newCountDown(timeLeft: Long) {
        Log.i("TICK", "TICK LEFT: ${timeLeft} FROM CGH")
        gameInterface?.newCountDown(timeLeft)
    }
}

