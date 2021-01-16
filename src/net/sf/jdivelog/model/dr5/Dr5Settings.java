/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5Settings.java
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

public class Dr5Settings {

    public enum Dr5DiveView {
        None(0), Timer_MaxDepth(1), Oxygen(2), Snake(3), Decolist(4), Temp(5);

        private int val;

        Dr5DiveView(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public static Dr5DiveView fromVal(int val) {
            for (Dr5DiveView v : values()) {
                if (val == v.getVal()) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + val);
        }
    }

    public enum DiveMode {
        OC(0), CCR(1);

        private byte val;

        DiveMode(int val) {
            this.val = (byte) val;
        }

        public byte getVal() {
            return val;
        }

        public static DiveMode fromVal(byte b) {
            for (DiveMode m : values()) {
                if (b == m.getVal()) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + b);
        }
    }

    private boolean flipScreen;
    private boolean flipScreenNextStart;
    private int brightMin;
    private int brightMax;
    private Dr5DiveView defaultView;
    private Dr5DiveView defaultCcrView;
    private int defaultViewFallback;
    private DiveMode mode;
    private Mix[] mixes = new Mix[12];

    private Dr5SettingsBuehlmann buehlmann;
    private Dr5SettingsOxygenDump oxygenDump;

    public boolean isFlipScreen() {
        return flipScreen;
    }

    public void setFlipScreen(boolean flipScreen) {
        this.flipScreen = flipScreen;
    }

    public boolean isFlipScreenNextStart() {
        return flipScreenNextStart;
    }

    public void setFlipScreenNextStart(boolean flipScreenNextStart) {
        this.flipScreenNextStart = flipScreenNextStart;
    }

    public int getBrightMin() {
        return brightMin;
    }

    public void setBrightMin(int brightMin) {
        this.brightMin = brightMin;
    }

    public int getBrightMax() {
        return brightMax;
    }

    public void setBrightMax(int brightMax) {
        this.brightMax = brightMax;
    }

    public Dr5DiveView getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(Dr5DiveView defaultView) {
        this.defaultView = defaultView;
    }

    public void setDefaultViewString(String val) {
        Dr5DiveView defaultView = val == null ? null : Dr5DiveView.valueOf(val);
        setDefaultView(defaultView);
    }

    public Dr5DiveView getDefaultCcrView() {
        return defaultCcrView;
    }

    public void setDefaultCcrView(Dr5DiveView defaultCcrView) {
        this.defaultCcrView = defaultCcrView;
    }

    public void setDefaultCcrViewString(String val) {
        setDefaultCcrView(val == null ? null : Dr5DiveView.valueOf(val));
    }

    public int getDefaultViewFallback() {
        return defaultViewFallback;
    }

    public void setDefaultViewFallback(int defaultViewFallback) {
        this.defaultViewFallback = defaultViewFallback;
    }

    public DiveMode getMode() {
        return mode;
    }

    public void setMode(DiveMode mode) {
        this.mode = mode;
    }

    public void setModeString(String val) {
        setMode(val == null || "null".equals(val) ? null : DiveMode.valueOf(val));
    }

    public Mix[] getMixes() {
        return mixes;
    }

    public void setMixes(Mix[] mixes) {
        this.mixes = mixes;
    }

    public Dr5SettingsBuehlmann getBuehlmann() {
        return buehlmann;
    }

    public void setBuehlmann(Dr5SettingsBuehlmann buehlmann) {
        this.buehlmann = buehlmann;
    }

    public Dr5SettingsOxygenDump getOxygenDump() {
        return oxygenDump;
    }

    public void setOxygenDump(Dr5SettingsOxygenDump oxygenDump) {
        this.oxygenDump = oxygenDump;
    }
    
    public void setOxygenDumpString(String str) {
        setOxygenDump(new Dr5SettingsOxygenDump(str));
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Dr5Settings>");
        sb.append("<flipScreen>");
        sb.append(flipScreen);
        sb.append("</flipScreen>");
        sb.append("<flipScreenNextStart>");
        sb.append(flipScreenNextStart);
        sb.append("</flipScreenNextStart>");
        sb.append("<brightMin>");
        sb.append(brightMin);
        sb.append("</brightMin>");
        sb.append("<brightMax>");
        sb.append(brightMax);
        sb.append("</brightMax>");
        if (defaultView != null) {
            sb.append("<defaultView>");
            sb.append(defaultView);
            sb.append("</defaultView>");
        }
        if (defaultCcrView != null) {
            sb.append("<defaultCcrView>");
            sb.append(defaultCcrView);
            sb.append("</defaultCcrView>");
        }
        sb.append("<defaultViewFallback>");
        sb.append(defaultViewFallback);
        sb.append("</defaultViewFallback>");
        if (mode != null) {
            sb.append("<mode>");
            sb.append(mode);
            sb.append("</mode>");
        }
        sb.append(mixesToString());
        sb.append("\n");
        if (buehlmann != null) {
            sb.append(buehlmann);
            sb.append("\n");
        }
        if (oxygenDump != null) {
            sb.append(oxygenDump);
            sb.append("\n");
        }
        sb.append("</Dr5Settings>");
        return sb.toString();
    }

    private String mixesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<mixes>");
        if (mixes != null) {
            for (Mix mix : mixes) {
                sb.append(mix);
            }
        }
        sb.append("</mixes>");
        return sb.toString();
    }

    public void addMix(String name, int o2, int he, double ppO2, int mod, double change) {
        Mix m = new Mix(name, o2, he, ppO2, mod, change);
        if (mixes == null) {
            mixes = new Mix[1];
        }
        for (int i = 0; i < mixes.length; i++) {
            if (mixes[i] == null) {
                mixes[i] = m;
                return;
            }
        }
        Mix[] oldmixes = mixes;
        mixes = new Mix[oldmixes.length];
        System.arraycopy(oldmixes, 0, mixes, 0, oldmixes.length);
        mixes[oldmixes.length] = m;
    }
}
