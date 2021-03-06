/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: AladinData.java
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
package net.sf.jdivelog.model.aladin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: container for the contents of an Aladin log file
 * 
 * For further information see <a href="http://pakuro.is.sci.toho-u.ac.jp/aladin/protocol-e.html">Protocol and Data Structure of Uwatec Aladin Dive Computers</a>
 * @author Andr&eacute; Schenk
 * @version $Revision: 882 $
 */
public class AladinData
{
    public static final int DATA_SIZE = 2046;
    public static final int SETTINGS_OFFSET = 0x7bc;
    public static final int CURRENT_STATUS_OFFSET = 0x7f0;
    public static final int NEWEST_LOGENTRY_OFFSET = 0x7f4;
    public static final int DEPTH_PROFILE_BUFFER_SIZE = 0x600;

    private List<DepthProfile> depthProfiles = new ArrayList<DepthProfile> ();
    private List<LogEntry>     logbook       = new ArrayList<LogEntry> ();
    private Settings           settings      = null;
    private CurrentStatus      currentStatus = null;

    public AladinData (int [] read_data)
    {
        parseData (read_data);
    }

    public AladinData (String fileName)
        throws IOException
    {
        if (fileName == null) {
            throw new IllegalArgumentException ("file name = null");
        }

        DataInputStream in = new DataInputStream
            (new BufferedInputStream (new FileInputStream (fileName)));
        int [] read_data = new int [DATA_SIZE];

        for (int index = 0; index < read_data.length; index++) {
            read_data [index] = in.read () & 0xFF;
        }
        parseData (read_data);
        in.close ();
    }

    public CurrentStatus getCurrentStatus ()
    {
        return currentStatus;
    }

    public List<DepthProfile> getDepthProfiles ()
    {
        return depthProfiles;
    }

    public List<LogEntry> getLogbook ()
    {
        return logbook;
    }

    public Settings getSettings ()
    {
        return settings;
    }

    private void parseData (int [] read_data)
    {
        if ((read_data == null) || (read_data.length < DATA_SIZE)) {
            throw new IllegalArgumentException
                ("need an array with at least " + DATA_SIZE + " bytes");
        }

        // read settings
        int [] bytes = new int [Settings.DATA_SIZE];

        System.arraycopy (read_data, SETTINGS_OFFSET, bytes, 0, bytes.length);
        settings = new Settings (bytes);

        // read current status
        bytes = new int [CurrentStatus.DATA_SIZE];
        System.arraycopy (read_data, CURRENT_STATUS_OFFSET, bytes, 0, bytes.length);
        currentStatus = new CurrentStatus (bytes);

        // read depth profiles

        // find start of newest depth profile
        int profileOffset = currentStatus.endOfProfileBuffer;

        for (;;) {
            if (profileOffset == DEPTH_PROFILE_BUFFER_SIZE) {
                profileOffset = 0x0;
            }
            if (read_data [profileOffset] == 0xff) {
                break;
            }
            profileOffset++;
        }

        int offset = profileOffset;

        for (int index = 1; index <= currentStatus.numberOfDiveProfiles;
             index++) {
            // copy next depth profile into buffer
            int [] buffer       = new int [DEPTH_PROFILE_BUFFER_SIZE];
            int    bufferOffset = 0;

            for (;;) {
                if (offset == DEPTH_PROFILE_BUFFER_SIZE) {
                    offset = 0x0;
                }
                if ((bufferOffset >= buffer.length) ||
                    (offset == currentStatus.endOfProfileBuffer) ||
                    ((bufferOffset > 0) && (read_data [offset] == 0xff))) {
                    break;
                }
                buffer [bufferOffset++] = read_data [offset++];
            }

            int [] result = new int [bufferOffset];

            System.arraycopy (buffer, 0, result, 0, result.length);

            DepthProfile depthProfile = new DepthProfile (result, settings.aladinType);

            depthProfiles.add (depthProfile);
        }

        // read log book
        int newestLogbook = read_data [NEWEST_LOGENTRY_OFFSET];

        if (newestLogbook == 0) {
            newestLogbook = 37;
        }

        int numberOfDives = currentStatus.totalDiveNumbers;

        if (numberOfDives > 37) {
            numberOfDives = 37;
        }
        for (int index = 1; index <= numberOfDives; index++) {
            int logOffset = ((newestLogbook - numberOfDives + index + 36) % 37)
                * 12 + DEPTH_PROFILE_BUFFER_SIZE;

            bytes = new int [12];
            System.arraycopy (read_data, logOffset, bytes, 0, bytes.length);
            logbook.add (new LogEntry (bytes, settings.aladinType));
        }
    }

    public String toString ()
    {
        return
            "depthProfiles: " + depthProfiles + "\n" +
            "logbook: "       + logbook       + "\n" +
            "settings: "      + settings      + "\n" +
            "currentStatus: " + currentStatus + "\n";
    }
}
