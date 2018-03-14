package com.younivibes.bogia.GameEngine.GameInvites

import com.younivibes.bogia.GameEngine.Requests.NewGameRequest

/**
 * Created by bryan on 2/21/2018.
 */
interface InviteResponse {

    fun onAccepted(gameRequest: NewGameRequest)
    fun onDeclined(gameRequest: NewGameRequest)

}