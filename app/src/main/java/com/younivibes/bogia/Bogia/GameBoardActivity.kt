package com.younivibes.bogia.Bogia

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.*
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.Game.GameRoom.Referee
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler
import com.younivibes.bogia.GameEngine.GameInterface
import com.younivibes.bogia.GameEngine.GameInvites.InviteResponse
import com.younivibes.bogia.GameEngine.GameServer
import com.younivibes.bogia.GameEngine.GameType
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Requests.NewGameRequest
import com.younivibes.bogia.GameUtils.SimpleTextWatcher
import com.younivibes.bogia.Players.AI.AI
import com.younivibes.bogia.Players.AI.AIServer
import com.younivibes.bogia.Players.Player
import com.younivibes.bogia.Players.UserStatsJsonFactory.Companion.getUserFileData

import com.younivibes.bogia.R
import com.younivibes.bogia.Widgets.CellView
import com.younivibes.bogia.Widgets.RTextView
import kotlinx.android.synthetic.main.layout_invite_dialog.*

class GameBoardActivity : AppCompatActivity(), GameServer.GameInitializerObserver{

    companion object {
        const val USER_ID_KEY = "userId.ky"
        const val USER_NAME_KEY = "userName.ky"
        val AI_PLAYER = Player(PieceType.PLAYER_TWO, AI.AI_ID, AI.NAME)

        private fun showStatus(ctx: Context, @StringRes text: CharSequence){
            val theLayout = LayoutInflater.from(ctx).inflate(R.layout.layout_game_status_dialog, null)

            val theD = AlertDialog.Builder(ctx, R.style.StyleDialog)
                    .setView(theLayout)
                    .show()

            theLayout.setOnTouchListener { _, event ->
                if(event.actionMasked == MotionEvent.ACTION_DOWN)
                    if(theD.isShowing)
                        theD.dismiss()

                true
            }

            theLayout.findViewById<RTextView>(R.id.id_status_ontainer)
                    .text = text

        }

    }

    private lateinit var storage: StorageReference
    private lateinit var userId: String
    private lateinit var userName: String

    private lateinit var us: Player
    private lateinit var them: Player

    private var gameHandler: ClientGameHandler? = null
    private lateinit var bogiaFragment: BogiaFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        userId  = intent.getStringExtra(USER_ID_KEY)
        userName = intent.getStringExtra(USER_NAME_KEY)

        storage = FirebaseStorage.getInstance().getReference(getUserFileData(userId))

        var userStats: String = "" //Json string of the user stats

        val fetchStatsTask = FirebaseStorage
                               .getInstance()
                               .getReference(getUserFileData(userId))
                               .getBytes(1024*1024)

        fetchStatsTask.addOnSuccessListener {
            userStats = String(it)
        }

        fetchStatsTask.addOnFailureListener {
            /*Do something*/
        }


        val args = Bundle()
            args.putString(BogiaFragment.PLAYER_NAME_KEY, userName)
            args.putString(BogiaFragment.PLAYER_ID_KEY, userId)
            args.putString(BogiaFragment.PLAYER_STATS_KEY, userStats)

        bogiaFragment = BogiaFragment.newInstance(args, UserInterfaceListener()) as BogiaFragment

