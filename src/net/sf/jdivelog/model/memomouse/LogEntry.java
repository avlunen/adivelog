/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: LogEntry.java
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
package net.sf.jdivelog.model.memomouse;

import net.sf.jdivelog.model.aladin.DepthProfile;

/**
 * Description: container for one log entry of a MemoMouse log
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 548 $
 */
public class LogEntry extends net.sf.jdivelog.model.aladin.LogEntry {

    private DepthProfile depthProfile = null;

    public LogEntry(int[] bytes, int aladinType, int timeAdjustment) {
        super(bytes, aladinType, timeAdjustment);
    }

    public void setDepthProfile(DepthProfile depthProfile) {
        this.depthProfile = depthProfile;
    }

    public DepthProfile getDepthProfile() {
        return depthProfile;
    }

    public String toString() {
        return super.toString() + "\nprofile: " + depthProfile;
    }
}
