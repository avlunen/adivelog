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
package net.sf.jdivelog.model.suuntong;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jdivelog.ci.ChecksumException;
import net.sf.jdivelog.ci.SuuntoComputerType;
import net.sf.jdivelog.gui.DiveImportWindow;
import net.sf.jdivelog.gui.MainWindow;
import net.sf.jdivelog.gui.resources.Messages;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.SuuntoNGAdapter;

/**
 * Description: loads Suunto NG data files and converts them into JDiveLog
 * format.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class SuuntoNGFileLoader {
    private static final Logger LOGGER = Logger.getLogger(SuuntoNGFileLoader.class.getName());

    private ArrayList<JDive> dives = new ArrayList<JDive>();

    public SuuntoNGFileLoader(MainWindow mainWindow, File[] files, SuuntoComputerType computerModel) {
        for (int i = 0; i < files.length; i++) {
            LogBook logBook = null;

            try {
                Memory memory = Memory.loadFromFile(files[i].getPath());

                // read memory header
                byte[] bytes = readMemory(memory, 0, MemoryHeader.SIZE);
                MemoryHeader memoryHeader = new MemoryHeader(bytes);

                logBook = new LogBook(memoryHeader);

                // read all dive profiles
                int currentDiveOffset = memoryHeader.oldestDiveInBuffer;

                for (int diveNumber = 1; diveNumber <= memoryHeader.numberOfDivesInBuffer; diveNumber++) {
                    // read dive profile header to get offset to next dive
                    // profile header
                    bytes = readMemory(memory, currentDiveOffset, DiveProfileHeader.VYPER_AIR_SIZE);

                    DiveProfileHeader diveProfileHeader = new DiveProfileHeader(bytes);

                    // read dive profile header with dive profile data
                    if (diveProfileHeader.nextDiveOffset > currentDiveOffset) {
                        bytes = readMemory(memory, currentDiveOffset, diveProfileHeader.nextDiveOffset
                                - currentDiveOffset);
                    } else {
                        bytes = readMemory(memory, currentDiveOffset, diveProfileHeader.nextDiveOffset
                                - MemoryHeader.SIZE);
                    }
                    currentDiveOffset = diveProfileHeader.nextDiveOffset;
                    diveProfileHeader = new DiveProfileHeader(bytes);

                    LogEntry logEntry = new LogEntry(diveProfileHeader);

                    // copy dive profile data into separate array
                    byte[] diveProfile = new byte[bytes.length - diveProfileHeader.getSize()];

                    System.arraycopy(bytes, diveProfileHeader.getSize(), diveProfile, 0, diveProfile.length);

                    DiveProfileData diveProfileData = new DiveProfileData(diveProfile,
                            diveProfileHeader.sampleRecordingInterval, diveProfileHeader.temperatureRecordingInterval,
                            diveProfileHeader.offsetToFirstMarker, diveProfileHeader.hasTankPressureInformation());

                    logEntry.setProfile(diveProfileData.getProfile());
                    logBook.addLogEntry(logEntry);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "failed to load Suunto NG file", e);
            } finally {
                if (logBook != null) {
                    dives.addAll(new SuuntoNGAdapter(logBook));
                }
            }
        }

        // open the diveImportDataTrak window to mark the dives for import
        if (mainWindow != null) {
            DiveImportWindow daw = new DiveImportWindow(mainWindow, dives, Messages.getString("diveimportsuuntong"));

            daw.setVisible(true);
        }
    }

    private byte[] readMemory(Memory memory, int address, int count) throws IOException, ChecksumException,
            InterruptedException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] bytes = null;

        if (address + count < Memory.SIZE) {
            result.write(memory.read(address, count));
        } else {
            bytes = memory.read(address, Memory.SIZE - address);
            result.write(bytes);
            result.write(memory.read(MemoryHeader.SIZE, count - bytes.length));
        }
        return result.toByteArray();
    }

    public static void main(String[] args) {
        new SuuntoNGFileLoader(null, new File[] { new File(args[0]) }, SuuntoComputerType.VYPER_AIR);
    }
}
