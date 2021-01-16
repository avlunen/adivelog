/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: MemoryHeader.java
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

import net.sf.jdivelog.util.DiveParser;

/**
 * Description: container for the memory header of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.3 $
 */
public class MemoryHeader {
	public static final int SIZE = 0x19A;

	public final long serialNumber;

	public final int maxDepth;

	public final int totalDiveTime;

	public final int totalNumberOfDives;

	public final int lastDiveInBuffer;

	public final int numberOfDivesInBuffer;

	public final int oldestDiveInBuffer;

	public MemoryHeader(byte[] bytes) {
		if ((bytes == null) || (bytes.length < SIZE)) {
			throw new IllegalArgumentException("parameter \"bytes\" too short");
		}
		serialNumber = DiveParser.readULong(bytes, 0x23);
		maxDepth = DiveParser.readUInt(bytes, 0x186);
		totalDiveTime = DiveParser.readUInt(bytes, 0x188);
		totalNumberOfDives = DiveParser.readUInt(bytes, 0x18A);
		lastDiveInBuffer = DiveParser.readUInt(bytes, 0x190);
		numberOfDivesInBuffer = DiveParser.readUInt(bytes, 0x192);
		oldestDiveInBuffer = DiveParser.readUInt(bytes, 0x196);
	}
}
