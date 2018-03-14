package com.younivibes.bogia.Players

/**
 * makes / parses JSON files using google GSON
 */

import com.google.gson.Gson

class UserStatsJsonFactory {


    companion object {
        //Well that was hard...

        const val USER_STATS = "userStats.txt"


        fun getUserFileData(userId: String): String{
               return "users/$userId/${USER_STATS}"
           }

        fun statsToJson(data: UserStats): String{
            return Gson().toJson(data, UserStats::class.java)
        }

        fun jsonToStats(data: String): UserStats {
          return Gson().fromJson<UserStats>(data, UserStats::class.java)
        }

    }


}