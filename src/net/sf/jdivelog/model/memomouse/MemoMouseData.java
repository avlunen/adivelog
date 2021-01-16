/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: MemoMouseData.java
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.jdivelog.model.aladin.CurrentStatus;
import net.sf.jdivelog.model.aladin.DepthProfile;
import net.sf.jdivelog.model.aladin.Settings;

/**
 * Description: container for the contents of a MemoMouse log
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 548 $
 */
public class MemoMouseData {
    private final List<LogEntry> logBook = new ArrayList<LogEntry>();

    private final Settings settings;

    private final CurrentStatus currentStatus;

    public MemoMouseData(byte[] read_data, int timeAdjustment)
        throws IOException {
        int[] settingsData = new int[Settings.DATA_SIZE];

        // Aladin type
        settingsData[0] = read_data[8] & 0xFF;

        // serial number
        settingsData[49] = read_data[5] & 0xFF;
        settingsData[50] = read_data[6] & 0xFF;
        settingsData[51] = read_data[7] & 0xFF;
        settings = new Settings(settingsData);

        int[] currentStatusData = new int[CurrentStatus.DATA_SIZE];

        // current time
        currentStatusData[8] = read_data[4] & 0xFF;
        currentStatusData[9] = read_data[3] & 0xFF;
        currentStatusData[10] = read_data[2] & 0xFF;
        currentStatusData[11] = read_data[1] & 0xFF;

        // offset for the newest logbook
        currentStatusData[4] = 0;

        int offset = 5;
        int totalDiveNumbers = 0;
        Date lastEntryTime = null;
        int lastDepthProfileOffset = 0;

        while (offset + 18 < read_data.length) {
            int[] logEntryData = new int[LogEntry.DATA_SIZE];

            for (int index = 0; index < 7; index++) {
                logEntryData[index] = read_data[offset + index + 4] & 0xFF;
            }
            // entry time
            logEntryData[7] = read_data[offset + 14] & 0xFF;
            logEntryData[8] = read_data[offset + 13] & 0xFF;
            logEntryData[9] = read_data[offset + 12] & 0xFF;
            logEntryData[10] = read_data[offset + 11] & 0xFF;
            // water temperature
            logEntryData[11] = read_data[offset + 15] & 0xFF;

            Date entryTime =
                LogEntry.getEntryTime(logEntryData[7], logEntryData[8],
                    logEntryData[9], logEntryData[10], timeAdjustment);

            if ((lastEntryTime != null) && (!entryTime.after(lastEntryTime))) {
                break;
            }
            lastEntryTime = entryTime;

            LogEntry logEntry =
                new LogEntry(logEntryData, settings.aladinType, timeAdjustment);

            logBook.add(logEntry);

            // offset for the newest logbook
            currentStatusData[4]++;

            int depthProfileLength =
                (read_data[offset + 17] & 0xFF) * 256
                    + (read_data[offset + 16] & 0xFF);

            if (depthProfileLength > 0) {
                int[] depthProfileData = new int[depthProfileLength + 1];

                depthProfileData[0] = 0xFF;
                for (int index = 0; index < depthProfileLength; index++) {
                    depthProfileData[index + 1] =
                        read_data[offset + 18 + index] & 0xFF;
                }
                lastDepthProfileOffset += depthProfileLength + 1;

                DepthProfile depthProfile =
                    new DepthProfile(depthProfileData, settings.aladinType);

                logEntry.setDepthProfile(depthProfile);

                // end of profile buffer
                currentStatusData[6] = lastDepthProfileOffset % 256;
                currentStatusData[7] = (lastDepthProfileOffset / 256) << 1;

                // number of dive profiles
                currentStatusData[5]++;
            }

            offset += 18 + depthProfileLength;
            totalDiveNumbers++;
        }

        // total dive numbers
        currentStatusData[2] = totalDiveNumbers / 256;
        currentStatusData[3] = totalDiveNumbers % 256;

        currentStatus = new CurrentStatus(currentStatusData);
    }

    public MemoMouseData(File file, int timeAdjustment) throws IOException {
        this(getFileContent(file), timeAdjustment);
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    private static byte[] getFileContent(File file)
        throws FileNotFoundException, IOException {
        byte[] result = null;
        DataInputStream in =
            new DataInputStream(new BufferedInputStream(new FileInputStream(
                file)));

        try {
            result = new byte[(int) file.length()];
            for (int index = 0; index < result.length; index++) {
                result[index] = (byte) in.read();
            }
        }
        finally {
            in.close();
        }
        return result;
    }

    public Collection<LogEntry> getLogbook() {
        return logBook;
    }

    public Settings getSettings() {
        return settings;
    }

    public String toString() {
        return "logbook: " + logBook + "\n" + "settings: " + settings + "\n"
            + "currentStatus: " + currentStatus + "\n";
    }
}
