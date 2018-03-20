package com.younivibes.bogia.GameEngine

import com.younivibes.bogia.Game.GameRoom.GamePiece
import com.younivibes.bogia.Game.GameRoom.GameObserver
import com.younivibes.bogia.Game.GameRoom.Referee
import com.younivibes.bogia.Game.GameRoom.TimeObserver
import com.younivibes.bogia.GameEngine.GameEngines.OnSyncStateChange
import com.younivibes.bogia.Players.Player

/**
 * STUB IMPLEMENTATIONS of the various game state callbacks
 * Created by bryan on 2/23/2018.
 */
open class GameInterface: GameObserver, TimeObserver, OnSyncStateChange {

    override fun newMatchTimer(timeLeft: Long){}

    override fun onConnected(){}


    override fun newCountDown(timeLeft: Long){}


    override fun onGameStateChanged(newState: Int){}


    override fun gameWon(winResult: Referee.WinResult, loser: Player){}


    override fun gameTied(){}


    override fun onBoardChanged(board: Array<GamePiece>){}


    override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int){}

}