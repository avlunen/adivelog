/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: LogBook.java
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

import java.util.LinkedList;
import java.util.List;

/**
 * Description: container for the logbook of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.3 $
 */
public class LogBook {
	public final long serialNumber;

	public final int maxDepth;

	public final int totalDiveTime;

	public final int totalNumberOfDives;

	public final int numberOfDivesInBuffer;

	private final List<LogEntry> logEntries = new LinkedList<LogEntry>();

	public LogBook(MemoryHeader memoryHeader) {
		serialNumber = memoryHeader.serialNumber;
		maxDepth = memoryHeader.maxDepth;
		totalDiveTime = memoryHeader.totalDiveTime;
		totalNumberOfDives = memoryHeader.totalNumberOfDives;
		numberOfDivesInBuffer = memoryHeader.numberOfDivesInBuffer;
	}

	public void addLogEntry(LogEntry logEntry) {
		logEntries.add(logEntry);
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}
}
