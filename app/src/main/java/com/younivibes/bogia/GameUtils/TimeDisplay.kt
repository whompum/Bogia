package com.younivibes.bogia.GameUtils

/**
 * Created by bryan on 2/23/2018.
 */
class TimeDisplay {

    companion object {

        private val timeFormatter = StringBuilder()

        fun format(m: Long): String{
            timeFormatter.delete(0, timeFormatter.length)

            val min = m / 1000 / 60
            val sec = m / 1000 % 60

            var minText = "$min:"
            var secText = "$sec"

            if(sec < 10)
                secText = "0$sec"

          return  minText+secText
        }

    }

}