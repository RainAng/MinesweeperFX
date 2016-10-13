package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.Event;
import com.github.rainang.minesweeperlib.Minesweeper;
import com.github.rainang.minesweeperlib.Tile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.paint.Color.*;

public class MinesweeperFX extends Application implements Event.Listener
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	private final Image ICON = new Image("icon.png");
	
	private Stage stage;
	
	final Minesweeper minesweeper = new Minesweeper(new Random(0));
	
	final Board board = new Board(minesweeper, createBevelBorderLowered());
	final Data data = new Data(minesweeper);
	final MenuBar menu = new MenuBar(this);
	
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		data.setBorder(createBevelBorderLowered());
		minesweeper.addEventListener(board);
		minesweeper.addEventListener(data);
		minesweeper.addEventListener(menu);
		minesweeper.addEventListener(this);
		minesweeper.setDifficulty(Minesweeper.Difficulty.BEGINNER);
		
		VBox root = new VBox(menu, data, board);
		root.setFillWidth(true);
		root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("MinesweeperFX");
		stage.getIcons().add(ICON);
		stage.show();
		stage.setResizable(false);
	}
	
	private Border createBevelBorderLowered()
	{
		Stream<BorderStroke> stream = IntStream.range(6, 9).mapToObj(i -> new BorderStroke(GRAY, WHITE, WHITE, GRAY,
				SOLID, SOLID, SOLID, SOLID, null, BorderWidths.DEFAULT, new Insets(i)));
		Stream<BorderStroke> stream2 = IntStream.range(0, 6).mapToObj(i -> new BorderStroke(LIGHTGRAY,
				BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT, new Insets(i)));
		return new Border(Stream.concat(stream, stream2).toArray(BorderStroke[]::new));
	}
	
	@Override
	public void onGameEvent(Event event, Minesweeper minesweeper, Tile tile)
	{
		switch (event)
		{
		case DIFFICULTY_CHANGED:
			stage.sizeToScene();
			stage.centerOnScreen();
			break;
		}
	}
}
