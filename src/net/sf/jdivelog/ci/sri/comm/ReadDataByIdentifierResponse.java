/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: ReadDataByIdentifierResponse.java
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
package net.sf.jdivelog.ci.sri.comm;

import java.io.IOException;

import net.sf.jdivelog.ci.sri.helper.NumberUtil;

/**
 * A response that contains the identifier requested by
 * {@link ReadDataByIdentifierResponse}.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
final class ReadDataByIdentifierResponse implements ResponseDatagram {

	private static final int SERVICE_MESSAGE_ID = 0x62;
	private int dataIdentifier; // each data identifier is 2 bytes long, ie
								// 0xFFFF
	private byte[] dataRecords;

	ReadDataByIdentifierResponse(int dataIdentifier, byte[] dataRecords) {
		this.dataIdentifier = dataIdentifier;
		this.dataRecords = dataRecords;
	}

	public int getDataIdentifier() {
		return dataIdentifier;
	}

	public byte[] getDataRecords() {
		return dataRecords;
	}

	@Override
	public byte[] getDatagram() {
		byte[] dataIdentifierBytes = NumberUtil.toByte(dataIdentifier);

		byte[] dataGram = new byte[3 + dataRecords.length];
		dataGram[0] = (byte) SERVICE_MESSAGE_ID;
		dataGram[1] = dataIdentifierBytes[2];
		dataGram[2] = dataIdentifierBytes[3];

		System.arraycopy(dataRecords, 0, dataGram, 4, dataRecords.length);

		return dataGram;
	}

	// returns null on invalid format
	public static ReadDataByIdentifierResponse Parse(byte[] dataGram)
			throws IOException {
		if (dataGram != null && dataGram[0] == SERVICE_MESSAGE_ID
				&& dataGram.length >= 4) {
			int dataIdentifier;
			dataIdentifier = dataGram[2];
			dataIdentifier |= dataGram[1] << 8;

			byte[] dataRecords = new byte[dataGram.length - 3];
			System.arraycopy(dataGram, 3, dataRecords, 0, dataRecords.length);

			return new ReadDataByIdentifierResponse(dataIdentifier, dataRecords);
		}
        throw new IOException("failed to parse");
	}
}
