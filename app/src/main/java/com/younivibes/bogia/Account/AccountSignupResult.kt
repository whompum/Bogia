package com.younivibes.bogia.Account

import com.google.firebase.auth.FirebaseUser

/**
 * Created by bryan on 2/28/2018.
 */
interface AccountSignupResult {

    fun onResult(res: Int)
    fun onResult(res: String)
    fun onUserSaved(user: FirebaseUser)
}