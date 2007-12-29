package net.uidl.util;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * ButtonRenderer for displaying buttons in JTables.
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class ButtonRenderer extends JButton implements TableCellRenderer
{

    public ButtonRenderer()
    {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {
        if (isSelected)
        {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else
        {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
