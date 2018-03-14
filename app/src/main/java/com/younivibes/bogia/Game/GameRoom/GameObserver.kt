package com.younivibes.bogia.Game.GameRoom

import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/22/2018.
 */
interface GameObserver {
    fun onGameStateChanged(newState: Int)
    fun gameWon(winResult: Referee.WinResult, loser: Player)
    fun gameTied()
    fun onBoardChanged(board: Array<GamePiece>)
    fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int)
}