package net.sf.jdivelog.util;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.plot.DefaultDrawingSupplier;

public class ChartDrawingSupplier extends DefaultDrawingSupplier  {
	private static final long serialVersionUID = -4250803737294588965L;
	public Paint[] paintSequence;
    public int paintIndex;
    public int fillPaintIndex;

    {
        paintSequence =  new Paint[] {
        	    Color.decode("0xCB272F"),
        	    Color.decode("0x2175B6"),
        	    Color.decode("0xA4C23B"),
        	    Color.decode("0xE18918"),
        	    Color.decode("0x026264"),
        	    Color.decode("0x128E02"),
        	    Color.decode("0x000B7A"),
        	    Color.decode("0xE7D400"),
        	    Color.decode("0xA357D9"),
        	    Color.decode("0xD85ED1")
        };
    }

    @Override
    public Paint getNextPaint() {
        Paint result
        = paintSequence[paintIndex % paintSequence.length];
        paintIndex++;
        return result;
    }


    @Override
    public Paint getNextFillPaint() {
        Paint result
        = paintSequence[fillPaintIndex % paintSequence.length];
        fillPaintIndex++;
        return result;
    }   
}