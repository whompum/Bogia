package com.younivibes.bogia.Widgets

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.younivibes.bogia.R


/**
 * Created by bryan on 12/2/2017.
 */

class FunPopup
/**
 * Instantiate the Popup with an already styled TextView
 *
 * @param popupContent The textview
 * @param msg The messasge the display in the TextView; Be aware there exist a contract
 * If the client passes null as the msg variable, that is legal PROVIDED
 * the TextView already has text in it. Else if getText() and msg are null
 * an exception will be thrown
 */
private constructor(popupContent: ViewGroup, msg: CharSequence?) : PopupWindow(popupContent, SIZE, SIZE) {


    private var duration = DEF_DURATION
    private var fadeDuration = DEF_FADE_DURATION


    private var fadeHandler: Handler? = null
    private var fadeRunnable: Runnable? = null

    private var hasStarted = false
    private var hasEnded = false


    private val dad = Handler.Callback { message ->
        //Set the alpha value of content view to the compute view in the Handler
        this@FunPopup.contentView.alpha = message.obj as Float

        //Get the elapsed time (arg1) and check if equal to fadeDuration. If so, remove the runnable callback, and kill
        if (message.arg1.toLong() == fadeDuration) {
            onKill()
            return@Callback true
        }
        true
    }


    /**
     * Bare-Bones constructor
     *
     * @param context inflates TextView
     * @param msg CAN NOT BE NULL else the TextView will have no message to it
     */
    private constructor(context: Context, msg: CharSequence) : this(LayoutInflater.from(context).inflate(CONTENT_ID, null, false) as ViewGroup, msg)


    init {

        val content = popupContent.findViewById<TextView>(R.id.popupTextView)



        if (msg != null)
            content.setText(msg)
        else
            throw IllegalArgumentException(MSG_IAE)


        isOutsideTouchable = false
        isTouchable = false
        isFocusable = false

        if (Build.VERSION.SDK_INT >= 22)
            isAttachedInDecor = true

        setBackgroundDrawable(null)

    }


    /**
     * Change the background color of the View
     *
     * @param color the color to set to the background
     */
    fun changeBackgroundColor(color: Int) {
        if (contentView != null)
            contentView.setBackgroundColor(color)
    }

    /**
     * LIFECYCLE
     * Starts the fade animation, but with a delay of duration.
     * Hands the Fade object a fadeDuration that will say how long to run
     * after duration is delayed.
     */
    protected fun onStart() {

        if (hasEnded)
        //Used when onKill() is called, but i want to re-show this
            bringMeBackToLife()

        this.fadeHandler = Handler(dad)
        this.fadeRunnable = Fade(fadeDuration, fadeHandler!!)
        fadeHandler!!.postDelayed(fadeRunnable, duration)

        hasStarted = true
        hasEnded = false
    }

    /**
     * LIFECYCLE
     *
     * TODO: FIX. DOESN'T WORK RIGHT! onRestart()#FunPopup.class
     */
    protected fun onRestart() {
        bringMeBackToLife()
        onStart()
    }


    /**
     * LIFECYCLE
     */
    protected fun onKill() {
        endFade()
        hasEnded = true
        hasStarted = false
        dismiss()
    }


    private fun bringMeBackToLife() {
        if (contentView != null)
            contentView.alpha = Fade.VALUE
    }

    private fun endFade() {
        this.fadeHandler!!.removeCallbacks(fadeRunnable)
    }


    fun haveMoreFun() {
        onRestart()
    }

    fun killFun() {
        onKill()
    }


    fun setLingerDuration(duration: Long): FunPopup {

        if (hasStarted)
            Log.i("NOTIFICATIONS", "FunPopup Already Started \n" + "" +
                    "Changes will take effect if restarted.")


        this.duration = duration
        return this
    }

    fun setFadeDuration(fadeDuration: Long): FunPopup {

        if (hasStarted)
            Log.i("NOTIFICATIONS", "FunPopup already started \n" + "" +
                    "Changes will take effect if restarted.")

        this.fadeDuration = fadeDuration
        return this
    }

    fun haveFun(parent: View) {
        this.haveFun(parent, Gravity.NO_GRAVITY)
    }

    fun haveFun(parent: View, gravity: Int, xOffset: Int, yOffset: Int) {

        val xLoc = parent.x.toInt() + xOffset
        val yLoc = parent.y.toInt() + yOffset

        super.showAtLocation(parent, gravity, xLoc, yLoc)

        this.onStart()
    }

    /*****
     *
     *
     *
     *
     * PLEASE BE AWARE THAT THIS CODE IS MODIFIED TO SUITE
     * SOME BUGS IN MY APP. iT IS NOT PRODUCTION CODE FOR AN
     * OPEN SOURCE CODE BASE!
     */
    fun haveFun(parent: View, gravity: Int) {
        val locations = IntArray(2)
        parent.getLocationOnScreen(locations)

        this.haveFun(parent, gravity, locations[0], locations[1]+parent.height)
    }

    fun haveFun(parent: View, xOffset: Int, yOffset: Int) {
        this.haveFun(parent, Gravity.NO_GRAVITY, xOffset, yOffset)
    }

    /**
     * Computes an alpha value to fades away the popup
     * @ specified intervals.
     * Then it posts itself as a delayed message to the handler where it is called, and computes another value
     */
    private class Fade
    /**
     *
     * @param fadeDuration How long the anim should last
     * @param handler The handler that started it
     */
    constructor(fadeDuration: Long, private val handler: Handler) : Runnable {

        private var fadeDuration = FADE_DURATION_DEF
        private var elapsedTime = 0L

        private var value: Float? = VALUE

        init {
            if (fadeDuration % CYCLE != 0L)
            //If not a perfect divisor for 100, don't set it
                Log.i("NOTIFICATIONS", "Fade-IMPL @ class FunPopup: \n" +
                        "Fade Duration must be greater than a 1000 with 100 evenly dividing into it. \n" +
                        "Fade Duration: " + fadeDuration.toString())
            else
                this.fadeDuration = fadeDuration
        }

        override fun run() {
            //Increment elapsed time by CYCLE
            elapsedTime += CYCLE

            val message = Message()

            computeValue()

            //Store alpha value in Float and hand to handler (Re-use an object in case this method is run a lot)
            message.obj = value
            message.arg1 = elapsedTime.toInt()

            handler.sendMessage(message)
            handler.postDelayed(this, CYCLE)
        }

        /**
         * Computes a value based on elapsed time / duration / and the alpha value
         *
         * VALUE is the percentage of the time left relative to duration,
         * to its own cieling and floor value
         *
         */
        private fun computeValue() {
            value = (fadeDuration - elapsedTime).toFloat() / fadeDuration
            Log.i(DEBUG, "FADE VALUE: " + value.toString())
        }

        companion object {
            val DEBUG = "Fade"
            val FADE_DURATION_DEF: Long = 500
            val CYCLE = 50L //Every 100 MS deliver an updated value to handler
            val VALUE = 1.0f
        }

    }

    companion object {

        val DEBUG = "FunPopup"

        val MSG_IAE = "The TextView Has No Text :0"

        val DEF_DURATION = 4000L
        val DEF_FADE_DURATION = 1000L

        val SIZE = ViewGroup.LayoutParams.WRAP_CONTENT

        @LayoutRes
        val CONTENT_ID = R.layout.fun_popup_textview


        /**
         * Barebones method to use this class. Just a String and a Context
         * and this class will take care of all inflation styling and whatnot
         *
         * @param context You know...
         * @param msg msessage displayed in the TextView
         * @return again... the popup...
         */
        fun play(context: Context, msg: CharSequence): FunPopup {
            return FunPopup(context, msg)
        }

        /**
         * Utility method of play(Context ,Charsequence) where the client wants a String resource instead
         *
         * @param context inflates the views
         * @param msgId R.string value
         * @return pop..... UP!
         */
        fun play(context: Context, @StringRes msgId: Int): FunPopup {
            return play(context, resolveStringResource(context, msgId))
        }


        /**
         * Utility method to resolve a String resource and convert to a CharSequence :)
         * @param context used to generate the string from the R.* file
         * @param id the R resource id
         * @return  whatever value has 'id'
         */
        private fun resolveStringResource(context: Context, @StringRes id: Int): CharSequence {
            return context.getString(id)
        }
    }


}