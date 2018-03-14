package com.younivibes.bogia.Account

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.younivibes.bogia.FirebaseSchema
import com.younivibes.bogia.Players.UserStats
import com.younivibes.bogia.Players.UserStatsJsonFactory
import java.lang.Exception

/**
 * Created by bryan on 2/28/2018.
 *
 *
 * Handles the Signup process
 * Doesn't make gaurantees about data conditionals
 * E.G. if the  password contains non-numeric characters
 * and is at least X length, etc, etc
 *
 *
 * TODO make updating the available status a separate object
 * TODO Initialize available_players in one transaction
 *
 */
class AccountSignup(val account: Account, val resultListener: AccountSignupResult): OnUniqueUsernameResult {

    companion object {
        const val RESULT_USERNAME_IN_USE = 0
    }

    val root = FirebaseDatabase.getInstance().reference
    val playerRef = root.child(FirebaseSchema.PLAYERS_NODE).child(account.username)

    /**
     * Beginning of the signup process. First it checks if the
     * requested username is able to be used. If not, then it will
     * post an error message to its listener
     */
    fun signup(){
        playerRef.addListenerForSingleValueEvent(UsernameChecker(this))
    }

    override fun usernameErrorResult(res: Int) {
        resultListener.onResult(res)
    }

    //Given a boolean true if we  can use the name
    override fun usernameErrorResult(res: Boolean) {
            if(res){
                val task = createUser()
                task.addOnFailureListener { onAccountCreationFailure(it)  }
                task.addOnCompleteListener { onAccountCreated(it) }
            }else
                resultListener.onResult(RESULT_USERNAME_IN_USE)
    }


    private fun createUser(): Task<AuthResult> {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(account.email, account.password)
    }

    private fun saveUser(task: Task<AuthResult>){
        val user = task.result.user

        //Saves the username to the User
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(account.username).build())

        //Inserts the User into the global players database (for future checking when creating new users)
        FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebaseSchema.PLAYERS_NODE)
                .child(account.username)
                .setValue(user.uid)
                .addOnFailureListener {resultListener.onResult(it.localizedMessage)}

        //Initialize the players states
        FirebaseStorage
                .getInstance()
                .getReference(UserStatsJsonFactory.getUserFileData(user.uid))
                .putBytes(UserStatsJsonFactory.statsToJson(UserStats()).toByteArray())
                .addOnFailureListener {resultListener.onResult(it.localizedMessage)}

        resultListener.onUserSaved(user)
    }


    fun onAccountCreationFailure(e: Exception) {
         Log.i("SIGNUP", "We've failed at creating a new account")
         resultListener.onResult(e.localizedMessage)
    }


    fun onAccountCreated(task: Task<AuthResult>) {
          Log.i("SIGNUP", "We've successfully created a new account: ${task.isSuccessful}")
          if(task.isSuccessful) saveUser(task)
    }


}