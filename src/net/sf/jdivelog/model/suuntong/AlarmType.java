/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: AlarmType.java
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

/**
 * Description: enumeration of the warnings which may occur in the Suunto D6 log
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.6 $
 */
public enum AlarmType {
    // @formatter:off
	MANDATORY_SAFETY_STOP_BEGIN(0x01),
	// A No Decompression Dive (Nullzeittauchgang) turned into a Decompression dive.
	DECO_WARNING_BEGIN(0x03),
	ASC_WARNING_BEGIN(0x04),
	MANDATORY_SAFETY_STOP_CEILING_ERROR_BEGIN(0x06),
	BELOW_FLOOR_WARNING_BEGIN(0x07),
	DIVE_TIME_ALARM_BEGIN(0x08),
	DEPTH_ALARM_BEGIN(0x09),
	HIGH_PP02_WARNING_BEGIN(0x0C),
	AIR_TIME_WARNING_BEGIN(0x0D),
	RGBM_WARNING_BEGIN(0x0E),
	MANDATORY_SAFETY_STOP_END(0x81),
	DECO_WARNING_END(0x83),
	ASC_WARNING_END(0x84),
	MANDATORY_SAFETY_STOP_CEILING_ERROR_END(0x86),
	BELOW_FLOOR_WARNING_END(0x87),
	DIVE_TIME_ALARM_END(0x88),
	DEPTH_ALARM_END(0x89),
	HIGH_PP02_WARNING_END(0x8C),
	AIR_TIME_WARNING_END(0x8D),
	RGBM_WARNING_END(0x8E),
	UNKNOWN(0xFF);
    // @formatter:on

    private final byte alarmByte;

    /**
     * Create an alarm.
     * 
     * @param alarmByte
     *            numeric value of the alarm
     */
    AlarmType(int alarmByte) {
        this.alarmByte = (byte) alarmByte;
    }

    /**
     * Get the numeric value of the alarm.
     * 
     * @return numeric value of the alarm
     */
    public byte getAlarmByte() {
        return alarmByte;
    }

    /**
     * Create an alarm.
     * 
     * @param alarmByte
     *            numeric value of the alarm
     * 
     * @return alarm
     */
    public static AlarmType valueOf(byte alarmByte) {
        AlarmType result = null;

        for (AlarmType e : AlarmType.values()) {
            if (e.alarmByte == alarmByte) {
                result = e;
                break;
            }
        }
        if (result == null) {
            result = UNKNOWN;
        }
        return result;
    }
}
