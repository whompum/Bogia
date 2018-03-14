package com.younivibes.bogia.Account

/**
 * Created by bryan on 2/28/2018.
 */
interface OnUniqueUsernameResult {

    fun usernameErrorResult(res: Int)
    fun usernameErrorResult(res: Boolean)

}