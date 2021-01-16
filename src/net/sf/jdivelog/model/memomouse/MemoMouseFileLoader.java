/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: MemoMouseFileLoader.java
 *
 * @author Andr&eacute; Schenk <andre_schenk@users.sourceforge.net>
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
package net.sf.jdivelog.model.memomouse;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jdivelog.gui.DiveImportWindow;
import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.MemoMouseAdapter;

/**
 * Description: loads MemoMouse data files and converts them into JDiveLog
 * format.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class MemoMouseFileLoader {
    private static final Logger LOGGER = Logger
        .getLogger(MemoMouseFileLoader.class.getName());

    public MemoMouseFileLoader(MainWindow mainWindow, File[] files,
        int timeAdjustment) {
        ArrayList<JDive> dives = new ArrayList<JDive>();

        for (int i = 0; i < files.length; i++) {
            try {
                dives.addAll(new MemoMouseAdapter(new MemoMouseData(files[i],
                    timeAdjustment)));
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, "failed to load MemoMouse file", e);
            }
        }

        // open the dive import window to mark the dives for import
        if (mainWindow != null) {
            DiveImportWindow daw =
                new DiveImportWindow(mainWindow, dives,
                    Messages.getString("diveimportmemomouse"));

            daw.setVisible(true);
        }
    }
}
