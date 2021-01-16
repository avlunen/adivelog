/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLogUtil.java
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

import net.sf.jdivelog.ci.sri.helper.Buffer;

/**
 * Utility class to extract specific info out of the dive logs.
 * 
 * @author Kasra F.
 */
public final class DiveLogUtil {

	private static final int TOTAL_LOG_DATA_LENGTH = 131200;
	private static final int FINAL_RECORD_LENGTH = 128;
	private static final int INTERNAL_LENGTH = 2560;
	private static final int FINAL_RECORD_ID = 0xFFFD;
	private static final int INTERNAL_ID = 0xFFFC;

	private DiveLogUtil() {
		// no inst
	}

	public static int getLogVersion(byte[] logData) {
		int fro = TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH;

		Buffer buf = new Buffer(logData, fro, FINAL_RECORD_LENGTH);

		if (buf.readBeInt16() == FINAL_RECORD_ID) {
			buf.skip(10);
			int logVersion = buf.readByte();

			if (logVersion == 0x3) {
				int ieo = TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH
						- INTERNAL_LENGTH;

				buf = new Buffer(logData, ieo, INTERNAL_LENGTH);
				if (buf.readBeInt16() == INTERNAL_ID) {
					logVersion = 0x4;
				}
			}

			return logVersion;
		}

		throw new IllegalStateException("should never happen");
	}

	public static int getProduct(byte[] logData) {
		int fro = TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH;

		Buffer buf = new Buffer(logData, fro, FINAL_RECORD_LENGTH);

		if (buf.readBeInt16() == FINAL_RECORD_ID) {
			buf.skip(8);
			int softwareVersion = buf.readByte();
			buf.skip(2);
			int product = buf.readByte();
			int logVersion = getLogVersion(logData);

			if (product > 0) {
				return product;
			}
            if (logVersion >= 0x4) {
            	return 0x2;
            } else if (softwareVersion >= 0x40 && softwareVersion <= 0x69) {
            	return 0x1;
            } else if (softwareVersion >= 0 && softwareVersion <= 0x19) {
            	return 0;
            } else {
            	return -1;
            }
		}
        return -1;
	}
}
