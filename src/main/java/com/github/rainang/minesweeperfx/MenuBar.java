package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.Event;
import com.github.rainang.minesweeperlib.Minesweeper;
import com.github.rainang.minesweeperlib.Tile;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;

import static com.github.rainang.minesweeperlib.Minesweeper.Difficulty.*;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class MenuBar extends javafx.scene.control.MenuBar implements Event.Listener
{
	private final RadioMenuItem miBeginner = new RadioMenuItem("Beginner");
	private final RadioMenuItem miIntermediate = new RadioMenuItem("Intermediate");
	private final RadioMenuItem miExpert = new RadioMenuItem("Expert");
	
	MenuBar(MinesweeperFX mfx)
	{
		MenuItem miNew = new MenuItem("New Game");
		MenuItem miRestart = new MenuItem("Restart Game");
		MenuItem miPause = new MenuItem("Pause Game");
		MenuItem miExit = new MenuItem("Exit");
		Menu mbFile = new Menu("File");
		mbFile.getItems().addAll(miNew, miRestart, new SeparatorMenuItem(), miPause, new SeparatorMenuItem(), miExit);
		
		RadioMenuItem miCustom = new RadioMenuItem("Custom...");
		ToggleGroup tgDifficulty = new ToggleGroup();
		miBeginner.setToggleGroup(tgDifficulty);
		miIntermediate.setToggleGroup(tgDifficulty);
		miExpert.setToggleGroup(tgDifficulty);
		miCustom.setToggleGroup(tgDifficulty);
		tgDifficulty.selectToggle(miBeginner);
		CheckMenuItem miNF = new CheckMenuItem("No Flagging");
		
		Menu mbOptions = new Menu("Options");
		mbOptions.getItems().addAll(miBeginner, miIntermediate, miExpert, miCustom, new SeparatorMenuItem(), miNF);
		
		CheckMenuItem miMins = new CheckMenuItem("Show Minutes");
		CheckMenuItem miMills = new CheckMenuItem("Show Milliseconds");
		CheckMenuItem miGrid = new CheckMenuItem("Show Grid");
		CheckMenuItem miMenu = new CheckMenuItem("Show Menu");
		CheckMenuItem miData = new CheckMenuItem("Show Data");
		MenuItem miCL = new MenuItem("Change Log v" + Minesweeper.VERSION);
		
		miGrid.setSelected(true);
		miMenu.setSelected(true);
		miData.setSelected(true);
		
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
		miGrid.setAccelerator(new KeyCodeCombination(G));
		miMenu.setAccelerator(new KeyCodeCombination(F3));
		miData.setAccelerator(new KeyCodeCombination(F4));
		
		Menu mbView = new Menu("View");
		mbView.getItems().addAll(miMins, miMills, new SeparatorMenuItem(), miMenu, miData, miGrid, new
				SeparatorMenuItem(), miCL);
		
		getMenus().addAll(mbFile, mbOptions, mbView);
		
		miNew.setOnAction(e -> mfx.minesweeper.newGame());
		miRestart.setOnAction(e -> mfx.minesweeper.restartGame());
		miPause.setOnAction(e -> mfx.minesweeper.pauseGame());
		miExit.setOnAction(e -> System.exit(0));
		
		miBeginner.setOnAction(e -> mfx.minesweeper.setDifficulty(BEGINNER));
		miIntermediate.setOnAction(e -> mfx.minesweeper.setDifficulty(INTERMEDIATE));
		miExpert.setOnAction(e -> mfx.minesweeper.setDifficulty(EXPERT));
		miCustom.setOnAction(e -> new CustomDialog(mfx));
		miNF.setOnAction(e -> mfx.minesweeper.setNoFlagging(miNF.isSelected()));
		
		miMins.setOnAction(e -> mfx.data.setFormat(miMins.isSelected(), miMills.isSelected()));
		miMills.setOnAction(e -> mfx.data.setFormat(miMins.isSelected(), miMills.isSelected()));
		miGrid.setOnAction(e -> mfx.board.setShowGrid(miGrid.isSelected()));
		miMenu.setOnAction(e -> mfx.showData(miMenu.isSelected(), miData.isSelected()));
		miData.setOnAction(e -> mfx.showData(miMenu.isSelected(), miData.isSelected()));
	}
	
	@Override
	public void onGameEvent(Event event, Minesweeper ms, Tile tile)
	{
		switch (event)
		{
		case DIFFICULTY_CHANGED:
			if (!miBeginner.isSelected() && ms.getWidth() == 9 && ms.getHeight() == 9 && ms.getMines() == 10)
				miBeginner.setSelected(true);
			else if (!miIntermediate.isSelected() && ms.getWidth() == 16 && ms.getHeight() == 16 && ms.getMines() ==
					40)
				miIntermediate.setSelected(true);
			else if (!miExpert.isSelected() && ms.getWidth() == 30 && ms.getHeight() == 16 && ms.getMines() == 99)
				miExpert.setSelected(true);
			break;
		}
	}
	
	private class CustomDialog extends Dialog<ButtonType>
	{
		private CustomDialog(MinesweeperFX mfx)
		{
			int w = mfx.minesweeper.getWidth();
			int h = mfx.minesweeper.getHeight();
			int m = mfx.minesweeper.getMines();
			
			Label lbl1 = new Label("Width (5-32):");
			Label lbl2 = new Label("Height (5-32):");
			Label lbl3 = new Label("Mines (5-" + (w * h - 10) + "):");
			
			Field fld1 = new Field(32, w);
			Field fld2 = new Field(32, h);
			Field fld3 = new Field(fld1.getInt() * fld2.getInt() - 10, m);
			
			fld1.textProperty().addListener(a -> updateMineLimit(fld1.getInt() * fld2.getInt() - 10, lbl3, fld3));
			fld2.textProperty().addListener(a -> updateMineLimit(fld1.getInt() * fld2.getInt() - 10, lbl3, fld3));
			
			GridPane grid = new GridPane();
			grid.setHgap(32);
			grid.add(lbl1, 0, 0);
			grid.add(lbl2, 0, 1);
			grid.add(lbl3, 0, 2);
			grid.add(fld1, 1, 0);
			grid.add(fld2, 1, 1);
			grid.add(fld3, 1, 2);
			
			ButtonType b1 = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
			ButtonType b2 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			getDialogPane().getButtonTypes().addAll(b1, b2);
			setTitle("Custom Board");
			getDialogPane().setContent(grid);
			showAndWait().filter(bt -> bt == b1).ifPresent(bt -> mfx.minesweeper.setDifficulty(fld1.getInt(),
					fld2.getInt(), fld3.getInt()));
		}
		
		private void updateMineLimit(int limit, Label label, IntField field)
		{
			label.setText("Mines (5-" + limit + "):");
			field.setMaxValue(limit);
		}
	}
	
	private class Label extends javafx.scene.control.Label
	{
		private Label(String text)
		{
			super(text);
			setMinWidth(150);
			setMaxWidth(100);
		}
	}
	
	private class Field extends IntField
	{
		private Field(int maxValue, int initalValue)
		{
			super(5, maxValue, initalValue);
			setMaxWidth(50);
		}
	}
}
