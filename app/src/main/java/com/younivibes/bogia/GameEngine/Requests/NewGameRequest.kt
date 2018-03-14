package com.younivibes.bogia.GameEngine.Requests

import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/19/2018.
 */
data class NewGameRequest(val gameType: Int, val requestingPlayer: Player, val  requestedPlayer: Player)