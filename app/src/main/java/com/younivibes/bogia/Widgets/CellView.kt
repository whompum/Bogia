package com.younivibes.bogia.Widgets

import android.content.Context
import android.support.annotation.AnimRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.R
import org.jetbrains.annotations.Nullable

/**
 * Created by bryan on 3/18/2018.
 */
class CellView(ctx: Context, attrs: AttributeSet, styleRes: Int): View(ctx, attrs, styleRes) {

    companion object {
        @AnimRes
        const val TOUCH_ANIM = R.anim.shrink_grow

        @DrawableRes
        const val DEF_PIECE_TYPE = R.drawable.circle_silver

        @DrawableRes
        const val PLAYER_ONE_PIECE_TYPE = R.drawable.circle_green

        @DrawableRes
        const val PLAYER_TWO_PIECE_TYPE = R.drawable.circle_red


    }

    private lateinit var touchAnim: Animation
    lateinit var index: Index

    constructor(ctx: Context,@Nullable attrs: AttributeSet): this(ctx, attrs, 0){
        touchAnim = AnimationUtils.loadAnimation(ctx, TOUCH_ANIM)

        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.CellView)

        for(i in 0 until typedArray.indexCount)
            if(typedArray.getIndex(i) == R.styleable.CellView_index)
                index = Index(typedArray.getInteger(i, -1))

        setPieceType(PieceType.NO_PLAYER_STATUS)

        typedArray.recycle()
    }


    fun doAnimation(){
        startAnimation(touchAnim)
    }

    fun setPieceType(pieceType: PieceType){
        when(pieceType){
            PieceType.PLAYER_ONE -> setBackgroundResource(PLAYER_ONE_PIECE_TYPE)
            PieceType.PLAYER_TWO -> setBackgroundResource(PLAYER_TWO_PIECE_TYPE)
            PieceType.NO_PLAYER_STATUS -> setBackgroundResource(DEF_PIECE_TYPE)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event?.actionMasked == MotionEvent.ACTION_DOWN)
            doAnimation()

        return super.onTouchEvent(event)
    }


    data class Index(val index: Int)
}