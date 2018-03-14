/*
 * Copyright 2017 Bryan A. Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.younivibes.bogia.Widgets

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.younivibes.bogia.R

/**
 * Created by bryan on 12/6/2017.
 */

class TimelessWave : View {


    init {
        initBackground()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, set: AttributeSet) : super(context, set)
    constructor(context: Context, set: AttributeSet, defStyle: Int) : super(context, set, defStyle)

    private fun initBackground() {

        if (Build.VERSION.SDK_INT >= 21)
            background = context.getDrawable(R.drawable.timeless_wave_animator)
        else
            background = context.resources.getDrawable(R.drawable.timeless_wave_animator)
    }


    fun start() {
        (background as AnimationDrawable).start()
    }


    fun stop() {
        (background as AnimationDrawable).stop()
    }


    /**
     * Since we add the drawable progmatically, the size of the view can't be determined
     * Thus if we pass wrap_content, it will take up all the size it can (we're extending view thats why)
     * To get around this, we simply fetch the wanted size of the Drawable,
     * and pass that to the super implementation along with the clients LayoutParams mode
     * passing along these values is better than setting them ourselves because we can
     * let the view handle padding and what not
     *
     * @param widthMeasureSpec holds the LayoutParams mode we wanted
     * @param heightMeasureSpec holds the LayoutParams mode we wanted
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = background.intrinsicWidth
        val height = background.intrinsicHeight

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, widthMode), View.MeasureSpec.makeMeasureSpec(height, heightMode))

    }


}