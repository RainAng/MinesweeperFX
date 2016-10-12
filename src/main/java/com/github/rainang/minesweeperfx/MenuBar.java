package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.Minesweeper;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;

import static com.github.rainang.minesweeperlib.Minesweeper.Difficulty.*;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class MenuBar extends javafx.scene.control.MenuBar
{
	MenuBar(MinesweeperFX mfx)
	{
		MenuItem miNew = new MenuItem("New Game");
		MenuItem miRestart = new MenuItem("Restart Game");
		MenuItem miPause = new MenuItem("Pause Game");
		MenuItem miExit = new MenuItem("Exit");
		Menu mbFile = new Menu("File");
		mbFile.getItems().addAll(miNew, miRestart, new SeparatorMenuItem(), miPause, new SeparatorMenuItem(), miExit);
		
		RadioMenuItem miBeginner = new RadioMenuItem("Beginner");
		RadioMenuItem miIntermediate = new RadioMenuItem("Intermediate");
		RadioMenuItem miExpert = new RadioMenuItem("Expert");
		ToggleGroup tgDifficulty = new ToggleGroup();
		miBeginner.setToggleGroup(tgDifficulty);
		miIntermediate.setToggleGroup(tgDifficulty);
		miExpert.setToggleGroup(tgDifficulty);
		tgDifficulty.selectToggle(miBeginner);
		CheckMenuItem miNF = new CheckMenuItem("No Flagging");
		
		Menu mbOptions = new Menu("Options");
		mbOptions.getItems().addAll(miBeginner, miIntermediate, miExpert, new SeparatorMenuItem(), miNF);
		
		CheckMenuItem miMins = new CheckMenuItem("Show Minutes");
		CheckMenuItem miMills = new CheckMenuItem("Show Milliseconds");
		MenuItem miCL = new CheckMenuItem("Change Log v" + Minesweeper.VERSION);
		miMins.selectedProperty().addListener((a, b, c) -> mfx.data.setFormat(c, miMills.isSelected()));
		miMills.selectedProperty().addListener((a, b, c) -> mfx.data.setFormat(miMins.isSelected(), c));
		
		miNew.setAccelerator(new KeyCodeCombination(F2));
		miRestart.setAccelerator(new KeyCodeCombination(F2, CONTROL_DOWN));
		miPause.setAccelerator(new KeyCodeCombination(SPACE));
		miExit.setAccelerator(new KeyCodeCombination(Q, CONTROL_DOWN));
		
		miBeginner.setAccelerator(new KeyCodeCombination(B));
		miIntermediate.setAccelerator(new KeyCodeCombination(I));
		miExpert.setAccelerator(new KeyCodeCombination(E));
		miNF.setAccelerator(new KeyCodeCombination(F, CONTROL_DOWN));
		
		miMins.setAccelerator(new KeyCodeCombination(M));
		miMills.setAccelerator(new KeyCodeCombination(M, CONTROL_DOWN));
		
		Menu mbView = new Menu("View");
		mbView.getItems().addAll(miMins, miMills, miCL);
		
		getMenus().addAll(mbFile, mbOptions, mbView);
		
		miNew.setOnAction(e -> mfx.minesweeper.newGame());
		miRestart.setOnAction(e -> mfx.minesweeper.restartGame());
		miPause.setOnAction(e -> mfx.minesweeper.pauseGame());
		miExit.setOnAction(e -> System.exit(0));
		
		miBeginner.setOnAction(e -> mfx.minesweeper.setDifficulty(BEGINNER));
		miIntermediate.setOnAction(e -> mfx.minesweeper.setDifficulty(INTERMEDIATE));
		miExpert.setOnAction(e -> mfx.minesweeper.setDifficulty(EXPERT));
		miNF.setOnAction(e -> mfx.minesweeper.setNoFlagging(miNF.isSelected()));
	}
}
