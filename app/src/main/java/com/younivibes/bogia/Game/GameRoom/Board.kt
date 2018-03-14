package com.younivibes.bogia.Game.GameRoom

import android.support.annotation.IntRange
import com.younivibes.bogia.Game.PieceType

/**
 * Created by bryan on 2/18/2018.
 *
 * Contains the Board Array, and manipulates it
 *
 */
class Board {

    companion object BoardUtils {
        val ROWS = 3
        val COLS = 3
    }

    val board = Array(ROWS* COLS, { GamePiece() })

    fun setBoardCell(pieceType: PieceType,@IntRange(from = 0 , to = 8) index: Int){
        board[index].pieceType = pieceType
    }

    fun getBoardCell(@IntRange(from = 0, to = 8) index: Int): PieceType = board[index].pieceType

    fun getBoardLength(): Int = board.size

}