/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: FifteenBitCustomFunction.java
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

/**
 * 15-bit Value for Custom Function
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 */
public class FifteenBitCustomFunction implements OSTCValue {
    
    private final int defaultValue;
    private final int currentValue;
    
    public FifteenBitCustomFunction(int defaultValue, int currentValue) {
        if (!isFifteenBitValue(defaultValue)) {
            throw new IllegalArgumentException("defaultValue is not a 15-bit Value '"+defaultValue+"'");
        }
        if (!isFifteenBitValue(currentValue)) {
            throw new IllegalArgumentException("currentValue is not a 15-bit Value '"+currentValue+"'");
        }
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
    }
    
    
    public FifteenBitCustomFunction(int defaultValue, int currentValue, int defaultSettings) {
        if (!isFifteenBitValue(defaultValue)) {
            defaultValue = defaultSettings;
        }
        if (!isFifteenBitValue(currentValue)) {
            currentValue = defaultSettings;
        }
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
    }

    public int defaultValue() {
        return defaultValue;
    }
    
    public int currentValue() {
        return currentValue;
    }
    
    public static boolean isFifteenBitValue(int value) {
        return (value & 0x8000) == 0;
    }
}
