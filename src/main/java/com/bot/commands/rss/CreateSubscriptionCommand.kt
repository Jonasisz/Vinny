package com.bot.commands.rss

import com.bot.commands.ModerationCommand
import com.bot.db.RssDAO
import com.bot.db.UserDAO
import com.bot.models.UsageLevel
import com.bot.utils.ConstantStrings
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter

abstract class CreateSubscriptionCommand : ModerationCommand() {
    protected val rssDAO : RssDAO = RssDAO.getInstance()
    private val userDAO : UserDAO = UserDAO.getInstance()
    protected lateinit var waiter : EventWaiter

    fun canMakeNewSubscription(commandEvent: CommandEvent) : Boolean {
        val user = userDAO.getById(commandEvent.author.id)
        if (user == null || user.usageLevel() == UsageLevel.BASIC) {
            if (rssDAO.getCountForAuthor(commandEvent.author.id) >= 10) {
                commandEvent.replyWarning("You can only make 10 subscriptions. To be able to make unlimited you can donate" +
                        " at " + ConstantStrings.DONATION_URL + ". This is to help prevent abuse. If you have already donated, make sure you are in the Vinny support server." +
                        " To get a support server invite use `~support`.\nYou can also remove your current scheduled commands with the " +
                        "`~unschedule` command")
                return false
            }
        }
        return true
    }
}