/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SuuntoNGFileLoader.java
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
package net.sf.jdivelog.model.sri;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jdivelog.ci.sri.format.DiveLogParser;
import net.sf.jdivelog.gui.DiveImportWindow;
import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.PredatorAdapter;

/**
 * Description: loads Shearwater Predator data files and converts them into
 * JDiveLog format.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class PredatorFileLoader {
    private static final Logger LOGGER = Logger
            .getLogger(PredatorFileLoader.class.getName());

    private ArrayList<JDive> dives = new ArrayList<JDive>();

    public PredatorFileLoader(MainWindow mainWindow, File[] files) {
        final DiveLogParser parser = new DiveLogParser();

        for (int i = 0; i < files.length; i++) {
            try {
                final DataInputStream in = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(files[i])));
                final byte[] dump = new byte[(int) files[i].length()];

                in.read(dump);
                dives.addAll(new PredatorAdapter(parser.getDiveLogs(dump), null));
                in.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "failed to load Predator file", e);
            }
        }

        // open the diveImportDataTrak window to mark the dives for import
        if (mainWindow != null) {
            DiveImportWindow daw = new DiveImportWindow(mainWindow,
                    dives, Messages.getString("diveimportpredator"));

            daw.setVisible(true);
        }
    }
}
