package com.bot.commands.general

import com.bot.commands.GeneralCommand
import com.bot.db.ChannelDAO
import com.bot.exceptions.PixivException
import com.bot.utils.PixivClient
import com.jagrosh.jdautilities.command.CommandEvent
import datadog.trace.api.Trace

class PixivCommand : GeneralCommand() {

    private val channelDAO: ChannelDAO

    init {
        this.name = "pixiv"
        this.help = "Gets a post from pixiv"
        this.arguments = "<search terms>"

        this.channelDAO = ChannelDAO.getInstance()
    }

    @Trace(operationName = "executeCommand", resourceName = "Pixiv")
    override fun executeCommand(commandEvent: CommandEvent) {
        commandEvent.channel.sendTyping().queue()
        try {
            commandEvent.reply(PixivClient.getRandomPixivPostFromSearch(commandEvent.args, false))
        } catch (e: PixivException ) {
            commandEvent.replyWarning("Something went wrong getting the pixiv post: " + e.message)
        }
    }
}
