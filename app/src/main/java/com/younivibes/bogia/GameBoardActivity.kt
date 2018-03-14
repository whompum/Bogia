package com.younivibes.bogia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IntRange
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.younivibes.bogia.Game.*
import com.younivibes.bogia.Game.GameRoom.GamePiece
import com.younivibes.bogia.Game.GameRoom.GameRoom
import com.younivibes.bogia.Game.GameRoom.Referee
import com.younivibes.bogia.GameEngine.GameHandlers.ClientGameHandler
import com.younivibes.bogia.GameEngine.GameInterface
import com.younivibes.bogia.GameEngine.GameInvites.InviteResponse
import com.younivibes.bogia.GameEngine.GameServer
import com.younivibes.bogia.GameEngine.GameType
import com.younivibes.bogia.GameEngine.Requests.MoveRequest
import com.younivibes.bogia.GameEngine.Requests.NewGameRequest
import com.younivibes.bogia.GameUtils.TimeDisplay
import com.younivibes.bogia.Players.*
import com.younivibes.bogia.Players.AI.AI
import com.younivibes.bogia.Players.AI.AIServer
import com.younivibes.bogia.Players.UserStatsJsonFactory.Companion.getUserFileData
import com.younivibes.bogia.Widgets.RTextView

import kotlinx.android.synthetic.main.activity_game_board.*
import kotlinx.android.synthetic.main.layout_board.*
import kotlinx.android.synthetic.main.layout_count_down.*
import kotlinx.android.synthetic.main.layout_match_players.*
import kotlinx.android.synthetic.main.layout_match_timer.*
import kotlinx.android.synthetic.main.layout_user_actions_main.*
import java.util.concurrent.TimeUnit


class GameBoardActivity : AppCompatActivity(), GameServer.GameInitializerObserver{

    companion object {
        val USER_ID_KEY = "userId.ky"
        val USER_NAME_KEY = "userName.ky"
    }

    lateinit var storage: StorageReference
    lateinit var userId: String
    lateinit var userName: String

    lateinit var userStats: UserStats

    lateinit var countDownDisplay: View
    lateinit var matchTimerDisplay: View

    var gameHandler: ClientGameHandler? = null

