/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Dr5SettingsOxygen.java
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

import net.sf.jdivelog.util.Hexadecimal;


public class Dr5SettingsOxygenDump {
    
    private byte[] dump;
    
    public Dr5SettingsOxygenDump() {
    }
    
    public Dr5SettingsOxygenDump(String str) {
        setDumpString(str);
    }
    
    public byte[] getDump() {
        return dump;
    }
    
    public void setDump(byte[] dump) {
        this.dump = dump;
    }
    
    public void setDumpString(String str) {
        dump = Hexadecimal.parseSeq(str);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Dr5SettingsOxygenDump>");
        sb.append(Hexadecimal.valueOf(dump));
        sb.append("</Dr5SettingsOxygenDump>");
        return sb.toString();
    };

}
