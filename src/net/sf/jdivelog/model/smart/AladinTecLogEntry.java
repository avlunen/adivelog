/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: AladinTecLogEntry.java
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
package net.sf.jdivelog.model.smart;

import net.sf.jdivelog.util.DiveParser;

/**
 * Description: container for one logbook entry of an Aladin Tec 2G file.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 423 $
 */
public class AladinTecLogEntry extends LogEntry {
    private static final int HEADER_LENGTH = 116;

    public AladinTecLogEntry(byte[] read_data, int offset) {
        this.read_data = read_data;
        this.offset = offset + HEADER_LENGTH;

        diveDataLength = DiveParser.readULong(read_data, offset + 4);
        diveStartTime = getDateFromAladin((int) DiveParser.readULong(read_data,
                offset + 8));
        // unknown1 = readULong (read_data, offset + 12);
        utcOffset = DiveParser.readUShort(read_data, offset + 16);
        repNo = DiveParser.readUShort(read_data, offset + 17);
        mbLevel = DiveParser.readUShort(read_data, offset + 18);
        battery = DiveParser.readUShort(read_data, offset + 19);
        // unknown2 = readUShort (read_data, offset + 20);
        alarmsDuringDive = DiveParser.readUShort(read_data, offset + 21);
        maximumDepth = new Float(DiveParser.readUInt(read_data, offset + 22)) / 100;
        duration = DiveParser.readUInt(read_data, offset + 26);
        minimumTemperature = new Float(DiveParser.readInt(read_data,
                offset + 30)) / 10;
        maximumTemperature = new Float(DiveParser.readInt(read_data,
                offset + 28)) / 10;
        o2Percentage = DiveParser.readUInt(read_data, offset + 30);
        airTemperature = new Float(DiveParser.readInt(read_data, offset + 32)) / 10;
        surfaceInterval = DiveParser.readUInt(read_data, offset + 34);
        cnsPercentage = DiveParser.readUInt(read_data, offset + 36);
        altitudeLevel = DiveParser.readUInt(read_data, offset + 38);
        // unknown3 = readUInt (read_data, offset + 40);
        po2Limit = DiveParser.readUInt(read_data, offset + 42);
        depthLimit = DiveParser.readUInt(read_data, offset + 44);
        // unknown4 = readUInt (read_data, offset + 46);
        desatBeforeDive = DiveParser.readUInt(read_data, offset + 48);
        // unknown5 = readUInt (read_data, offset + 50);

        length = (int) diveDataLength - HEADER_LENGTH - 4;
        offset += HEADER_LENGTH;

        index = offset;
        while (available() > 0) {
            final AladinTecDTI dti = readDTI();

            if (dti == AladinTecDTI.DELTA_DEPTH) {
                readDeltaDepth();
            } else if (dti == AladinTecDTI.ALARMS2) {
                readAlarms2();
            } else if (dti == AladinTecDTI.DELTA_TEMPERATURE) {
                readDeltaTemperature();
            } else if (dti == AladinTecDTI.DELTA_DEPTH2) {
                readDeltaDepth2();
            } else if (dti == AladinTecDTI.DELTA_TEMPERATURE2) {
                readDeltaTemperature2();
            } else if (dti == AladinTecDTI.ALARMS) {
                readAlarms();
            } else if (dti == AladinTecDTI.TIME) {
                readTime();
            } else if (dti == AladinTecDTI.ABSOLUTE_DEPTH) {
                readAbsoluteDepth();
            } else if (dti == AladinTecDTI.ABSOLUTE_TEMPERATURE) {
                readAbsoluteTemperature();
            } else {
                System.out.println("received unknown DTI value " + dti);
                break;
            }
        }
    }

    private void readAbsoluteDepth() {
        int depth = DiveParser.ByteToU17(new byte[] { read_data[index + 2],
                read_data[index + 1], read_data[index + 0] });

        index += 3;
        if (this.depthCalibration == 0) {
            this.depthCalibration = depth;
        } else {
            this.depth = depth - this.depthCalibration;
            completeSegment();
        }
    }

    private void readAbsoluteTemperature() {
        temperature = DiveParser.ByteToS16(new byte[] { read_data[index + 2],
                read_data[index + 1] });
        index += 3;
    }

    private void readAlarms() {
        alarms = (byte) DiveParser.ByteToU4(read_data[index]);
        index++;
    }

    private void readAlarms2() {
        alarms = (byte) DiveParser.ByteToU7(read_data[index + 1]);
        index += 2;
    }

    private void readDeltaDepth() {
        depth += DiveParser.ByteToS7(read_data[index]);
        index++;
        completeSegment();
    }

    private void readDeltaTemperature() {
        temperature += DiveParser.ByteToS6(read_data[index]);
        index++;
    }

    private void readDeltaDepth2() {
        depth += DiveParser.ByteToS11(new byte[] { read_data[index + 0],
                read_data[index + 1] });
        index += 2;
        completeSegment();
    }

    private void readDeltaTemperature2() {
        temperature += DiveParser.ByteToS10(new byte[] { read_data[index + 0],
                read_data[index + 1] });
        index += 2;
    }

    private AladinTecDTI readDTI() {
        AladinTecDTI result = null;

        if (available() > 0) {
            int dti = read_data[index] & 0xFF;

            if (dti != 0xFF) {
                if (dti == 0xFE) {
                    // 11111110
                    if (available() > AladinTecDTI.ABSOLUTE_TEMPERATURE
                            .getExtraBytes()) {
                        result = AladinTecDTI.ABSOLUTE_TEMPERATURE;
                    }
                } else if ((dti & 0xFE) == 0xFC) {
                    // 1111110d
                    if (available() > AladinTecDTI.ABSOLUTE_DEPTH
                            .getExtraBytes()) {
                        result = AladinTecDTI.ABSOLUTE_DEPTH;
                    }
                } else if ((dti & 0xFC) == 0xF8) {
                    // 111110dd
                    if (available() > AladinTecDTI.DELTA_TEMPERATURE2
                            .getExtraBytes()) {
                        result = AladinTecDTI.DELTA_TEMPERATURE2;
                    }
                } else if ((dti & 0xF8) == 0xF0) {
                    // 11110ddd
                    if (available() > AladinTecDTI.DELTA_DEPTH2.getExtraBytes()) {
                        result = AladinTecDTI.DELTA_DEPTH2;
                    }
                } else if ((dti & 0xF0) == 0xE0) {
                    // 1110dddd
                    if (available() > AladinTecDTI.ALARMS.getExtraBytes()) {
                        result = AladinTecDTI.ALARMS;
                    }
                } else if ((dti & 0xE0) == 0xC0) {
                    // 110ddddd
                    if (available() > AladinTecDTI.TIME.getExtraBytes()) {
                        result = AladinTecDTI.TIME;
                    }
                } else if ((dti & 0xC0) == 0x80) {
                    // 10dddddd
                    if (available() > AladinTecDTI.DELTA_TEMPERATURE
                            .getExtraBytes()) {
                        result = AladinTecDTI.DELTA_TEMPERATURE;
                    }
                } else if ((dti & 0x80) == 0) {
                    // 0ddddddd
                    if (available() > AladinTecDTI.DELTA_DEPTH.getExtraBytes()) {
                        result = AladinTecDTI.DELTA_DEPTH;
                    }
                }
            }
        }
        return result;
    }

    private void readTime() {
        int count = DiveParser.ByteToU5(read_data[index]);

        index++;
        for (int i = 1; i <= count; i++) {
            completeSegment();
        }
    }
}
