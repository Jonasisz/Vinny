package com.bot.utils

import club.minnced.discord.webhook.send.WebhookMessage
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.bot.models.RssUpdate
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import org.json.JSONObject

class RssUtils {
    companion object {
        val logger = Logger(this::class.java.simpleName)

        fun sendRssUpdate(rssUpdate: RssUpdate, jda : JDA) {
            val channel = jda.getTextChannelById(rssUpdate.channel)
            if (channel == null) {
                logger.warning("Failed to find text channel for RSS update $channel")
                return
            }
            if (!channel.guild.selfMember.hasPermission(Permission.MANAGE_WEBHOOKS)) {
                channel.sendMessage("WARNING: I don't have the `MANAGE_WEBHOOKS` permission. Please give me this permission " +
                        "to allow Scheduled commands and Subscriptions to work correctly")
            } else {
                val webhook = ScheduledCommandUtils.getWebhookForChannel(channel)
                when (rssUpdate.provider) {
                    1 -> { // Reddit
                        webhook.send(buildMessage("New post in ***${rssUpdate.subject}***" +
                                "\nhttps://reddit.com${rssUpdate.url}", jda))
                    }
                    2 -> { // Twitter
                        val msg = if (rssUpdate.subject.startsWith("RT")) {
                            "New retweet from ***${rssUpdate.subject.replace("RT", "")}"
                        } else {
                            "New tweet from ***${rssUpdate.subject}"
                        }
                        webhook.send(buildMessage("$msg***\n" + rssUpdate.url, jda))
                    }
                    3 -> { // 4Chan
                        webhook.send(buildMessage("New thread in ***${rssUpdate.subject}***\n" +
                                rssUpdate.url, jda))
                    }
                    else -> { // other

                    }
                }
            }
        }

        fun mapJsonToUpdate(json: JSONObject) : RssUpdate {
            return RssUpdate(
                    json.getString("channel"),
                    json.getString("url"),
                    json.getInt("provider"),
                    json.getString("subject")
            )
        }

        fun buildMessage(msg: String, jda: JDA) :WebhookMessage {
            return WebhookMessageBuilder()
                    .setUsername("Vinny")
                    .setAvatarUrl(jda.selfUser.avatarUrl)
                    .setContent(msg)
                    .build()
        }
    }
}