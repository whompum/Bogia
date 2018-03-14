package com.younivibes.bogia.Players

/**
 * Class responsible
 * for creating / parsing
 * UserStat files
 *
 *
 *
 */
data class UserStats(var totalGames: String = "0", var gamesWon: String = "0", var gamesLost: String = "0", var gamesTied: String ="0"){
    constructor(): this("0", "0", "0", "0")
}