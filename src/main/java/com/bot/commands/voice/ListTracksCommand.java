package com.bot.commands.voice;

import com.bot.voice.QueuedAudioTrack;
import com.bot.voice.VoiceSendHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ListTracksCommand extends Command {
	private static final Logger LOGGER = Logger.getLogger(ListTracksCommand.class.getName());

	public ListTracksCommand() {
		this.name = "list";
		this.arguments = "";
		this.help = "Lists the tracks currently in the queue";
	}

	@Override
	protected void execute(CommandEvent commandEvent) {
		VoiceSendHandler handler = (VoiceSendHandler) commandEvent.getGuild().getAudioManager().getSendingHandler();

		if (handler == null) {
			commandEvent.reply(commandEvent.getClient().getWarning() + " I am not currently playing audio.");
			return;
		}

		if (!handler.isPlaying()) {
			commandEvent.reply(commandEvent.getClient().getWarning() + " I am not currently playing audio.");
			return;
		}

		QueuedAudioTrack nowPlaying = handler.getNowPlaying();
		List<QueuedAudioTrack> tracks = new ArrayList<>(handler.getTracks());
		StringBuilder sb = new StringBuilder();
		sb.append("```\nNow Playing: " + nowPlaying.getTrack().getInfo().title + "\n");

		for (int i = 0; i < tracks.size(); i++) {
			if (i == 0)
				sb.append("Next: " + tracks.get(i).getTrack().getInfo().title + "\n");
			else
				sb.append((i + 1) + ": " + tracks.get(i).getTrack().getInfo().title + "\n");
		}
		sb.append("```");
		commandEvent.reply(sb.toString());
	}
}
