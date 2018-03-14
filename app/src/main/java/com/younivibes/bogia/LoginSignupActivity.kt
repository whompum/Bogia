package com.younivibes.bogia

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.younivibes.bogia.Account.Account
import com.younivibes.bogia.Account.AccountSignup
import com.younivibes.bogia.Account.AccountSignupResult
import com.younivibes.bogia.Players.UserStats
import com.younivibes.bogia.Players.UserStatsJsonFactory
import com.younivibes.bogia.Players.UserStatsJsonFactory.Companion.getUserFileData

import com.younivibes.bogia.Widgets.FunPopup

import kotlinx.android.synthetic.main.layout_login_signup.*
import java.lang.Exception

//TODO add anonymous functionality (offline mode)
class LoginSignupActivity: AppCompatActivity(), AccountSignupResult {

    val firebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = firebaseAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        if(user != null)
            gameOut(user, false)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_login_signup)

        firebaseAuth.useAppLanguage()

        id_login_button.setOnClickListener(login())
        id_signup_button.setOnClickListener(signup())

    }

    private fun login(): (View) -> Unit{
        return {
            if(checkConditions()){
                    val task = signInUser()
                    task.addOnFailureListener(OnAccountTaskFailListener())
                    task.addOnCompleteListener(onSignInTaskCompleted())
            }
        }
    }


    private fun signup(): (View) -> Unit{
        return {
            if(checkConditions())
               AccountSignup(generateAccountInfo(), this).signup()
        }
    }

    private fun generateAccountInfo(): Account{
        return Account(
                id_username_editor.text.toString(),
                id_email_editor.text.toString(),
                id_password_editor.text.toString()
        )
    }

    inner class onSignInTaskCompleted : OnCompleteListener<AuthResult>{
        override fun onComplete(task: Task<AuthResult>) {
            if(task.isSuccessful)
                if(checkUsername(task.result.user, id_username_editor.text.toString()))
                    gameOut(task.result.user, false)
                else
                    displayStatus(getString(R.string.wrong_username))
        }
    }

    inner class OnAccountTaskFailListener : OnFailureListener{
        override fun onFailure(e: Exception) {
            displayStatus(e.localizedMessage)
        }
    }

    private fun diGracias(){
        Toast.makeText(this, R.string.thankyou, Toast.LENGTH_SHORT).show()
    }

    private fun signInUser(): Task<AuthResult>{
       return firebaseAuth.signInWithEmailAndPassword(id_email_editor.text.toString(),
                id_password_editor.text.toString())
    }

    private fun displayStatus(msg: String){

        status_display.text = msg
        status_display.visibility = View.VISIBLE

        status_display.animate().alpha(0F).setDuration(3500L)
                .setListener(object: Animator.AnimatorListener{
                    override fun onAnimationEnd(animation: Animator?) {
                        status_display.visibility = View.GONE
                        status_display.alpha = 1F
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })

    }

    private fun checkUsername(user: FirebaseUser, username: String): Boolean = (user.displayName.equals(username))

    private fun gameOut(user: FirebaseUser, isNewUser: Boolean) {

        val intent = Intent(this, GameBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS

        intent.putExtra(GameBoardActivity.USER_ID_KEY, user.uid)

        if(!isNewUser)
            intent.putExtra(GameBoardActivity.USER_NAME_KEY, user.displayName)

        else
            intent.putExtra(GameBoardActivity.USER_NAME_KEY, id_username_editor.text.toString())


        startActivity(intent)
    }

    private fun checkConditions(): Boolean{

        var isGood = true

        val userNameText = id_username_editor.text.toString()
        val emailText = id_email_editor.text.toString()
        val passwordText = id_password_editor.text.toString()


       if(!checkTextLength(userNameText, 10)){
           launchFunPopup(R.string.username_length_error, id_username_editor)
           isGood = false
       }
       if(!checkTextLength(passwordText, 10)){
           launchFunPopup(R.string.password_length_error, id_password_editor)
           isGood = false
       }
       if(TextUtils.isEmpty(userNameText)){
           launchFunPopup(R.string.empty_text_error, id_username_editor)
           isGood = false
       }
       if(TextUtils.isEmpty(passwordText)){ //BUG.. LET THIS CODE RUN AND YOU'LL SEE HOW THE POPUP DISPLAYS IN THE WRONG POSITION.
           launchFunPopup(R.string.empty_text_error, id_password_editor)
           isGood = false
       }
       if(TextUtils.isEmpty(emailText)){
           launchFunPopup(R.string.empty_text_error, id_email_editor)
           isGood = false
       }
    return isGood
    }
    private fun checkTextLength(txt: String, N: Int): Boolean = (txt.length <= N)
    private fun launchFunPopup(msg: Int, anchor: View){
        FunPopup.play(this, msg).haveFun(anchor)
    }

    override fun onUserSaved(user: FirebaseUser) {
        diGracias()
        gameOut(user, true)
    }

    override fun onResult(res: Int) {

    }

    override fun onResult(res: String) {
        displayStatus(res)
    }
}










