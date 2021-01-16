/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: RequestTransferExitResponse.java
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

/**
 * A response for {@link RequestTransferExitMessage}.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
class RequestTransferExitResponse implements ResponseDatagram {

	private static final int SERVICE_ID = 0x77;
	private byte[] paramRecords;

	public RequestTransferExitResponse(byte[] paramRecords) {
		this.paramRecords = paramRecords;
	}

	public byte[] getParamRecords() {
		return paramRecords;
	}

	@Override
	public byte[] getDatagram() {
		int dataGramLength = 1 + (paramRecords != null ? paramRecords.length
				: 0);
		byte[] dataGram = new byte[dataGramLength];

		dataGram[0] = SERVICE_ID;

		if (paramRecords != null) {
			System.arraycopy(paramRecords, 0, dataGram, 1, dataGramLength);
		}

		return dataGram;
	}

	public static RequestTransferExitResponse Parse(byte[] dataGram)
			throws IOException {
		if (dataGram[0] == SERVICE_ID) {
			byte[] paramRecords = new byte[dataGram.length - 1];
			System.arraycopy(dataGram, 1, paramRecords, 0, paramRecords.length);

			return new RequestTransferExitResponse(paramRecords);
		}
        throw new IOException("failed to parse");
	}
}
