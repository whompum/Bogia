package com.younivibes.bogia.GameEngine.GameEngines

import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/22/2018.
 *
 * Test class
 *
 * works like a charm. Still didnt fix my bug though :/
 *
 */
class LocalGameDataServer private constructor () {

    val clients = ArrayList<T>(2)

    companion object {

        var instace: LocalGameDataServer? = null

        fun getInstance(): LocalGameDataServer{
            if(instace == null)
                instace = LocalGameDataServer()

            val temp = instace

            return temp!!
        }

    }

    fun registerToClientList(t: T){
        clients.add(t)
    }

    fun sendData(req: MoveRequest) {

        var sender: T? = null
        var reciever: T? = null
        clients.forEach {
            if (it.player.userId == req.player.userId) sender = it
        }

        if (sender != null) {
            clients.forEach {
                if (sender?.playerTo == it.player)
                    reciever = it
            }
        }
        reciever?.d?.handleData(req)
    }
    interface ServerDataHandler{
        fun handleData(req: MoveRequest)
    }

    class T(var player: Player, val playerTo: Player, val d: ServerDataHandler)


}