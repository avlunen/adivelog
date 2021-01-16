/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: RequestUploadResponse.java
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
 * A response for {@link RequestUploadMessage}.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
public final class RequestUploadResponse implements ResponseDatagram {

	private static final int SERVICE_MESSAGE_ID = 0x75;
	private int maxBlockLength;

	RequestUploadResponse(int maxBlockLength) {
		this.maxBlockLength = maxBlockLength;
	}

	public int getMaxBlockLength() {
		return maxBlockLength;
	}

	@Override
	public byte[] getDatagram() {
		int maxBlockLengthByteLength = NumberUtil.byteLength(maxBlockLength);

		byte[] dataGram = new byte[2 + maxBlockLengthByteLength];
		dataGram[0] = (byte) SERVICE_MESSAGE_ID;
		dataGram[1] |= (byte) (maxBlockLengthByteLength << 4);

		byte[] maxBlockLengthBytes = NumberUtil.toByte(maxBlockLength);
		System.arraycopy(maxBlockLengthBytes, maxBlockLengthBytes.length
				- maxBlockLengthByteLength, dataGram, 2,
				maxBlockLengthByteLength);

		return dataGram;
	}

	// returns null on invalid format
	public static RequestUploadResponse Parse(byte[] dataGram)
			throws IOException {
		if (dataGram != null && dataGram[0] == SERVICE_MESSAGE_ID
				&& dataGram.length >= 3) {
			int maxBlockLengthByteLength = dataGram[1] >> 4;
			int maxBlockLength = 0;

			if (maxBlockLengthByteLength != 0) {
				maxBlockLength = NumberUtil.toInt(dataGram, 2,
						maxBlockLengthByteLength);
			}

			return new RequestUploadResponse(maxBlockLength);
		}
        throw new IOException("failed to parse");
	}
}
