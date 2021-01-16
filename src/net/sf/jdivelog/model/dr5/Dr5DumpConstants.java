/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5DumpConstants.java
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
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
package net.sf.jdivelog.model.dr5;

public class Dr5DumpConstants {

    private static final int FILE_OFFSET = 0;

    public static final int CUSTOM_TEXT = FILE_OFFSET + 66; // 18
    public static final int LOCALTIME = FILE_OFFSET + 84; // 4
    public static final int FLIP_SCREEN = FILE_OFFSET + 240; // 1
    public static final int FLIP_SCREEN_NEXT_START = FILE_OFFSET + 241; // 1
    public static final int BRIGHTNESS_MIN = FILE_OFFSET + 242; // 1
    public static final int BRIGHTNESS_MAX = FILE_OFFSET + 243; // 1
    public static final int DEFAULT_VIEW = FILE_OFFSET + 250; // 2
    public static final int DEFAULT_CCR_VIEW = FILE_OFFSET + 252; // 2
    public static final int DEFAULT_VIEW_FALLBACK_SECS = FILE_OFFSET + 254; // 2
    public static final int DIVE_MODE = FILE_OFFSET + 256; // 1
    public static final int GASLIST_NEW = FILE_OFFSET + 257; // 24
    public static final int BUEHLMANN_CONFIG_NEW = FILE_OFFSET + 281; // 10
    public static final int GASCHANGES_NEW = FILE_OFFSET + 291; // 12
    public static final int GASCHANGES_BAILOUT_NEW = FILE_OFFSET + 303; // 12
    public static final int OXYGEN_SETPOINTS_NEW = FILE_OFFSET + 387; // 12
    public static final int OXYGEN_STD_SETPOINT = FILE_OFFSET + 399; // 4

    public static final int OXY_SENSOR_BLOCK = FILE_OFFSET + 412; // 46
    public static final int CALIBRATION_GASES = FILE_OFFSET + 412; // 2
    public static final int CALIBRATION_GRADIENT = FILE_OFFSET + 414; // 12
    public static final int CALIBRATION_GRADIENT_TWOPOINT = FILE_OFFSET + 416; // 12
    public static final int CALIBRATION_INTERSECTION_TWO_POINT = FILE_OFFSET + 428; // 12
    public static final int O2_FRACTION = FILE_OFFSET + 440; // 8;
    public static final int CALIBRATION_GAS_1 = FILE_OFFSET + 448; // 1
    public static final int CALIBRATION_GAS_2 = FILE_OFFSET + 449; // 1
    public static final int OXY_SENSOR_ACTIVE = FILE_OFFSET + 452; // 3
    public static final int VOTING_LOGIC_HILO = FILE_OFFSET + 455; // 1
    public static final int CCR_FIXPOINT_MANUALLY = FILE_OFFSET + 456; // 1
    public static final int OXYGEN_DISPLAY_IN_PERCENT = FILE_OFFSET + 457; // 1

    public static class BuehlmannConfigNew {

        public static final int DESATURATION_MULTIPLIER = 0;
        public static final int NORMAL_GF_HI = 1;
        public static final int EMERGENCY_GF_HI = 2;
        public static final int NORMAL_GF_LO = 3;
        public static final int EMERGENCY_GF_LO = 4;
        public static final int GF_LO_POS_MAX = 5;
        public static final int GF_LO_POS_MIN = 6;
        public static final int LAST_DECO_STOP = 7;
        public static final int SAFETY_DISTANCE_DECO_STOP = 8;
        public static final int SATURATION_MULTIPLIER = 9;
    }

}
