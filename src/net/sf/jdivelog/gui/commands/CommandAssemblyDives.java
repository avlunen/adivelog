/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: CommandDeleteDives.java
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

import java.util.TreeSet;

import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.model.JDive;

/**
 * Description: Command to delete the dive from the dives collection
 * TODO: make undoable!!!
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 */
public class CommandAssemblyDives implements Command {
    
    private MainWindow mainWindow = null;
    private TreeSet<JDive> divesToAssembly = null;
	@SuppressWarnings("unused")
	private TreeSet<JDive> oldDiveList = null;
    @SuppressWarnings("unused")
	private boolean oldChanged = false;
    @SuppressWarnings("unused")
	private TreeSet<JDive> newDiveList = null;
    
    public CommandAssemblyDives(MainWindow mainWindow, TreeSet<JDive> divesToAssembly) {
        this.mainWindow = mainWindow;
        this.divesToAssembly = divesToAssembly;
    }

    /**
     * @see net.sf.jdivelog.gui.commands.Command#execute()
     */
    public void execute() {
        this.mainWindow.getLogBook().assemblyDives(divesToAssembly);
        // notify about table change, file change
        mainWindow.setChanged(true);
        mainWindow.getLogbookChangeNotifier().notifyLogbookDataChanged();
    }

}
