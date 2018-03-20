package com.younivibes.bogia.Bogia

import com.younivibes.bogia.Widgets.CellView

/**
 * Created by bryan on 3/18/2018.
 *
 * DUMP CLASS for all user interactions
 *
 */
interface UIListener {

    fun onExitSelected()
    fun onMatchMakingOption()
    fun onInviteOption(playerName: String)
    fun onAIOption()
    fun onCellTapped(cell: CellView)

}