package com.younivibes.bogia.Game.GameRoom

import android.os.CountDownTimer
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/21/2018.
 *
 * Contains the Board
 *
 * Object responsible for controlling the gameboard, and access to it
 * He sends out notifications to clients when certain events happen
 * such as a change in the board
 */
class GameRoom(val playerOne: Player, val playerTwo: Player){

    companion object {
        const val NOTIFY = 1000L
        const val MATCH_TIME = 180000L
        const val COUNT_DOWN = 6000L

        const val MATCH_TIMER_ID = "matchTimerId"
        const val COUNT_DOWN_ID = "countDownId"

        const val GAME_STATE_COUNTDOWN = 0
        const val GAME_STATE_STARTED = 1
        const val GAME_STATE_END = 2
    }


    private var currentPlayer: Player? = null
    private val gameBoard = Board()
    private var gameIsActive = false
    private val referee = Referee(playerOne, playerTwo)
    private var observer: GameObserver? = null
    private var timeObserver: TimeObserver? = null

    private var matchTimer: CountDownTimer? = null


    fun registerObservable(o: GameObserver){
        observer = o
    }

    fun registerTimeObservable(t: TimeObserver){
        timeObserver = t
    }



    /**
     * Can set piece only if
     * 1) the game is active
     * 2)the requesting player has the current turn
     * 3)The cell is open
     */
    fun canSetPiece(request: MoveRequest): Boolean{

        return (gameIsActive && //If game is active
                request.player.userId == currentPlayer?.userId && //And the requesting player has the current turn
                gameBoard.getBoardCell(request.index) == PieceType.NO_PLAYER_STATUS) //And the piece isn't already flipped
    }


    /**
     * NOTE: A very subtle bug was discovered here. Not exactly sure how
     * but the ill-placement of changeCurrentPlayer
     * was causing a loss of data-synchronization between the two players.
     *
     * Way too many code smells in this method
     * Check here if regression bugs occur
     *
     */
    fun setPiece(request: MoveRequest){

        val cellPieceType = gameBoard.getBoardCell(request.index)

        if(cellPieceType == PieceType.NO_PLAYER_STATUS){

            /**
             * Order of Logic:
             * Set the board piece and notify subscribers of board change
             * Then check if a win or tied happened;
             * Lastly change current player, and notify subscribers of piece flipped
             */

            gameBoard.setBoardCell(request.player.pieceType, request.index)
            observer?.onBoardChanged(gameBoard.board)


            val winResult = referee.checkWin(gameBoard)

            if(winResult != null) { //SOMEBODY WON
                handleWin(winResult)
                handleGameStateChange(GAME_STATE_END)
            }
            else if(referee.checkTie(gameBoard)){ //This should be called after handle win
                handleTie()
                handleGameStateChange(GAME_STATE_END)
            }

            changeCurrentPlayer(request.player)
            observer?.onPieceFlipped(currentPlayer!!, request.player, request.index)

        }
    }


    fun play(){
        GameCountDown().start()
        handleGameStateChange(GAME_STATE_COUNTDOWN)
        currentPlayer = playerOne

    }


    private fun resolvePlayerFromid(userId: String): Player = if(userId == playerOne.userId) playerOne else playerTwo

    private fun resolveOppositePlayerFromId(userId: String): Player = if(userId == playerOne.userId) playerTwo else playerOne

    private fun resolveLoserFromWinner(winner: Player): Player = if(winner.userId == playerOne.userId) playerTwo else playerOne


    private fun handleWin(winResult: Referee.WinResult){
        gameIsActive = false
        observer?.gameWon(winResult, resolveLoserFromWinner(resolvePlayerFromid(winResult.playerId)))
    }

    private fun handleTie(){
        gameIsActive = false
        observer?.gameTied()
    }

    private fun handleGameStateChange(state: Int){
        if(state == GAME_STATE_END)
             matchTimer?.cancel()
                    
        observer?.onGameStateChanged(state)
    }

    /**
     * Resolves the current player
     * by taking the opposite player that the request was for
     *
     * E.G. if request.player.userId == playerOne.userId
     * then currentPlayer = playerTwo
     */
    private fun changeCurrentPlayer(lastPlayer: Player){
        val tempCurrPayer = resolveOppositePlayerFromId(lastPlayer.userId)
        currentPlayer = tempCurrPayer
    }



    private inner class GameCountDown: CountDownTimer(COUNT_DOWN, NOTIFY){
        override fun onFinish() {
            gameIsActive = true

            this@GameRoom.matchTimer = GameTimer().start()

            handleGameStateChange(GAME_STATE_STARTED)
        }

        override fun onTick(millisUntilFinished: Long) {
            timeObserver?.newCountDown(millisUntilFinished)
        }
    }


    private inner class GameTimer: CountDownTimer(MATCH_TIME, NOTIFY){
        override fun onFinish() {
            gameIsActive = false
            handleGameStateChange(GAME_STATE_END)
        }

        override fun onTick(millisUntilFinished: Long) {
            timeObserver?.newMatchTimer(millisUntilFinished)
        }
    }
}