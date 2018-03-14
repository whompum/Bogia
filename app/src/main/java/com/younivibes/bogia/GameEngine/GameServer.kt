package com.younivibes.bogia.GameEngine

import android.content.Context
import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler
import com.younivibes.bogia.GameEngine.GameInvites.InviteResponse
import com.younivibes.bogia.GameEngine.GameInvites.Inviter
import com.younivibes.bogia.GameEngine.GameEngines.LocalGameEngine
import com.younivibes.bogia.GameEngine.GameEngines.OnSyncServerInit
import com.younivibes.bogia.GameEngine.Requests.NewGameRequest


/**
 * Entry point into my game engine. This guys job is pretty simple:
 * He gets the underlying game engine going for the client, and returns a entry
 * point to the game system
 */
class GameServer(g: GameInitializerObserver): OnSyncServerInit {

    lateinit var gameInitializerObserver: GameInitializerObserver

    init{ registerInitializerObserver(g) }

    var requestInProgress = false

    fun requestNewRemoteGame(gameRequest: NewGameRequest){
        /**
         * Requests a new remote game
         * How it does this is send a push notif request to the server where it will send
         * it to the user. If the user responds, our registered callbacks will be triggered
         * Which will either start a notify the client, comienzo un nuevo juego, establish connection
         */

    }

    fun requestNewLocalGame(gameRequest: NewGameRequest, inviteResponse: InviteResponse){
        Inviter(object : InviteResponse {
            override fun onAccepted(gameRequest: NewGameRequest) {
                inviteResponse.onAccepted(gameRequest)
                LocalGameEngine(this@GameServer, gameRequest.requestingPlayer, gameRequest.requestedPlayer)
            }

            override fun onDeclined(gameRequest: NewGameRequest) {
                //Will never happen :)
            }
        }).invite(gameRequest)
    }

    /**
     * All the handle methods do is set up our game engine and give us the interface
     * to the engine
     */
    fun handleRemoteGameInvite(gameRequest: NewGameRequest){

    }

    fun handleLocalGameInvite(gameRequest: NewGameRequest){
        LocalGameEngine(this, gameRequest.requestedPlayer, gameRequest.requestingPlayer)
    }


    fun registerInitializerObserver(o: GameInitializerObserver){
        gameInitializerObserver = o
    }



    override fun onServerInitialized(g: ClientGameHandler) {
        gameInitializerObserver.onConnectionEstablished(g)
    }

    /**
     * Interface called when the GameEngine is created, and running
     * Returns a proxy to said server
     */
    interface GameInitializerObserver{
        fun onConnectionEstablished(cgh: ClientGameHandler)
    }

}