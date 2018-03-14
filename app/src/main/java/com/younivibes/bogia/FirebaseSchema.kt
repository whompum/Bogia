package com.younivibes.bogia

/**
 * Created by bryan on 2/27/2018.
 */
class FirebaseSchema {

    companion object {

        const val PLAYERS_NODE = "players"
        const val NAME = "name"
        const val UID = "userId"

        const val AVAILABLE_USERS = "onlineUsers"
        const val IS_SEARCHING = "isSearching"
        const val IS_IN_GAME= "isInGame"
    }


    /**
     * DB SCHEMA FOR ALL USERS IN APP
     *
     * -ROOT
     * --USERS
     * ---NAME
     * ----UID
     *
     *
     * DB SCHEMA FOR AVAILABLE PLAYERS
     * -ROOT
     * --AVAILABLE_PLAYERS
     * ---NAME
     * ----UID
     * ----IS_ONLINE
     * ----IS_IN_GAME
     *
     */

}