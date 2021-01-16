/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5SettingsConverter.java
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

import net.sf.jdivelog.model.Mix;
import net.sf.jdivelog.model.dr5.Dr5DumpConstants.BuehlmannConfigNew;
import net.sf.jdivelog.model.dr5.Dr5Settings.DiveMode;
import net.sf.jdivelog.model.dr5.Dr5Settings.Dr5DiveView;

public class Dr5SettingsWriter {

    private final byte[] dump;

    public Dr5SettingsWriter(byte[] dump) {
        this.dump = dump;
    }

    public Dr5Settings read() {
        Dr5Settings result = new Dr5Settings();
        ByteArrayAccessor accessor = new ByteArrayAccessor(dump);
        result.setFlipScreen(dump[Dr5DumpConstants.FLIP_SCREEN] == 1);
        result.setFlipScreen(dump[Dr5DumpConstants.FLIP_SCREEN_NEXT_START] == 1);
        result.setBrightMin(dump[Dr5DumpConstants.BRIGHTNESS_MIN] & 0xff);
        result.setBrightMax(dump[Dr5DumpConstants.BRIGHTNESS_MAX] & 0xff);
        result.setDefaultView(Dr5Settings.Dr5DiveView.fromVal(accessor.readUInt(Dr5DumpConstants.DEFAULT_VIEW)));
        result.setDefaultCcrView(Dr5DiveView.fromVal(accessor.readUInt(Dr5DumpConstants.DEFAULT_CCR_VIEW)));
        result.setDefaultViewFallback(accessor.readUInt(Dr5DumpConstants.DEFAULT_VIEW_FALLBACK_SECS));
        result.setMode(DiveMode.fromVal(dump[Dr5DumpConstants.DIVE_MODE]));

        result.setMixes(new Mix[12]);
        for (int i = 0; i < 12; i++) {
            result.getMixes()[i] = readMix(i);
        }
        result.setBuehlmann(new Dr5SettingsBuehlmann());
        result.getBuehlmann().setNormalGfLo(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.NORMAL_GF_LO] & 0xff);
        result.getBuehlmann().setNormalGfHi(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.NORMAL_GF_HI] & 0xff);
        result.getBuehlmann().setEmergencyGfLo(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.EMERGENCY_GF_LO] & 0xff);
        result.getBuehlmann().setEmergencyGfHi(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.EMERGENCY_GF_HI] & 0xff);
        result.getBuehlmann().setDesaturationMultiplier(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.DESATURATION_MULTIPLIER] & 0xff);
        result.getBuehlmann().setSaturationMultiplier(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.SATURATION_MULTIPLIER] & 0xff);
        result.getBuehlmann().setGfLoPosMin(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.GF_LO_POS_MIN] & 0xff);
        result.getBuehlmann().setGfLoPosMax(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.GF_LO_POS_MAX] & 0xff);
        result.getBuehlmann().setLastStop(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.LAST_DECO_STOP] & 0xff);
        result.getBuehlmann().setSafetyDistanceDecoStop(dump[Dr5DumpConstants.BUEHLMANN_CONFIG_NEW + BuehlmannConfigNew.SAFETY_DISTANCE_DECO_STOP] & 0xff);

        result.setOxygenDump(new Dr5SettingsOxygenDump());
        byte[] oxydump = new byte[46];
        System.arraycopy(dump, Dr5DumpConstants.OXY_SENSOR_BLOCK, oxydump, 0, oxydump.length);
        result.getOxygenDump().setDump(oxydump);
        return result;
    }
    
    public void write(Dr5Settings s) {
        ByteArrayAccessor accessor = new ByteArrayAccessor(dump);
        accessor.writeBool(Dr5DumpConstants.FLIP_SCREEN, s.isFlipScreen());
        accessor.writeBool(Dr5DumpConstants.FLIP_SCREEN_NEXT_START, s.isFlipScreenNextStart());
        accessor.writeUByte(Dr5DumpConstants.BRIGHTNESS_MIN, s.getBrightMin());
        accessor.writeUByte(Dr5DumpConstants.BRIGHTNESS_MAX, s.getBrightMax());
        accessor.writeUInt(Dr5DumpConstants.DEFAULT_VIEW, s.getDefaultView().getVal());
        accessor.writeUInt(Dr5DumpConstants.DEFAULT_CCR_VIEW, s.getDefaultCcrView().getVal());
        accessor.writeUInt(Dr5DumpConstants.DEFAULT_VIEW_FALLBACK_SECS, s.getDefaultViewFallback());
        accessor.writeByte(Dr5DumpConstants.DIVE_MODE, s.getMode().getVal());
        for (int i=0; i<12;i++) {
            writeMix(i, s.getMixes()[i]);
        }
        if (s.getBuehlmann() != null) {
            Dr5SettingsBuehlmann b = s.getBuehlmann();
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.NORMAL_GF_LO, b.getNormalGfLo());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.NORMAL_GF_HI, b.getNormalGfHi());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.EMERGENCY_GF_LO, b.getEmergencyGfLo());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.EMERGENCY_GF_HI, b.getEmergencyGfHi());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.DESATURATION_MULTIPLIER, b.getDesaturationMultiplier());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.SATURATION_MULTIPLIER, b.getSaturationMultiplier());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.GF_LO_POS_MIN, b.getGfLoPosMin());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.GF_LO_POS_MAX, b.getGfLoPosMax());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.LAST_DECO_STOP, b.getLastStop());
            accessor.writeUByte(Dr5DumpConstants.BUEHLMANN_CONFIG_NEW+BuehlmannConfigNew.SAFETY_DISTANCE_DECO_STOP, b.getSafetyDistanceDecoStop());
        }
        if (s.getOxygenDump() != null) {
            System.arraycopy(s.getOxygenDump().getDump(), 0, dump, Dr5DumpConstants.OXY_SENSOR_BLOCK, s.getOxygenDump().getDump().length);
        }
    }

    private Mix readMix(int id) {
        int n2 = dump[Dr5DumpConstants.GASLIST_NEW + id] & 0xff;
        int he = dump[Dr5DumpConstants.GASLIST_NEW + id + 12] & 0xff;
        int o2 = 100 - n2 - he;
        return new Mix(o2, he);
    }
    
    private void writeMix(int id, Mix m) {
        int he = m.getHelium();
        int o2 = m.getOxygen();
        int n2 = 100 - he - o2;
        dump[Dr5DumpConstants.GASLIST_NEW + id] = (byte)n2;
        dump[Dr5DumpConstants.GASLIST_NEW + id + 12] = (byte)he;
    }

}
