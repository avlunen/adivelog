/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SalinityFixCommand.java
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 * 
 * This file is part of JDiveLog.
 * JDiveLog is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * JDiveLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JDiveLog; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jdivelog.gui.commands;

import java.util.ArrayList;
import java.util.List;

import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.model.JDive;

/**
 * Command for fixing the salinity
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 */
public class SalinityFixCommand implements UndoableCommand {
    
    private final MainWindow mainWindow;
    private final List<JDive> dives;
    private final double oldSalinity;
    private final double newSalinity;
    private ArrayList<JDive> newDives;
    private boolean oldChanged;

    public SalinityFixCommand(MainWindow mainWindow, List<JDive> dives, double oldSalinity, double newSalinity) {
        this.mainWindow = mainWindow;
        this.dives = dives;
        this.oldSalinity = oldSalinity;
        this.newSalinity = newSalinity;
    }

    public void redo() {
        mainWindow.getLogBook().getDives().removeAll(dives);
        mainWindow.getLogBook().getDives().addAll(newDives);
        mainWindow.setChanged(true);
    }

    public void undo() {
        mainWindow.getLogBook().getDives().removeAll(newDives);
        mainWindow.getLogBook().getDives().addAll(dives);
        mainWindow.setChanged(oldChanged);
    }

    public void execute() {
        oldChanged = mainWindow.isChanged();
        SalinityFixTool ft = new SalinityFixTool(oldSalinity, newSalinity);
        newDives = new ArrayList<JDive>();
        for (JDive oldDive : dives) {
            JDive newDive = ft.fix(oldDive);
            newDives.add(newDive);
        }
        redo();
    }

}
