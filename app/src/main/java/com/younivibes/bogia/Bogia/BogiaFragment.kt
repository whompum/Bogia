package com.younivibes.bogia.Bogia

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorRes
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.younivibes.bogia.Game.PieceType
import com.younivibes.bogia.GameUtils.SimpleAnimListener
import com.younivibes.bogia.GameUtils.TimeDisplay
import com.younivibes.bogia.Players.Player
import com.younivibes.bogia.Players.UserStats
import com.younivibes.bogia.Players.UserStatsJsonFactory
import com.younivibes.bogia.R
import com.younivibes.bogia.Widgets.CellView
import com.younivibes.bogia.Widgets.TimelessWave

import kotlinx.android.synthetic.main.layout_board.*
import kotlinx.android.synthetic.main.layout_count_down.*
import kotlinx.android.synthetic.main.layout_game.*
import kotlinx.android.synthetic.main.layout_match_players.*
import kotlinx.android.synthetic.main.layout_match_timer.*
import kotlinx.android.synthetic.main.layout_user_actions_main.*

/**
 * Created by bryan on 3/17/2018.
 *
 * The handler of the UI
 * This object isn't responsible for sending data over a network
 * fetching data etc etc. His job is solely to update the UI;
 */
class BogiaFragment: Fragment() {

    companion object {
        const val LAYOUT_ID = R.layout.layout_game

        const val PLAYER_NAME_KEY = "playerName.ky"
        const val PLAYER_ID_KEY = "playerId.ky"
        const val PLAYER_STATS_KEY = "playerStats.ky"

        fun newInstance(@NonNull args: Bundle, UIListener: UIListener): Fragment{
            val fragment = BogiaFragment()
            fragment.arguments = args
            fragment.UIL = UIListener
            return fragment
        }

    }

    private lateinit var UIL: UIListener

    private lateinit var countDownDisplay: View
    private lateinit var matchTimerDisplay: View


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(LAYOUT_ID, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id_sign_out.setOnClickListener{
            UIL.onExitSelected()
        }

        //Set a listener on the Drawer Toggle
        id_drawer_toggle.setOnClickListener {
            id_drawer.openDrawer(Gravity.START)
        }

        //Sets the text of the DrawerLayout
        id_drawer_you.text = arguments.getString(PLAYER_NAME_KEY)

        id_ai.setOnClickListener{onAiOption()}
        id_search_match.setOnClickListener{onInviteOption()} //Random matchmaking
        id_random_match.setOnClickListener{onMatchmakingSelected()}

        //Set click listeners on all of the board pieces
        for(childIndex in 0 until id_board.childCount)
            id_board.getChildAt(childIndex).setOnClickListener {
                if(id_board.getChildAt(childIndex) is CellView)
                UIL.onCellTapped(it as CellView) //Send the tap information to the listener
            }

        val rawStats = arguments.getString(PLAYER_STATS_KEY)
        //Binding Player game statistics in the Drawer
        if(rawStats.isNotEmpty()){
            val stats = UserStatsJsonFactory.jsonToStats(rawStats)
            id_drawer_total_matches.text = stats.totalGames
            id_games_won_value.text = stats.gamesWon
            id_games_lost_value.text = stats.gamesLost
            id_games_tied_value.text = stats.gamesTied
        }
        initTimers()
    }


    fun updateCell(cellIndex: Int, pieceType: PieceType){
        for(childIndex in 0 until id_board.childCount) {
            val child = id_board.getChildAt(childIndex)
            if (child is CellView)
                if (cellIndex == child.index.index)
                    child.setPieceType(pieceType)
        }
    }

    fun updateCountDown(millis: Long){
       if(doesStatusContain(countDownDisplay.id))
           id_match_countdown.text = (millis / 1000).toString() //Sets the count down seconds value
       else{
           unBindStatusContainer()
           bindStatusContainer(countDownDisplay)
           updateCountDown(millis)
       }
    }

    fun updateMatchTimer(millis: Long){
        if(doesStatusContain(matchTimerDisplay.id))
            id_match_timer.text = TimeDisplay.formatMMSS(millis) //Sets the match timer value
        else{
            unBindStatusContainer()
            bindStatusContainer(matchTimerDisplay)
            updateMatchTimer(millis)
        }
    }

