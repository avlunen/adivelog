/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLog.java
 * 
 * @author Kasra F (Shearwater Research Inc) <kfaghihi@shearwaterresearch.com>
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
package net.sf.jdivelog.ci.sri.format;

import java.util.List;

/**
 * A predator dive log.
 * 
 * @author Kasra F.
 */
public class DiveLog {

	private DiveLogHeader diveLogHeader;
	private DiveLogFooter diveLogFooter;
	private List<DiveLogRecord> diveLogRecords;   // 1 entry every 10 seconds

	public DiveLogHeader getDiveLogHeader() {
		return diveLogHeader;
	}

	public void setDiveLogHeader(DiveLogHeader diveLogHeader) {
		this.diveLogHeader = diveLogHeader;
	}

	public DiveLogFooter getDiveLogFooter() {
		return diveLogFooter;
	}

	public void setDiveLogFooter(DiveLogFooter diveLogFooter) {
		this.diveLogFooter = diveLogFooter;
	}

	public List<DiveLogRecord> getDiveLogRecords() {
		return diveLogRecords;
	}

	public void setDiveLogRecords(List<DiveLogRecord> diveLogRecords) {
		this.diveLogRecords = diveLogRecords;
	}

}
