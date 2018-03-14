package com.younivibes.bogia.Players.AI

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.util.Log
import com.younivibes.bogia.Game.GameRoom.Board
import com.younivibes.bogia.Game.GameRoom.GamePiece
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.Players.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Created by bryan on 2/18/2018.
 * The LOCAL will always be player two
 *
 * This guy uses a simple Mimiax Algorithm
 * to compute the best possible move to make
 * out of all the moves he could make
 */
class AI: Player(PieceType.PLAYER_TWO, AI_ID, NAME) {

    var board: Array<GamePiece> = Array<GamePiece>(3*3, { GamePiece() } )

    private lateinit var brainHandler: Handler

    private val processor = HandlerThread("Processor")

    init{
        processor.start()
    }

    fun init(dR : (DecisionResult)){

        brainHandler = Handler(processor.looper, {
            dR.onDecisionMade(it)
            true
        })

    }

    fun destruct(){
        processor.quit()
    }


    fun onBoardChange(newBoard: Array<GamePiece>){
       board = newBoard
    }

    @MainThread
    fun decide(){

        brainHandler.post {
            val msg = Message()
                msg.arg1 = decide(7, 0, PieceType.PLAYER_TWO).index
                brainHandler.sendMessage(msg)
        }

    }

    @WorkerThread
    fun decide(depth: Int, index: Int, pieceType: PieceType): BestMove{

        val winner = checkWin(board)

        //If we won, return the best move object with the index, and our winning value
        if(winner != PieceType.NO_PLAYER_STATUS)
            return if(winner == PieceType.PLAYER_TWO) BestMove(index, 1) else BestMove(index, -1)

        //The game has tied. Return a neutral Value. Call this logic after check wins
        if( findOpenCells(board).isEmpty() )
            return BestMove(index, 0)

        //If the game hasn't reached a terminal state, generate a list of values from our children

        var listOfMoves = ArrayList<BestMove>()

        val openCells = findOpenCells(board)

        openCells.forEach{
            //NOTE: 'it' is not the index of openCells, but the value of openCells[i], which 'it'self is an index of the board.
            inject(it, pieceType) //Inject our piece type into the board, then hand off to children

            listOfMoves.add(decide(depth, it, ( if(pieceType == PieceType.PLAYER_TWO) PieceType.PLAYER_ONE else PieceType.PLAYER_TWO ) ))

            //Reset our board to maintain data integrity
            board[it].pieceType = PieceType.NO_PLAYER_STATUS
            listOfMoves[listOfMoves.lastIndex].index = it //Set the index my man
        }

        //Find the best values for ourselves to return.

        var bestMove: BestMove? = null

        //Is AI's turn, so find the highest ranking best move, and return it
        if(pieceType == PieceType.PLAYER_TWO) {
            var bestScore = -1 //Set low so we can match it easier

            listOfMoves.forEach {
                if (it.value >= bestScore){
                    bestMove = it
                    bestScore = it.value
                }
            }
        return bestMove!!
        }

        if(pieceType == PieceType.PLAYER_ONE){
            var bestScore = 1 //Set high so we can match it easier

            listOfMoves.forEach {

                if(it.value <= bestScore) {
                    bestMove = it
                    bestScore = it.value
                }

            }
            return bestMove!!
        }

        return bestMove!!
    }


    private fun inject(index: Int, pieceType: PieceType){
        board[index].pieceType = pieceType
    }



    /**
     * Determines the winner, if any
     *
     * 0|1|2
     * 3|4|5
     * 6|7|8
     */
    private fun checkWin(board: Array<GamePiece>): PieceType{

        if(board[0].pieceType == board[1].pieceType && board[1].pieceType == board[2].pieceType)
            return board[0].pieceType

        if(board[3].pieceType == board[4].pieceType && board[4].pieceType == board[5].pieceType)
            return board[3].pieceType

        if(board[6].pieceType == board[7].pieceType && board[7].pieceType == board[8].pieceType)
            return board[6].pieceType



        if(board[0].pieceType == board[3].pieceType && board[3].pieceType == board[6].pieceType)
            return board[0].pieceType

        if(board[1].pieceType == board[4].pieceType && board[4].pieceType == board[7].pieceType)
            return board[1].pieceType

        if(board[2].pieceType == board[5].pieceType && board[5].pieceType == board[8].pieceType)
            return board[2].pieceType




        if(board[2].pieceType == board[4].pieceType && board[4].pieceType == board[6].pieceType)
            return board[2].pieceType

        if(board[0].pieceType == board[4].pieceType && board[4].pieceType == board[8].pieceType)
            return board[0].pieceType

        return PieceType.NO_PLAYER_STATUS
    }

    /**
     * Returns an integer array containing the open cells of the board object
     */
    fun findOpenCells(board: Array<GamePiece>): List<Int>{

        val openCells = ArrayList<Int>()

        for(i in 0 until board.size)
            if(board[i].pieceType == PieceType.NO_PLAYER_STATUS)
                openCells.add(i)
        return openCells
    }

    companion object {
        val AI_ID = "MinimaxAIYOUNITTE"
        val NAME = "AI"
    }
}












