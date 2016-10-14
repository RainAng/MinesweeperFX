package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.Difficulty;
import com.github.rainang.minesweeperlib.GameEvent;
import com.github.rainang.minesweeperlib.Minesweeper;
import com.github.rainang.minesweeperlib.Tile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.paint.Color.*;

public class MinesweeperFX extends Application implements GameEvent.Listener
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	private final Image ICON = new Image("icon.png");
	
	private Stage stage;
	
	final Minesweeper minesweeper = new Minesweeper();
	
	final Board board = new Board(minesweeper, createBevelBorderLowered());
	final Data data = new Data(minesweeper);
	final MenuBar menu = new MenuBar(this);
	
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		data.setBorder(createBevelBorderLowered());
		minesweeper.addGameEventListener(board);
		minesweeper.addGameEventListener(data);
		minesweeper.addGameEventListener(menu);
		minesweeper.addGameEventListener(this);
		minesweeper.setDifficulty(Difficulty.BEGINNER);
		
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
	public void onGameEvent(GameEvent event, Minesweeper minesweeper, Tile tile)
	{
		switch (event)
		{
		case DIFFICULTY_CHANGE_EVENT:
			stage.sizeToScene();
			stage.centerOnScreen();
			break;
		}
	}
	
	void showData(boolean m, boolean d)
	{
		VBox box = (VBox) stage.getScene().getRoot();
		box.getChildren().clear();
		if (m)
			box.getChildren().add(menu);
		if (d)
			box.getChildren().add(this.data);
		box.getChildren().add(board);
		stage.sizeToScene();
	}
}