    lateinit var aiServer: AIServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)

        id_drawer_toggle.setOnClickListener(handleDrawerToggle())

        for(index in 0 until id_board.childCount) {
            val v: View = id_board.getChildAt(index)
            v.setOnClickListener(handleBoardClick())
        }

        id_sign_out.setOnClickListener(signOut())
        id_ai.setOnClickListener(onAiMatchSelected())
        id_search_match.setOnClickListener(onInviteMatchSelected())
        id_random_match.setOnClickListener(onRandomMatchSelected())

        userId  = intent.getStringExtra(USER_ID_KEY)
        userName = intent.getStringExtra(USER_NAME_KEY)

        storage = FirebaseStorage.getInstance().getReference(getUserFileData(userId))

        id_drawer_you.text = userName

        initUserStats()
        initTimers()



    }

    private fun onAiMatchSelected(): (View) -> Unit{
        return {
            closeDrawer(Gravity.START)
            val gameRequest = NewGameRequest(GameType.LOCAL, fetchOurLocalPlayer(), fetchAIPlayer())
            deliverNewGameRequest(gameRequest)
            aiServer = AIServer()
            aiServer.startServer(gameRequest)
        }
    }




        //TODO FIX THE SHIT OUT OF THIS METHOD
    private fun onInviteMatchSelected(): (View)->Unit{
        return {
            val layout = LayoutInflater.from(this)
                    .inflate(R.layout.layout_invite_dialog, null)

            val searchEditor = layout.findViewById<EditText>(R.id.id_search_editor)
            val inviteButton = layout.findViewById<RTextView>(R.id.id_dialog_invite_button)

            inviteButton.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    //Fetch username
                    //Fetch user id from the players db.

                    val username = searchEditor.text.toString()


                    FirebaseDatabase
                            .getInstance()
                            .reference
                            .child(FirebaseSchema.PLAYERS_NODE)
                            .child(username)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {

                                }

                                override fun onDataChange(data: DataSnapshot?) {

                                    if(data!=null)
                                    if(data.exists()){
                                        val userRoom = "MATCH_REQUESTER@" + data.value.toString()

                                        FirebaseDatabase //Set the value of the users match room requester to our UID object
                                                .getInstance()
                                                .reference
                                                .child(userRoom)
                                                .setValue(userId)

                                    }

                                }
                            })

                }
            })

            searchEditor.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                    if(s?.length == 0) {
                        inviteButton.isEnabled = false
                    }
                    else if(s?.length!! > 0) {
                        inviteButton.isEnabled = true
                    }

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })


            AlertDialog.Builder(this@GameBoardActivity)
                    .setView(layout)
                    .show()

            closeDrawer(Gravity.START)
        }
    }




    private fun onRandomMatchSelected(): (View) -> Unit{
        return {

        }
    }
    private fun deliverNewGameRequest(request: NewGameRequest){
       GameServer(this).requestNewLocalGame(request, GameInviteListener())
    }
    /**
     * CALLED WHENEVER A PIECE IS TOUCHED
     */
    private fun handleBoardClick(): (View) -> Unit = {
        val index = fetchPieceIndex(it)
        respondToTouch(index)

        if(gameHandler != null)
                gameHandler?.handleRequest(MoveRequest(fetchOurLocalPlayer(), index))
    }
    private fun handleDrawerToggle(): (View) -> Unit = { if(!id_drawer.isDrawerOpen(Gravity.START)) openDrawer(Gravity.START) }
    private fun updatePiece(player: Player, @IntRange(from = 0, to = 8) index: Int){
        val piece = id_board.getChildAt(index)
        piece.setBackgroundResource(getSelectedDrawableFromPlayer(player))
    }
    private fun respondToTouch(i: Int){
        id_board.getChildAt(i).startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink_grow))
    }
    private fun setNewCountdown(millis: Long){
        val seconds = TimeUnit.SECONDS.toSeconds(millis)
        id_match_countdown.text = (seconds/TimeUnit.SECONDS.toMillis(1)).toString()
    }
    private fun setNewMatchTimer(millis: Long){
        id_match_timer.text = TimeDisplay.format(millis)
    }
    private fun closeDrawer(grav: Int){
        if(id_drawer.isDrawerOpen(grav)) id_drawer.closeDrawer(grav)
    }
    private fun initTimers(){
        countDownDisplay = LayoutInflater.from(this).inflate(R.layout.layout_count_down, id_match_status_container, false)
        matchTimerDisplay = LayoutInflater.from(this).inflate(R.layout.layout_match_timer, id_match_status_container, false)
    }
    private fun openDrawer(grav: Int){
        if(!id_drawer.isDrawerOpen(grav)) id_drawer.openDrawer(grav)
    }
    private fun getSelectedDrawableFromPlayer(player: Player): Int = if(player.pieceType == PieceType.PLAYER_ONE) R.drawable.circle_green else R.drawable.circle_red
    fun beginCountdown(){
        unbindStatusContainer()
        bindStatusContainer(countDownDisplay)
    }
    fun beginMatchTimer(){
        unbindStatusContainer()
        bindStatusContainer(matchTimerDisplay)
    }
    fun unbindStatusContainer(){
        if(id_match_status_container.childCount > 0)
            id_match_status_container.removeViewAt(0)
    }
    fun bindStatusContainer(v: View){
        id_match_status_container.addView(v)
    }
    private fun fetchPieceIndex(v: View): Int{
        var index = -1

        when(v.id){
            R.id.zero -> index = 0
            R.id.one -> index = 1
            R.id.two -> index = 2
            R.id.three -> index = 3
            R.id.four -> index = 4
            R.id.five -> index = 5
            R.id.six -> index = 6
            R.id.seven -> index = 7
            R.id.eight -> index = 8
        }
        return index
    }
    /**
     * NOTE returns our player as player uno by default
     */
    private fun showStatusMsg(msg: Int){

    }

    override fun onConnectionEstablished(cgh: ClientGameHandler) {
        gameHandler = cgh
        cgh.bindCallbacks(MyGameInteface())
    }




    inner class MyGameInteface: GameInterface() {

        override fun onPieceFlipped(newCurrentPlayer: Player, lastPlayer: Player, index: Int) {
            updatePiece(lastPlayer, index)

            if(newCurrentPlayer.pieceType == PieceType.PLAYER_ONE) {
                id_player_two_waiting.visibility = View.GONE
                id_player_two_waiting.stop()

                id_player_one_waiting.visibility = View.VISIBLE
                id_player_one_waiting.start()
            }

            else if(newCurrentPlayer.pieceType == PieceType.PLAYER_TWO){
                id_player_two_waiting.visibility = View.VISIBLE
                id_player_two_waiting.start()

                id_player_one_waiting.visibility = View.GONE
                id_player_one_waiting.stop()
            }

        }

        override fun onGameStateChanged(newState: Int) {

            if (newState == GameRoom.GAME_STATE_COUNTDOWN)
                beginCountdown()

            else if (newState == GameRoom.GAME_STATE_STARTED) {
                beginMatchTimer()
                id_player_one_waiting.visibility = View.VISIBLE
                id_player_two_waiting.start()

            }
            else if (newState == GameRoom.GAME_STATE_END)
                Toast.makeText(this@GameBoardActivity, "ENDED", Toast.LENGTH_SHORT).show()
        }

        override fun gameWon(winResult: Referee.WinResult, loser: Player) {
            showStatusMsg( (if(winResult.playerId == userId) R.string.you_won else R.string.you_lost ) )

        }

        override fun gameTied() {
            showStatusMsg(R.string.game_tied)
        }

        override fun onBoardChanged(board: Array<GamePiece>) {

        }


        override fun newMatchTimer(timeLeft: Long) {
            setNewMatchTimer(timeLeft)
        }

        override fun newCountDown(timeLeft: Long) {
            setNewCountdown(timeLeft)
        }

        override fun onConnected() {

        }


    }
    inner class GameInviteListener: InviteResponse{
        override fun onAccepted(gameRequest: NewGameRequest) {

            val requester = gameRequest.requestingPlayer.name
            val requested = gameRequest.requestedPlayer.name

            id_player_one.text = requester
            id_player_two.text = requested

            if(gameRequest.requestingPlayer.userId == userId)
                id_player_one_you_label.visibility = View.VISIBLE
            else if(gameRequest.requestedPlayer.userId == userId)
                id_player_two_you_label.visibility = View.VISIBLE

        }

        override fun onDeclined(gameRequest: NewGameRequest) {
        }
    }

    /**DIRTY METHODS(Those that break OOP/OOD principles**/
    private fun fetchOurLocalPlayer(): Player = Player(PieceType.PLAYER_ONE, userId, userName)
    private fun fetchAIPlayer(): Player = Player(PieceType.PLAYER_TWO, AI.AI_ID, AI.NAME)
    private fun signOut(): (View)-> Unit =  {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginSignupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }
    private fun initUserStats(){
        val statsData = storage.getBytes(1024*1024) //Shouldn't ever be more than a MB
        statsData.addOnFailureListener {Log.i("test", "ERROR: " + it.localizedMessage)}
        statsData.addOnSuccessListener {bindUserStats(UserStatsJsonFactory.jsonToStats(String(it)))}
    }
    private fun bindUserStats(stats: UserStats){
        this.userStats = stats
        id_drawer_total_matches.text = stats.totalGames
        id_games_won_value.text = stats.gamesWon
        id_games_lost_value.text = stats.gamesLost
        id_games_tied_value.text = stats.gamesTied
    }
}
