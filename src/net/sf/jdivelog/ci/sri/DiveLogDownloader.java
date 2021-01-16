/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLogDownloader.java
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
package net.sf.jdivelog.ci.sri;

import java.util.List;

import net.sf.jdivelog.ci.sri.comm.PredatorCommunicator;
import net.sf.jdivelog.ci.sri.format.DiveLog;
import net.sf.jdivelog.ci.sri.format.DiveLogParser;
import net.sf.jdivelog.ci.sri.format.FinalLog;

/**
 * Example code that gets the serial number and a memory dump of predator. This
 * is known to work with with Predator firmwares version 37 to 42.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 * 
 */
public class DiveLogDownloader {

	private DiveLogDownloader() {
		// do nothing
	}

	public static void main(String[] args) throws Throwable {
		PredatorCommunicator pc = null;

		try {
			pc = new PredatorCommunicator();
			pc.connect();

			byte[] memDump = pc.getMemoryDump();

			DiveLogParser dlParser = new DiveLogParser();
			List<DiveLog> diveLogs = dlParser.getDiveLogs(memDump);
			FinalLog finalLog = dlParser.getFinalLog(memDump);

			System.out.println(diveLogs);
			System.out.println(finalLog);
		} finally {
			if (pc != null) {
				pc.close();
			}
		}
	}
}
