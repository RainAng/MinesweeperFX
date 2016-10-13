package com.github.rainang.minesweeperfx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

public class IntField extends TextField
{
	public static final String INT_PATTERN = "([+-]?\\d+)([eE][+-]?\\d+)?";
	
	private IntegerProperty minValue;
	
	private IntegerProperty maxValue;
	
	public IntField()
	{
		this(0, Integer.MAX_VALUE, 0);
	}
	
	public IntField(int minValue, int maxValue)
	{
		this(minValue, maxValue, 0);
	}
	
	public IntField(int minValue, int maxValue, int initialValue)
	{
		super("0");
		if (minValue > maxValue)
			throw new IllegalArgumentException(String.format("minValue %s must be less than or equal to maxValue %s",
					minValue, maxValue));
		
		StringConverter<String> converter = new StringConverter<String>()
		{
			@Override
			public String toString(String string)
			{
				return clamp(string);
			}
			
			@Override
			public String fromString(String string)
			{
				return clamp(string);
			}
			
			private String clamp(String string)
			{
				int i = 0;
				try
				{
					i = Integer.parseInt(string);
				} catch (Exception ignored)
				{
					i = getText().charAt(0) == '-' ? Integer.MIN_VALUE : Integer.MAX_VALUE;
				} finally
				{
					i = Math.min(Math.max(i, getMinValue()), getMaxValue());
				}
				return Integer.toString(i);
			}
		};
		
		UnaryOperator<TextFormatter.Change> filter = change -> (change.getText().matches("[0-9]*") || change.getText()
				.equals("-")) && change.getControlNewText().matches(INT_PATTERN) ? change : null;
		
		if (minValue != 0)
			setMinValue(minValue);
		if (maxValue != Integer.MAX_VALUE)
			setMaxValue(maxValue);
		setTextFormatter(new TextFormatter<>(converter, "0", filter));
		setInt(initialValue);
		setAlignment(Pos.CENTER_RIGHT);
	}
	
	public void setMinValue(int minValue)
	{
		minValueProperty().set(minValue);
		if (getInt() < getMinValue())
			setInt(getMinValue());
	}
	
	public void setMaxValue(int maxValue)
	{
		maxValueProperty().set(maxValue);
		if (getInt() > getMaxValue())
			setInt(getMaxValue());
	}
	
	public int getInt()
	{
		return Integer.parseInt(getText());
	}
	
	public int getMinValue()
	{
		return minValue == null ? 0 : minValueProperty().get();
	}
	
	public int getMaxValue()
	{
		return maxValue == null ? Integer.MAX_VALUE : maxValueProperty().get();
	}
	
	public IntegerProperty minValueProperty()
	{
		if (minValue == null)
			minValue = new SimpleIntegerProperty();
		return minValue;
	}
	
	public IntegerProperty maxValueProperty()
	{
		if (maxValue == null)
			maxValue = new SimpleIntegerProperty();
		return maxValue;
	}
	
	public void setInt(int newValue)
	{
		setText(Integer.toString(newValue));
	}
	
	@Override
	public void replaceText(int start, int end, String text)
	{
		super.replaceText(start, end, text);
		commitValue();
	}
	
	@Override
	public void replaceSelection(String text)
	{
		super.replaceSelection(text);
		commitValue();
	}
}
