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
package net.sf.jdivelog.model.suuntong;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: container for one logbook entry of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.3 $
 */
public class LogEntry {

	public enum GasType {
		AIR(0), NITROX(1), GAUGE(2);
		private final int id;

		private GasType(int id) {
			this.id = id;
		}
		
		public static GasType fromValue(int id) {
			GasType gasType = null;
			for (GasType type : GasType.values()) {
				if (type.id == id) {
					gasType = type;
					break;
				}
			}
			return gasType;
		}
	}

	public final int maxDepth;

	public final int diveTime;

	public final Date date;
	
	public final GasType gasType;
	
    public final int o2percent1;
    public final int o2percent2;
    public final int o2percent3;

	private List<DepthProfileEntry> profile = new LinkedList<DepthProfileEntry>();

	public LogEntry(DiveProfileHeader diveProfileHeader) {
		maxDepth = diveProfileHeader.maxDepth;
		diveTime = diveProfileHeader.diveTime;
		gasType = GasType.fromValue(diveProfileHeader.gasModel);
		o2percent1 = diveProfileHeader.o2percent1;
		o2percent2 = diveProfileHeader.o2percent2;
		o2percent3 = diveProfileHeader.o2percent3;

		Calendar date = Calendar.getInstance();

		date.set(Calendar.YEAR, diveProfileHeader.year);
		date.set(Calendar.MONTH, diveProfileHeader.month);
		date.set(Calendar.DAY_OF_MONTH, diveProfileHeader.day);
		date.set(Calendar.HOUR_OF_DAY, diveProfileHeader.hour);
		date.set(Calendar.MINUTE, diveProfileHeader.minute);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		this.date = date.getTime();
	}

	public List<DepthProfileEntry> getProfile() {
		return profile;
	}

	public void setProfile(List<DepthProfileEntry> profile) {
		this.profile = profile;
	}
}
