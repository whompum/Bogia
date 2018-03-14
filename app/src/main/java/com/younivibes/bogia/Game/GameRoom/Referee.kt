package com.younivibes.bogia.Game.GameRoom

import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/22/2018.
 */
class Referee(val p1: Player,val p2: Player) {


    fun checkWin(b: Board): WinResult?{

        /**
         *  [0][1][2]
         *  [3][4][5]
         *  [6][7][8]
         */

        var winningPiece: PieceType = PieceType.NO_PLAYER_STATUS
        var winningArray: Array<Int>? = null




        //HORIZONTAL
        if(b.getBoardCell(0) == b.getBoardCell(1) && b.getBoardCell(1) == b.getBoardCell(2)){
        winningPiece = b.getBoardCell(0)
        winningArray = Array(3, {0})
        winningArray[0] = 0
        winningArray[1] = 1
        winningArray[2] = 2
    }
        if(b.getBoardCell(3) == b.getBoardCell(4) && b.getBoardCell(4) == b.getBoardCell(5)){
            winningPiece = b.getBoardCell(3)
            winningArray = Array(3, {3})
            winningArray[0] = 3
            winningArray[1] = 4
            winningArray[2] = 5
        }
        if(b.getBoardCell(6) == b.getBoardCell(7) && b.getBoardCell(7) == b.getBoardCell(8)){
            winningPiece = b.getBoardCell(6)
            winningArray = Array(3, {6})
            winningArray[0] = 6
            winningArray[1] = 7
            winningArray[2] = 8
        }


        //VERTICAL
        if(b.getBoardCell(0) == b.getBoardCell(3) && b.getBoardCell(3) == b.getBoardCell(6)){

        winningPiece = b.getBoardCell(0)
        winningArray = Array(3, {0})
        winningArray[0] = 0
        winningArray[1] = 3
        winningArray[2] = 6

    }
        if(b.getBoardCell(1) == b.getBoardCell(4) && b.getBoardCell(4) == b.getBoardCell(7)){
        winningPiece = b.getBoardCell(1)
        winningArray = Array(3, {1})
        winningArray[0] = 1
        winningArray[1] = 4
        winningArray[2] = 7
    }
        if(b.getBoardCell(2) == b.getBoardCell(5) && b.getBoardCell(5) == b.getBoardCell(8)){
        winningPiece = b.getBoardCell(2)
        winningArray = Array(3, {2})
        winningArray[0] = 2
        winningArray[1] = 5
        winningArray[2] = 8
    }

         //DIAGONAL
         if(b.getBoardCell(6) == b.getBoardCell(4))
                if(b.getBoardCell(4) == b.getBoardCell(2)) {
                winningPiece = b.getBoardCell(6)
                winningArray = Array(3, {0})
                winningArray[0] = 6
                winningArray[1] = 4
                winningArray[2] = 2
            }
        //DIAGONAL
        if(b.getBoardCell(0) == b.getBoardCell(4))
            if(b.getBoardCell(4) == b.getBoardCell(8)) {
            winningPiece = b.getBoardCell(0)
            winningArray = Array(3, {0})
            winningArray[0] = 0
            winningArray[1] = 4
            winningArray[2] = 8
        }


        if(winningPiece == p1.pieceType)
           return WinResult(p1.userId, winningArray!!)


        else if(winningPiece == p2.pieceType)
           return WinResult(p2.userId, winningArray!!)


        return null
    }


    /**
     * Determines if the game is a tie. It does this by checking if
     * one cell is left, and if the current player put their piece their,
     * would it result in a win?
     */
    fun checkTie(board: Board): Boolean{

        var numNonFlipped = 0

        for(a in 0 until board.getBoardLength())
            if(board.getBoardCell(a) == PieceType.NO_PLAYER_STATUS)
                numNonFlipped++

        if(numNonFlipped == 0)
            return true


        return false
    }


    data class WinResult(val playerId: String, val winningPieces: Array<Int>)



}

