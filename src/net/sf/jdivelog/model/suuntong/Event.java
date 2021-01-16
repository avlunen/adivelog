/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: Event.java
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

import java.util.Collection;
import java.util.LinkedList;

import net.sf.jdivelog.util.DiveParser;

/**
 * Description: container for a dive event of a Suunto D6
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.8 $
 */
public class Event {
	private Collection<AlarmType> alarms = new LinkedList<AlarmType>();

	private int offset = 0;

	private int currentSampleMarker = 0;

	private EventType eventType = null;

	private int nextSampleOffset = 0;

	public Event(byte[] bytes, int offset, int currentSampleNumber) {
		if ((bytes == null) || (offset >= bytes.length)) {
			throw new IllegalArgumentException("parameter \"bytes\" too short");
		}
		this.offset = offset;
		while (this.offset < bytes.length - 1) {
			eventType = EventType.valueOf(DiveParser.readUShort(bytes,
					this.offset++));
			if (eventType == EventType.NEXT_EVENT_MARKER) {
				int currentSampleMarker = DiveParser.readUInt(bytes,
						this.offset);

				this.offset += 2;

				int nextSampleOffset = DiveParser.readUInt(bytes, this.offset);

				this.offset += 2;
				if (currentSampleMarker == currentSampleNumber) {
					this.currentSampleMarker = currentSampleMarker;
					this.nextSampleOffset = nextSampleOffset;
					break;
				}
			} else if (eventType == EventType.SURFACED) {
				this.offset += 2;
			} else if (eventType == EventType.EVENT) {
				alarms.add(AlarmType.valueOf((byte) DiveParser.readUShort(
						bytes, this.offset++)));
				this.offset++;
			} else if (eventType == EventType.BOOKMARK) {
				this.offset += 4;
			}
		}
	}

	public Collection<AlarmType> getAlarms() {
		return alarms;
	}

	public int getCurrentSampleMarker() {
		return currentSampleMarker;
	}

	public EventType getEventType() {
		return eventType;
	}

	public int getNextSampleOffset() {
		return nextSampleOffset;
	}

	public int getOffset() {
		return offset;
	}
}
