package com.younivibes.bogia.Account

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Created by bryan on 2/28/2018.
 */
class UsernameChecker(val i: OnUniqueUsernameResult): ValueEventListener {

    override fun onCancelled(c: DatabaseError?) {
        if(c!=null)
            i.usernameErrorResult(c.code)
    }

    override fun onDataChange(d: DataSnapshot?) {


        if(d!=null)
            if(!d.exists()) {
                i.usernameErrorResult(true) //TRUE the username is unique
            }else
                i.usernameErrorResult(false) //FALSE the username is not unique
    }




}