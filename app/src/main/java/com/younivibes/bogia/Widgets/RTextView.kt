package com.younivibes.bogia.Widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.younivibes.bogia.R

/**
 * Created by bryan on 2/14/2018.
 */
class RTextView: TextView{

    private val LIGHT = "RLight.ttf"
    private val REGULAR = "RRegular.ttf"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init(context!!, attrs)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init(context!!, attrs)}

    private fun init(ctx: Context, attrs: AttributeSet?){

        val fontArr = context.obtainStyledAttributes(attrs, R.styleable.RTextView)

        var fontEnum = 0

        for(i in 0 until fontArr.indexCount)
            if(fontArr.getIndex(i) == R.styleable.RTextView_RFonts)
                fontEnum = fontArr.getIndex(fontArr.getIndex(i))

        fontArr.recycle()

        val fontPath = resolveTypefaceAttr(fontEnum)

        typeface = resolveTypeface(fontPath, ctx)
    }

    private fun resolveTypefaceAttr(font: Int): String = when(font){ 0 -> LIGHT 1-> REGULAR else -> ""}
    private fun resolveTypeface(typefaceLocation: String, ctx: Context):Typeface = Typeface.createFromAsset(ctx.assets, typefaceLocation)

}