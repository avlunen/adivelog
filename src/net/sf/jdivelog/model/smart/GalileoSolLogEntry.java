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
 * Description: container for one logbook entry of a Galileo Sol file.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 423 $
 */
public class GalileoSolLogEntry extends LogEntry {
    private static final int HEADER_LENGTH = 152;

    public GalileoSolLogEntry(byte[] read_data, int offset) {
        this.read_data = read_data;
        this.offset = offset + HEADER_LENGTH;

        diveDataLength = DiveParser.readULong(read_data, offset + 4);
        diveStartTime = getDateFromAladin((int) DiveParser.readULong(read_data,
                offset + 8));
        utcOffset = DiveParser.readUShort(read_data, offset + 16);
        repNo = DiveParser.readUShort(read_data, offset + 17);
        alarmsDuringDive = DiveParser.readUShort(read_data, offset + 21);
        maximumDepth = new Float(DiveParser.readUInt(read_data, offset + 22)) / 100;
        duration = DiveParser.readUInt(read_data, offset + 26);
        maximumTemperature = new Float(DiveParser.readInt(read_data,
                offset + 28)) / 10;
        minimumTemperature = new Float(DiveParser.readInt(read_data,
                offset + 30)) / 10;
        airTemperature = new Float(DiveParser.readInt(read_data, offset + 32)) / 10;
        surfaceInterval = DiveParser.readUInt(read_data, offset + 34);
        cnsPercentage = DiveParser.readUInt(read_data, offset + 36);
        altitudeLevel = DiveParser.readUInt(read_data, offset + 38);
        po2Limit = DiveParser.readUInt(read_data, offset + 42);
        o2Percentage = DiveParser.readUInt(read_data, offset + 44);

        length = (int) diveDataLength - HEADER_LENGTH - 4;
        offset += HEADER_LENGTH;

        index = offset;
        while (available() > 0) {
            final GalileoSolDTI dti = readDTI();

            if (dti == GalileoSolDTI.DELTA_DEPTH) {
                readDeltaDepth();
            } else if (dti == GalileoSolDTI.DELTA_RBT) {
                index++;
            } else if (dti == GalileoSolDTI.DELTA_TANK_PRESSURE) {
                index++;
            } else if (dti == GalileoSolDTI.DELTA_TEMPERATURE) {
                readDeltaTemperature();
            } else if (dti == GalileoSolDTI.TIME) {
                readTime();
            } else if (dti == GalileoSolDTI.DELTA_HEARTRATE) {
                index++;
            } else if (dti == GalileoSolDTI.ALARMS) {
                readAlarms();
            } else if (dti == GalileoSolDTI.ALARMS2) {
                index += 2;
            } else if (dti == GalileoSolDTI.ABSOLUTE_DEPTH) {
                readAbsoluteDepth();
            } else if (dti == GalileoSolDTI.ABSOLUTE_RBT) {
                index += 2;
            } else if (dti == GalileoSolDTI.ABSOLUTE_TEMPERATURE) {
                readAbsoluteTemperature();
            } else if (dti == GalileoSolDTI.ABSOLUTE_TANK_1_PRESSURE) {
                index += 3;
            } else if (dti == GalileoSolDTI.ABSOLUTE_TANK_2_PRESSURE) {
                index += 3;
            } else if (dti == GalileoSolDTI.ABSOLUTE_TANK_3_PRESSURE) {
                index += 3;
            } else if (dti == GalileoSolDTI.ABSOLUTE_HEARTRATE) {
                index += 2;
            } else if (dti == GalileoSolDTI.BEARING) {
                index += 3;
            } else if (dti == GalileoSolDTI.ALARMS3) {
                index += 2;
            } else {
                System.out.println("received unknown DTI value " + dti);
                break;
            }
        }
    }

