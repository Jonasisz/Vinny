package com.bot.commands.reddit;

import com.bot.RedditConnection;
import com.bot.commands.RedditCommand;
import com.bot.exceptions.RedditRateLimitException;
import com.bot.exceptions.ScheduledCommandFailedException;
import com.bot.utils.CommandCategories;
import com.bot.utils.ConstantStrings;
import com.bot.utils.RedditHelper;
import com.jagrosh.jdautilities.command.CommandEvent;
import datadog.trace.api.Trace;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dv8tion.jda.api.entities.ChannelType;

import java.util.logging.Level;

public class RandomPostCommand extends RedditCommand{
    private RedditConnection redditConnection;

    public RandomPostCommand() {
        this.name = "rr";
        this.help = "Grabs a random hot post from a given subreddit";
        this.arguments = "<subreddit name>";
        this.category = CommandCategories.REDDIT;

        redditConnection = RedditConnection.getInstance();
    }

    @Override
    @Trace(operationName = "executeCommand", resourceName = "RandomReddit")
    protected void executeCommand(CommandEvent commandEvent) {
        boolean isNSFWAllowed = true;

        // TODO: Move to static helper
        if (!commandEvent.isFromType(ChannelType.PRIVATE)) {
            isNSFWAllowed = commandEvent.getTextChannel().isNSFW();
        }

        try{
            RedditHelper.getRandomSubmissionAndSend(redditConnection,
                    commandEvent,
                    SubredditSort.HOT,
                    TimePeriod.WEEK,
                    150,
                    isNSFWAllowed);
        } catch (NullPointerException e) {
            commandEvent.replyWarning("I could not find that subreddit, please make sure it is public, and spelled correctly.");
        } catch (ApiException e) {
            if (e.getCode().equals("403")) {
                commandEvent.replyWarning("Subreddit is private, please stick to public subreddits.");
            } else {
                commandEvent.replyError("Recieved error: " + e.getCode() + " from reddit.");
            }
        } catch (NetworkException e) {
            commandEvent.replyWarning("I was unable to get info the subreddit, please make sure it is correctly spelled.");
        } catch (ScheduledCommandFailedException e) {
            logger.warning("Failed to get webhook for scheduled command " + commandEvent.getTextChannel().getId(), e);
            commandEvent.replyWarning(ConstantStrings.SCHEDULED_WEBHOOK_FAIL);
        } catch (RedditRateLimitException e) {
            commandEvent.reply(commandEvent.getClient().getError() + "Reddit is rate limiting Vinny, please try again later.");
            metricsManager.markCommandFailed(this, commandEvent.getAuthor(), commandEvent.getGuild());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error thrown when getting reddit post", e);
            commandEvent.replyError("Sorry, something went wrong getting a reddit post.");
            metricsManager.markCommandFailed(this, commandEvent.getAuthor(), commandEvent.getGuild());
        }
    }
}
