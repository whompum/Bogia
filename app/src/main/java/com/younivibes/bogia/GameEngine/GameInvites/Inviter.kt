package com.younivibes.bogia.GameEngine.GameInvites

import com.younivibes.bogia.GameEngine.Requests.NewGameRequest
import com.younivibes.bogia.GameEngine.GameType

/**
 * Created by bryan on 2/21/2018.
 *
 *
 * invites a User
 *
 */
class Inviter(val inviteResponse: InviteResponse) {

    fun invite(gameRequest: NewGameRequest){
        if(gameRequest.gameType == GameType.LOCAL)
            askLocalPlayer(gameRequest)

        else if(gameRequest.gameType == GameType.REMOTE)
            askRemotePlayer(gameRequest)
    }


    fun askLocalPlayer(gameRequest: NewGameRequest){
        inviteResponse.onAccepted(gameRequest)
    }

    fun askRemotePlayer(gameRequest: NewGameRequest){

    }


}