    private void readAbsoluteDepth() {
        int depth = DiveParser.ByteToU16(new byte[] { read_data[index + 2],
                read_data[index + 1] });

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

    private void readDeltaDepth() {
        depth += DiveParser.ByteToS7(read_data[index]);
        index++;
        completeSegment();
    }

    private void readDeltaTemperature() {
        temperature += DiveParser.ByteToS4(read_data[index]);
        index++;
    }

    private GalileoSolDTI readDTI() {
        GalileoSolDTI result = null;

        if (available() > 0) {
            int dti = read_data[index] & 0xFF;

            if (dti != 0xFF) {
                if (dti == 0xF0) {
                    // 11110000
                    if (available() > GalileoSolDTI.ALARMS2.getExtraBytes()) {
                        result = GalileoSolDTI.ALARMS2;
                    }
                } else if (dti == 0xF1) {
                    // 11110001
                    if (available() > GalileoSolDTI.ABSOLUTE_DEPTH
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_DEPTH;
                    }
                } else if (dti == 0xF2) {
                    // 11110010
                    if (available() > GalileoSolDTI.ABSOLUTE_RBT
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_RBT;
                    }
                } else if (dti == 0xF3) {
                    // 11110011
                    if (available() > GalileoSolDTI.ABSOLUTE_TEMPERATURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_TEMPERATURE;
                    }
                } else if (dti == 0xF4) {
                    // 11110100
                    if (available() > GalileoSolDTI.ABSOLUTE_TANK_1_PRESSURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_TANK_1_PRESSURE;
                    }
                } else if (dti == 0xF5) {
                    // 11110101
                    if (available() > GalileoSolDTI.ABSOLUTE_TANK_2_PRESSURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_TANK_2_PRESSURE;
                    }
                } else if (dti == 0xF6) {
                    // 11110110
                    if (available() > GalileoSolDTI.ABSOLUTE_TANK_3_PRESSURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_TANK_3_PRESSURE;
                    }
                } else if (dti == 0xF7) {
                    // 11110111
                    if (available() > GalileoSolDTI.ABSOLUTE_HEARTRATE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.ABSOLUTE_HEARTRATE;
                    }
                } else if (dti == 0xF8) {
                    // 11111000
                    if (available() > GalileoSolDTI.BEARING.getExtraBytes()) {
                        result = GalileoSolDTI.BEARING;
                    }
                } else if (dti == 0xF9) {
                    // 11111001
                    if (available() > GalileoSolDTI.ALARMS3.getExtraBytes()) {
                        result = GalileoSolDTI.ALARMS3;
                    }
                } else if ((dti & 0x80) == 0) {
                    // 0ddddddd
                    if (available() > GalileoSolDTI.DELTA_DEPTH
                            .getExtraBytes()) {
                        result = GalileoSolDTI.DELTA_DEPTH;
                    }
                } else if ((dti & 0xE0) == 0x80) {
                    // 100ddddd
                    if (available() > GalileoSolDTI.DELTA_RBT.getExtraBytes()) {
                        result = GalileoSolDTI.DELTA_RBT;
                    }
                } else if ((dti & 0xF0) == 0xA0) {
                    // 1010dddd
                    if (available() > GalileoSolDTI.DELTA_TANK_PRESSURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.DELTA_TANK_PRESSURE;
                    }
                } else if ((dti & 0xF0) == 0xB0) {
                    // 1011dddd
                    if (available() > GalileoSolDTI.DELTA_TEMPERATURE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.DELTA_TEMPERATURE;
                    }
                } else if ((dti & 0xF0) == 0xC0) {
                    // 1100dddd
                    if (available() > GalileoSolDTI.TIME.getExtraBytes()) {
                        result = GalileoSolDTI.TIME;
                    }
                } else if ((dti & 0xF0) == 0xD0) {
                    // 1101dddd
                    if (available() > GalileoSolDTI.DELTA_HEARTRATE
                            .getExtraBytes()) {
                        result = GalileoSolDTI.DELTA_HEARTRATE;
                    }
                } else if ((dti & 0xF0) == 0xE0) {
                    // 1110dddd
                    if (available() > GalileoSolDTI.ALARMS.getExtraBytes()) {
                        result = GalileoSolDTI.ALARMS;
                    }
                }
            }
        }
        return result;
    }

    private void readTime() {
        int count = DiveParser.ByteToU4((byte) (read_data[index]));

        index++;
        for (int i = 1; i <= count; i++) {
            completeSegment();
        }
    }
}
