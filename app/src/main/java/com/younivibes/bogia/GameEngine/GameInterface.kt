package com.younivibes.bogia.GameEngine

import com.younivibes.bogia.Game.GameRoom.GamePiece
import com.younivibes.bogia.Game.GameRoom.GameObserver
import com.younivibes.bogia.Game.GameRoom.Referee
import com.younivibes.bogia.Game.GameRoom.TimeObserver
import com.younivibes.bogia.GameEngine.GameEngines.OnSyncStateChange
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/23/2018.
 */
abstract class GameInterface: GameObserver, TimeObserver, OnSyncStateChange {

    abstract override fun newMatchTimer(timeLeft: Long)

    abstract override fun onConnected()

    abstract  override fun newCountDown(timeLeft: Long)

    abstract  override fun onGameStateChanged(newState: Int)

    abstract  override fun gameWon(winResult: Referee.WinResult, loser: Player)

    abstract  override fun gameTied()

    abstract  override fun onBoardChanged(board: Array<GamePiece>)

    abstract  override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int)
}