package com.younivibes.bogia.Players.AI

import android.os.Handler
import android.os.Message
import android.support.annotation.WorkerThread
import com.younivibes.bogia.Game.GameRoom.GamePiece
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.Game.GameRoom.Referee
import com.younivibes.bogia.GameEngine.GameServer
import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler
import com.younivibes.bogia.GameEngine.GameInterface
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Requests.NewGameRequest
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/20/2018.
 * Will mimick that of a real user.  E.G. will contain its own version of GameServer
 */
class AIServer: GameServer.GameInitializerObserver {

    val player = AI()
    var gameHandler: ClientGameHandler? = null

    val processHandler = Handler({
        gameHandler?.handleRequest(MoveRequest(player, it.arg1))
        true
    })

    init{

        player.init(object: DecisionResult {
            @WorkerThread
            override fun onDecisionMade(result: Message) {
                val nMsg = Message()
                nMsg.arg1 = result.arg1
                    processHandler.sendMessage(nMsg)
            }
        })

    }


    fun startServer(gameRequest: NewGameRequest){
        GameServer(this).handleLocalGameInvite(gameRequest)
    }

    override fun onConnectionEstablished(cgh: ClientGameHandler) {
        this.gameHandler = cgh
        cgh.bindCallbacks(MyInterface())
    }

    inner class MyInterface: GameInterface(){
        override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int) {
            if(newCurrentPlayer.userId == player.userId)
                player.decide() // our turn
        }

        override fun onGameStateChanged(newState: Int) {
                if(newState == GameRoom.GAME_STATE_END)
                    player.destruct()
        }

        override fun onBoardChanged(board: Array<GamePiece>) {
            player.onBoardChange(board)
        }

    }

}