package net.sf.jdivelog.gui.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class MyTable extends JTable {
	private static final long serialVersionUID = 6559302251954139125L;
	private Border paddingBorder = BorderFactory.createEmptyBorder(2, 10, 2, 10);
  
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);

        if (JComponent.class.isInstance(comp)){
            ((JComponent)comp).setBorder(paddingBorder);
        }
        
        Color alternateColor = new Color(200, 200, 200);
        Color whiteColor = Color.WHITE;
        if(!comp.getBackground().equals(getSelectionBackground())) {
           Color c = (row % 2 == 0 ? alternateColor : whiteColor);
           comp.setBackground(c);
           c = null;
        }

        return comp;
    }

}
