package com.younivibes.bogia.GameEngine.Requests

import com.younivibes.bogia.GameEngine.Requests.Request
import com.younivibes.bogia.Players.Player

/**
 * Created by bryan on 2/20/2018.
 */
data class MoveRequest(val player: Player, var index: Int): Request