        supportFragmentManager.beginTransaction().add(R.id.container, bogiaFragment).commit()
    }


    /**
     * Called when the connection to the game is established
     */
    override fun onConnectionEstablished(cgh: ClientGameHandler) {
        gameHandler = cgh
        gameHandler?.bindCallbacks(GameEventListener())
    }

    private fun areWePlayerOne(): Boolean = us.pieceType == PieceType.PLAYER_ONE

    private fun createPlayer(pieceType: PieceType){
        us = Player(pieceType, userId, userName)
    }

    private fun findPeople(){
        /**
         * Launches a Dialog to search a users name
         * And then handles all business logic associated with that action
         */

        val theD = AlertDialog.Builder(this)
                .setView(findPeopleLayout())
                .create()

        theD.requestWindowFeature(Window.FEATURE_NO_TITLE)

        theD.show()
    }

    private fun findPeopleLayout(): View{
        val theDLook = LayoutInflater.from(this).inflate(R.layout.layout_invite_dialog, null)

        theDLook.findViewById<EditText>(R.id.id_search_editor)
            .addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable?) {
                    id_dialog_invite_button.isEnabled = s?.toString()?.isNotEmpty()!!
                }
            })

        theDLook.findViewById<RTextView>(R.id.id_dialog_invite_button)
                .setOnClickListener{
                    if(canSearchUser(id_search_editor.text))
                        inviteUser(id_search_editor.text)
                }


    return theDLook
    }


    /**
     * @return whether we can search the user or not (Can't if, no internet, or if the user doesn't exist, or is offline, or whateva)
     */
    private fun canSearchUser(username: CharSequence): Boolean{
        return false
    }



    private fun inviteUser(username: CharSequence) {

    }


    /**
     * Listens to the input events from the fragment
     */
    inner class UserInterfaceListener: UIListener{
        override fun onExitSelected() {

            AlertDialog.Builder(this@GameBoardActivity, R.style.StyleDialog)
                    .setTitle(R.string.signout_alert_title)
                    .setMessage(R.string.signout_alert_msg)
                    .setNegativeButton(android.R.string.no, { dialog, which ->
                        dialog.dismiss()
                    })
                    .setPositiveButton(android.R.string.yes, { dialog, which ->
                        FirebaseAuth.getInstance().signOut()

                        val intent = Intent(this@GameBoardActivity, LoginSignupActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        startActivity(intent)
                        finish()
                    })
                    .show()
        }

        override fun onMatchMakingOption() {
            createPlayer(PieceType.PLAYER_ONE)
        }

        override fun onInviteOption(playerName: String) {
            createPlayer(PieceType.PLAYER_ONE)
            findPeople()
        }

        override fun onAIOption() {
           createPlayer(PieceType.PLAYER_ONE) //We're number 1!

           val gameReq = NewGameRequest(GameType.LOCAL, us,  AI_PLAYER)

           GameServer(this@GameBoardActivity).requestNewLocalGame(gameReq, LocalResponseHandler())
           AIServer().startServer(gameReq) //Starts the AI's GameServer
        }

        override fun onCellTapped(cell: CellView) {
            if(gameHandler != null)
            if(gameHandler?.isGameActive!!)
                gameHandler?.handleRequest(MoveRequest(us, cell.index.index))
        }
    }


    /**
     * Pretty much handles events from the game, and then
     * calls various BogiaFragment methods about it
     *
     * NOTE: this is poor OOD since BogiaFragment, an GBA are very high coupled
     */
    inner class GameEventListener: GameInterface(){
        override fun newCountDown(timeLeft: Long) {
            bogiaFragment.updateCountDown(timeLeft)
        }

        override fun newMatchTimer(timeLeft: Long) {
            bogiaFragment.updateMatchTimer(timeLeft)
        }

        override fun onGameStateChanged(newState: Int) {
            if(newState == GameRoom.GAME_STATE_STARTED)
                bogiaFragment.toggleTimelessDisplay(PieceType.PLAYER_ONE)
        }

        override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int) {
            bogiaFragment.updateCell(index, lastPlayer.pieceType)

            if(gameHandler?.isGameActive!!)
                bogiaFragment.toggleTimelessDisplay(newCurrentPlayer)
        }

        override fun gameWon(winResult: Referee.WinResult, loser: Player) {
            bogiaFragment.animateOnWin(winResult.winningPieces, 0)

            if(loser.userId == us.userId)
                showStatus(this@GameBoardActivity, getString(R.string.you_lost))
            else
                showStatus(this@GameBoardActivity, getString(R.string.you_won))

        }

        override fun gameTied() {
            // *Do things to do when tied*
            showStatus(this@GameBoardActivity, getString(R.string.game_tied))
            bogiaFragment.reset()
            bogiaFragment.resetBoard()
        }
    }


    /**
     * All objects of this type will bind the BogiaFragment
     * But in case they want mas logic they can extend
     *
     * HIGHLY COUPLED BTW :(
     *
     */
    abstract inner class InviteResponseHandler: InviteResponse{
        @CallSuper
        override fun onAccepted(gameRequest: NewGameRequest) {
            them = gameRequest.requestedPlayer
            bogiaFragment.bindPlayers(gameRequest.requestingPlayer, gameRequest.requestedPlayer, areWePlayerOne())
        }

        abstract override fun onDeclined(gameRequest: NewGameRequest)
    }

    /**
     *Handles local invitatations.
     **/
    inner class LocalResponseHandler: InviteResponseHandler(){
        override fun onDeclined(gameRequest: NewGameRequest) {

        }
    }
}






