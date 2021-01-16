/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: OSTCProtocol110119Mk2.java
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
package net.sf.jdivelog.ci.ostc;

import java.util.Map;

/**
 * OSTC Protocol for Fimware >=1.90
 */
public class OSTCProtocol110523Mk2 extends OSTCProtocol110119Mk2 {

    private static final VersionRange FIRMWARE_VERSIONS = new VersionRange(VersionRange.getVersion("1.90"),
            VersionRange.getVersion("1.90"));
    private static final String[] HASHKEYS = new String[] {};

    protected static final Feature CF54MK2 = new Feature("ostc_cf54mk2", RadioButtonValue.class); // Shallow
                                                                                                  // stop
                                                                                                  // 1st
    protected static final Feature CF55MK2 = new Feature("ostc_cf55mk2", EightBitCustomFunction.class); // Gas
                                                                                                        // switch
                                                                                                        // time
    protected static final Feature CF56MK2 = new Feature("ostc_cf56mk2", EightBitCustomFunction.class); // Gas
                                                                                                        // consumption
                                                                                                        // bottom
                                                                                                        // gas
    protected static final Feature CF57MK2 = new Feature("ostc_cf57mk2", EightBitCustomFunction.class); // Gas
                                                                                                        // consumption
                                                                                                        // ascent
                                                                                                        // gas
    protected static final Feature CF58MK2 = new Feature("ostc_cf58mk2", EightBitCustomFunction.class); // future
                                                                                                        // TTS,
                                                                                                        // deco
                                                                                                        // prognosis

    private static final Feature[] ALL_FEATURES = { SERIALNUMBER, TOTALDIVES, GAS1, GAS1DEFAULT, GAS2, GAS2DEFAULT,
            GAS3, GAS3DEFAULT, GAS4, GAS4DEFAULT, GAS5, GAS5DEFAULT, GAS6CURRENT, STARTGAS, DECOTYPE, SALINITY, CF00,
            CF01, CF02, CF03, CF04, CF05, CF06, CF07, CF08, CF09, CF10, CF11, CF12, CF13, CF14, CF15, CF16, CF17, CF18,
            CF19, CF20, CF21, CF22, CF23, CF24, CF25, CF26, CF27, CF28, CF29, CF30, CF31, CF32, CF33, CF34MK2, CF35MK2,
            CF36MK2, CF37MK2, CF38, CF39MK2, CF40MK2, CF41MK2, CF42, CF43MK2, CF44MK2, CF45MK2, CF46MK2, CF47MK2,
            CF48MK2, CF49MK2, CF50MK2, CF51MK2, CF52MK2, CF53MK2, CF54MK2, CF55MK2, CF56MK2, CF57MK2, CF58MK2,
            CUSTOM_TEXT };

    @Override
    protected Map<Feature, OSTCValue> parseSettings2(byte[] data) {
        Map<Feature, OSTCValue> result = super.parseSettings2(data);
        int offset = 212;

        offset += 4;
        int def = ParseUtil.parseCFValue(data, offset);
        int cur = ParseUtil.parseCFValue(data, offset + 2);
        result.put(CF54MK2, new RadioButtonValue(cur, def, new int[] { 1, 0 }, new String[] { "ostc_cf54mk2_shallow",
                "ostc_cf54mk2_deep" }));

        offset += 4;
        def = ParseUtil.parseCFValue(data, offset);
        cur = ParseUtil.parseCFValue(data, offset + 2);
        result.put(CF55MK2, new EightBitCustomFunction(def, cur, 0));

        offset += 4;
        def = ParseUtil.parseCFValue(data, offset);
        cur = ParseUtil.parseCFValue(data, offset + 2);
        result.put(CF56MK2, new EightBitCustomFunction(def, cur, 20));

        offset += 4;
        def = ParseUtil.parseCFValue(data, offset);
        cur = ParseUtil.parseCFValue(data, offset + 2);
        result.put(CF57MK2, new EightBitCustomFunction(def, cur, 20));

        offset += 4;
        def = ParseUtil.parseCFValue(data, offset);
        cur = ParseUtil.parseCFValue(data, offset + 2);
        result.put(CF58MK2, new EightBitCustomFunction(def, cur, 0));

        return result;
    }

    @Override
    protected void store2(Map<Feature, OSTCValue> data, byte[] buffer) {
        super.store2(data, buffer);
        int offset = 208;

        offset += 4;
        RadioButtonValue rbv = (RadioButtonValue) data.get(CF54MK2);
        if (rbv != null) {
            buffer[offset] = (byte) (rbv.getSelectedValue() & 0xff);
            buffer[offset + 1] = (byte) ((rbv.getSelectedValue() / 256) & 0xff); // should
                                                                                 // be
                                                                                 // 0!
            buffer[offset + 2] = (byte) (rbv.getSelectedValue() & 0xff);
            buffer[offset + 3] = (byte) ((rbv.getSelectedValue() / 256) & 0xff); // should
                                                                                 // be
                                                                                 // 0!
        }

        offset += 4;
        EightBitCustomFunction cf8 = (EightBitCustomFunction) data.get(CF55MK2);
        if (cf8 != null) {
            buffer[offset] = (byte) (cf8.defaultValue() & 0xff);
            buffer[offset + 1] = (byte) ((cf8.defaultValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
            buffer[offset + 2] = (byte) (cf8.currentValue() & 0xff);
            buffer[offset + 3] = (byte) ((cf8.currentValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
        }

        offset += 4;
        cf8 = (EightBitCustomFunction) data.get(CF56MK2);
        if (cf8 != null) {
            buffer[offset] = (byte) (cf8.defaultValue() & 0xff);
            buffer[offset + 1] = (byte) ((cf8.defaultValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
            buffer[offset + 2] = (byte) (cf8.currentValue() & 0xff);
            buffer[offset + 3] = (byte) ((cf8.currentValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
        }

        offset += 4;
        cf8 = (EightBitCustomFunction) data.get(CF57MK2);
        if (cf8 != null) {
            buffer[offset] = (byte) (cf8.defaultValue() & 0xff);
            buffer[offset + 1] = (byte) ((cf8.defaultValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
            buffer[offset + 2] = (byte) (cf8.currentValue() & 0xff);
            buffer[offset + 3] = (byte) ((cf8.currentValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
        }

        offset += 4;
        cf8 = (EightBitCustomFunction) data.get(CF58MK2);
        if (cf8 != null) {
            buffer[offset] = (byte) (cf8.defaultValue() & 0xff);
            buffer[offset + 1] = (byte) ((cf8.defaultValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
            buffer[offset + 2] = (byte) (cf8.currentValue() & 0xff);
            buffer[offset + 3] = (byte) ((cf8.currentValue() / 256) & 0xff); // should
                                                                             // be
                                                                             // 0!
        }
    }

    @Override
    public VersionRange getFirmwareVersions() {
        return FIRMWARE_VERSIONS;
    }

    @Override
    public String[] getHashkeys() {
        return HASHKEYS;
    }

    @Override
    public Feature[] getFeatures() {
        return ALL_FEATURES;
    }
}