    fun toggleTimelessDisplay(newCurrentPlayer: Player){
        toggleTimelessDisplay(newCurrentPlayer.pieceType)
    }

    fun toggleTimelessDisplay(pieceType: PieceType){

        val showPlayerOne = pieceType == PieceType.PLAYER_ONE

        changeTimelessDisplay(id_player_one_waiting,  showPlayerOne)
        changeTimelessDisplay(id_player_two_waiting, !showPlayerOne)
    }


    fun animateOnWin(winningPieces: Array<Int>, index: Int){
        if(index == winningPieces.size){
            Handler().postDelayed({
                resetBoard()
            }, 1500L)
            reset()
            return
        }

        val animation = AnimationUtils.loadAnimation(context, R.anim.shrink_grow)

        animation.setAnimationListener(object: SimpleAnimListener(){
            override fun onAnimationEnd(animation: Animation?) {
                animateOnWin(winningPieces, index + 1)
            }
        })
        id_board.getChildAt(winningPieces[index]).startAnimation(animation)
    }



    /**
     * Sets the player board on the bottom of the screen
     */
    fun bindPlayers(playerOne: Player, playerTwo: Player, weArePlayerOne: Boolean){

        id_player_one.text = playerOne.name
        id_player_two.text = playerTwo.name

        var youLabelToDisplay: View
        //We always want our players name to be green.
        var playerOneTextColor: Int = if(weArePlayerOne) R.color.bogia_green else R.color.bogia_red
        var playerTwoTextColor: Int = if(!weArePlayerOne) R.color.bogia_green else R.color.bogia_red

        if(weArePlayerOne)
            youLabelToDisplay = id_player_one_you_label

         else
            youLabelToDisplay = id_player_two_you_label

        youLabelToDisplay.visibility = View.VISIBLE
        id_player_one.setTextColor(resolveColor(playerOneTextColor))
        id_player_two.setTextColor(resolveColor(playerTwoTextColor))
    }


    fun resetBoard(){
        for(childIndex in 0 until id_board.childCount)
            id_board.post {
                val child = id_board.getChildAt(childIndex)
                if(child is CellView)
                    child.setPieceType(PieceType.NO_PLAYER_STATUS)
            }
    }

    fun reset(){
        unBindStatusContainer()

        id_player_one.text = ""
        id_player_two.text = ""

        for(childIndex in 0 until id_players_container.childCount){
            val child = id_players_container.getChildAt(childIndex)
            if(child.id == R.id.id_player_one || child.id == R.id.id_player_two)
                continue

            child.visibility = View.GONE
        }
    }



    private fun resolveColor(@ColorRes clr: Int): Int{

        if(Build.VERSION.SDK_INT >= 23)
            return resources.getColor(clr, null)
        else
            return resources.getColor(clr)
    }

    private fun onAiOption(){
        id_drawer.closeDrawer(Gravity.START)
        UIL.onAIOption()
    }

    private fun onInviteOption(){
        id_drawer.closeDrawer(Gravity.START)
        UIL.onInviteOption("" /**Fetch player name*/)
    }

    private fun onMatchmakingSelected(){
        id_drawer.closeDrawer(Gravity.START)
        UIL.onMatchMakingOption()
    }

    //Pre initialize the game timers
    private fun initTimers(){
        countDownDisplay = LayoutInflater.from(context).inflate(R.layout.layout_count_down, id_match_status_container, false)
        matchTimerDisplay = LayoutInflater.from(context).inflate(R.layout.layout_match_timer, id_match_status_container, false)
    }

    private fun changeTimelessDisplay(wave: TimelessWave, show: Boolean){
        if(show){
            wave.visibility = View.VISIBLE
            wave.start()
        }else{
            wave.visibility = View.GONE
            wave.stop()
        }
    }

    private fun doesStatusContain(viewId: Int): Boolean{
        for(index in 0 until id_match_status_container.childCount)
            return id_match_status_container.getChildAt(index).id == viewId

        return false
    }

    private fun unBindStatusContainer(){

        for(childIndex in 0 until id_match_status_container.childCount)
            id_match_status_container.removeViewAt(childIndex) //May throw error
    }

    private fun bindStatusContainer(child: View){
        id_match_status_container.addView(child)
    }


}









