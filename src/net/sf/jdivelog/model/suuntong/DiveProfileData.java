/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveProfileData.java
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

import java.util.LinkedList;
import java.util.List;

import net.sf.jdivelog.util.DiveParser;

/**
 * Description: container for the dive profile data of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class DiveProfileData {
    private int time = 0;

    private int currentSampleNumber = 0;

    private int offset = 0;

    private int nextSampleMarker = 0;

    private Event event = null;

    private List<DepthProfileEntry> profile = new LinkedList<DepthProfileEntry>();

    public DiveProfileData(byte[] bytes, int sampleRecordingInterval,
            int temperatureRecordingInterval, int offsetToFirstMaker,
            boolean hasTankPressureInformation) {
        if (bytes == null) {
            throw new IllegalArgumentException("parameter \"bytes\" is null");
        }

        nextSampleMarker = offsetToFirstMaker;

        boolean done = false;

        checkForEvent(bytes);
        while (!done && (offset < bytes.length - 5)) {
            int depth = DiveParser.readUInt(bytes, offset);

            offset += 2;

            int pressure = 0;

            if (hasTankPressureInformation) {
                pressure = DiveParser.readUInt(bytes, offset);
                offset += 2;
            }

            currentSampleNumber++;

            Integer temperature = null;

            if (temperatureRecordingInterval != 0
                    && currentSampleNumber % temperatureRecordingInterval == 1) {
                temperature = Integer.valueOf(DiveParser.readUShort(bytes,
                        offset++));
            }

            profile.add(new DepthProfileEntry(time, depth, pressure,
                    temperature, (event != null ? event.getAlarms() : null)));
            event = null;
            time += sampleRecordingInterval;
            checkForEvent(bytes);
        }
    }

    /**
     * Check if the current position is the position of the next event. If it
     * is, then create an Event object.
     * 
     * @param bytes
     */
    private void checkForEvent(byte[] bytes) {
        if (currentSampleNumber == nextSampleMarker) {
            event = new Event(bytes, offset, currentSampleNumber);
            offset = event.getOffset();
            nextSampleMarker = event.getCurrentSampleMarker()
                    + event.getNextSampleOffset();
        }
    }

    public List<DepthProfileEntry> getProfile() {
        return profile;
    }
}
