/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveProfileHeader.java
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

import net.sf.jdivelog.util.DiveParser;

/**
 * Description: container for the dive profile header of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.5 $
 */
public class DiveProfileHeader {
    public static final int D6_SIZE = 0x4B;
    public static final int VYPER_AIR_SIZE = 0x4E;

    public final int prevDiveOffset;

    public final int nextDiveOffset;

    public final int maxDepth;

    public final int diveTime;

    public final int unknown;

    public final int hour;

    public final int minute;

    public final int second;

    public final int year;

    public final int month;

    public final int day;

    public final int sampleRecordingInterval;

    public final int gasModel;
    
    public final int o2percent1;
    public final int o2percent2;
    public final int o2percent3;

    public final int repetetiveDiveNumber;

    public final int temperatureRecordingInterval;

    public final int offsetToFirstMarker;

    private final int size;

    public DiveProfileHeader(byte[] bytes) {
        if ((bytes == null) || (bytes.length < D6_SIZE)) {
            throw new IllegalArgumentException("parameter \"bytes\" too short");
        }

        prevDiveOffset = DiveParser.readUInt(bytes, 0x00);
        nextDiveOffset = DiveParser.readUInt(bytes, 0x02);
        maxDepth = DiveParser.readUInt(bytes, 0x0D);
        diveTime = DiveParser.readUInt(bytes, 0x0F);
        unknown = DiveParser.readUInt(bytes, 0x11);
        hour = DiveParser.readUShort(bytes, 0x15);
        minute = DiveParser.readUShort(bytes, 0x16);
        second = DiveParser.readUShort(bytes, 0x17);
        year = DiveParser.readUInt(bytes, 0x18);
        month = DiveParser.readUShort(bytes, 0x1A) - 1;
        day = DiveParser.readUShort(bytes, 0x1B);
        sampleRecordingInterval = DiveParser.readUShort(bytes, 0x1C);
        gasModel = DiveParser.readUShort(bytes, 0x1D);
        repetetiveDiveNumber = DiveParser.readUShort(bytes, 0x1E);
        if(gasModel == 1) { //Nitrox mode
        	o2percent1 = DiveParser.readUShort(bytes, 0x25);
        	o2percent2 = DiveParser.readUShort(bytes, 0x26);
        	o2percent3 = DiveParser.readUShort(bytes, 0x27);
        } else {
        	o2percent1 = 0;
        	o2percent2 = 0;
        	o2percent3 = 0;
        }
        if (hasTankPressureInformation()) {
            temperatureRecordingInterval = DiveParser.readUShort(bytes, 0x47);
        } else {
            temperatureRecordingInterval = DiveParser.readUShort(bytes, 0x44);
        }
        if (hasTankPressureInformation()) {
            offsetToFirstMarker = DiveParser.readUInt(bytes, 0x4C);
        } else {
            offsetToFirstMarker = DiveParser.readUInt(bytes, 0x49);
        }
        size = hasTankPressureInformation() ? VYPER_AIR_SIZE : D6_SIZE;
    }

    /**
     * Get the size of this dive profile header.
     * 
     * @return dive profile header size
     */
    public int getSize() {
        return size;
    }

    public boolean hasTankPressureInformation() {
        return unknown != 0xFFFF;
    }
}
