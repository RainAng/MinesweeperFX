package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.Event;
import com.github.rainang.minesweeperlib.Minesweeper;
import com.github.rainang.minesweeperlib.Tile;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;

public class Data extends BorderPane implements Event.Listener
{
	private static final String TIME = "\u231A";
	private static final String MINE = "\u2738";
	private static final String FLAG = "\u2690";
	
	private final Label mine;
	
	private String pattern = "%1$ts";
	
	Data(Minesweeper ms)
	{
		mine = new Label(ms.getMines() + "");
		Label time = new Label();
		
		VBox left = new VBox(new HBox(8, new Label(MINE), mine));
		VBox right = new VBox(new HBox(8, time, new Label(TIME)));
		left.setPadding(new Insets(2, 0, 2, 8));
		right.setPadding(new Insets(2, 8, 2, 0));
		
		setLeft(left);
		setRight(right);
		
		new Timer("timer", true).scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				Platform.runLater(() -> time.setText(String.format(pattern, ms.getTime())));
			}
		}, 0, 16);
	}
	
	void setFormat(boolean min, boolean mil)
	{
		if (min)
			pattern = "%1$tM:%1$tS";
		else
			pattern = "%1$ts";
		if (mil)
			pattern += ".%1$tL";
	}
	
	@Override
	public void onGameEvent(Event event, Minesweeper ms, Tile tile)
	{
		switch (event)
		{
		case NEW_GAME:
		case RESTART_GAME:
		case TILE_FLAGGED:
			mine.setText("" + (ms.isNoFlagging() ? ms.getMines() : ms.getMines() - ms.getFlagsUsed()));
			break;
		}
	}
}
