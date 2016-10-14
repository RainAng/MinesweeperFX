package com.github.rainang.minesweeperfx;

import com.github.rainang.minesweeperlib.GameEvent;
import com.github.rainang.minesweeperlib.GameState;
import com.github.rainang.minesweeperlib.Minesweeper;
import com.github.rainang.minesweeperlib.Tile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import static javafx.scene.paint.Color.*;

class Board extends StackPane implements GameEvent.Listener
{
	private static final String FLAG = "\u2690";
	private static final String MINE = "\u2738";
	
	private final Minesweeper ms;
	private final double resolution = 24;
	
	private final Canvas canvas;
	
	private boolean showGrid = true;
	
	Board(Minesweeper ms, Border border)
	{
		this.ms = ms;
		canvas = new Canvas();
		
		setBorder(border);
		getChildren().add(canvas);
	}
	
	void setShowGrid(boolean showGrid)
	{
		this.showGrid = showGrid;
		canvas.paintBoard(canvas.getGraphicsContext2D());
	}
	
	@Override
	public boolean isResizable()
	{
		return false;
	}
	
	@Override
	public void onGameEvent(GameEvent event, Minesweeper minesweeper, Tile tile)
	{
		switch (event)
		{
		case DIFFICULTY_CHANGE_EVENT:
			Insets i = getBorder().getInsets();
			double w = ms.getWidth() * resolution;
			double h = ms.getHeight() * resolution;
			if (w != getWidth())
			{
				canvas.setWidth(w);
				setWidth(w + i.getLeft() + i.getRight());
			}
			if (h != getHeight())
			{
				canvas.setHeight(h);
				setHeight(h + i.getTop() + i.getBottom());
			}
		case NEW_GAME_EVENT:
		case RESTART_GAME_EVENT:
		case PAUSE_EVENT:
			canvas.paintBoard(canvas.getGraphicsContext2D());
			break;
		case WIN_EVENT:
		case LOSE_EVENT:
			GraphicsContext gc = canvas.getGraphicsContext2D();
			canvas.paintBoard(gc);
			gc.setFill(Color.color(0, 0, 0, 0.25));
			gc.fillRect(0, 0, getWidth(), getHeight());
			break;
		case OPEN_EVENT:
		case CHORD_EVENT:
			canvas.paintBoard(canvas.getGraphicsContext2D());
			break;
		case FLAG_EVENT:
			canvas.paintTile(canvas.getGraphicsContext2D(), tile);
			break;
		}
	}
	
	private class Canvas extends javafx.scene.canvas.Canvas
	{
		private final ObjectProperty<Tile> mouseover = new SimpleObjectProperty<>();
		
		Canvas()
		{
			GraphicsContext gc = getGraphicsContext2D();
			gc.setFill(LIGHTGRAY);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			
			setOnMouseMoved(e -> mouseover.setValue(ms.getTile(x(e), y(e))));
			setOnMouseDragged(e -> mouseover.setValue(ms.getTile(x(e), y(e))));
			setOnMouseExited(e -> mouseover.setValue(null));
			setOnMouseClicked(e ->
			{
				if (e.getButton() == MouseButton.PRIMARY)
					if (ms.getGameState() == GameState.END)
					{
						paintBoard(gc);
						paintBoardOverlay(gc);
					} else if (e.isSecondaryButtonDown())
						ms.chord(x(e), y(e));
					else
						ms.open(x(e), y(e));
			});
			setOnMousePressed(e ->
			{
				if (e.getButton() == MouseButton.SECONDARY)
					ms.flag(x(e), y(e));
			});
			
			mouseover.addListener((a, b, c) ->
			{
				paintTile(gc, b);
				paintTile(gc, c);
			});
		}
		
		private int x(MouseEvent e)
		{
			return (int) (e.getX() / resolution);
		}
		
		private int y(MouseEvent e)
		{
			return (int) (e.getY() / resolution);
		}
		
		private void paintBoard(GraphicsContext gc)
		{
			for (int y = 0; y < ms.getHeight(); y++)
				for (int x = 0; x < ms.getWidth(); x++)
					paintTile(gc, ms.getTile(x, y));
		}
		
		private void paintTile(GraphicsContext gc, Tile t)
		{
			if (t == null)
				return;
			
			double x = t.getX() * resolution;
			double y = t.getY() * resolution;
			
			String text = "";
			
			clearTile(gc, x, y);
			
			if (ms.getGameState() == GameState.PAUSE)
			{
				paintFloor(gc, x, y);
				return;
			} else if (t.isOpen())
			{
				paintFloor(gc, x, y);
				if (t.isMine())
				{
					gc.setFill(t == ms.getLosingTile() ? RED : BLACK);
					text = MINE;
				} else
				{
					int i = t.getMineCount();
					if (i > 0)
					{
						switch (i)
						{
						case 1:
							gc.setFill(BLUE);
							break;
						case 2:
							gc.setFill(GREEN);
							break;
						case 3:
							gc.setFill(RED);
							break;
						case 4:
							gc.setFill(DARKBLUE);
							break;
						case 5:
							gc.setFill(DARKRED);
							break;
						case 6:
							gc.setFill(TEAL);
							break;
						case 7:
							gc.setFill(GRAY);
							break;
						case 8:
							gc.setFill(BLACK);
							break;
							
						}
						text = "" + i;
					}
				}
			} else
			{
				paintTile(gc, x, y);
				if (t.hasFlag())
				{
					gc.setFill(ms.getGameState() == GameState.END && !t.isMine() ? RED : BLACK);
					text = FLAG;
				}
			}
			
			gc.fillText(text, x + resolution / 2, y + resolution / 2);
			
			if (mouseover.get() == t)
				paintTileHighlight(gc, x, y);
		}
		
		private void paintTile(GraphicsContext gc, double x, double y)
		{
			double s = resolution - 1;
			x += 0.5;
			y += 0.5;
			
			for (int i = 0; i < 2; i++)
			{
				gc.setStroke(WHITE);
				gc.strokeLine(x, y, x + s - 1, y);
				gc.strokeLine(x, y, x, y + s - 1);
				
				gc.setStroke(GRAY);
				gc.strokeLine(x + s, y + s, x + 1, y + s);
				gc.strokeLine(x + s, y + s, x + s, y + 1);
				x += 1;
				y += 1;
				s -= 2;
			}
		}
		
		private void paintFloor(GraphicsContext gc, double x, double y)
		{
			if (!showGrid)
				return;
			x += 0.5;
			y += 0.5;
			double s = resolution - 1;
			gc.setStroke(GRAY);
			gc.strokeLine(x, y, x + s, y);
			gc.strokeLine(x, y, x, y + s);
			gc.setStroke(WHITE);
			gc.strokeLine(x + s, y + s, x + s, y);
			gc.strokeLine(x + s, y + s, x + 1, y + s);
		}
		
		private void paintTileHighlight(GraphicsContext gc, double x, double y)
		{
			gc.setFill(Color.color(0, 0, 0, 0.25));
			gc.fillRect(x, y, resolution, resolution);
		}
		
		private void paintBoardOverlay(GraphicsContext gc)
		{
			gc.setFill(Color.color(0, 0, 0, 0.25));
			gc.fillRect(0, 0, getWidth(), getHeight());
		}
		
		private void clearTile(GraphicsContext gc, double x, double y)
		{
			gc.setFill(LIGHTGRAY);
			gc.fillRect(x, y, resolution, resolution);
		}
	}
}
