/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: RequestUploadMessage.java
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

import net.sf.jdivelog.ci.sri.helper.NumberUtil;

/**
 * A message to initiate a dump of the Predator's log memory.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
class RequestUploadMessage implements MessageDatagram {

	private static int SERVICE_MESSAGE_ID = 0x35;
	private int dataFormat;
	private int memoryAddress;
	private int memorySize;

	public RequestUploadMessage(int dataFormat, int memoryAddress,
			int memorySize) {
		this.dataFormat = dataFormat;
		this.memoryAddress = memoryAddress;
		this.memorySize = memorySize;
	}

	public int getDataFormat() {
		return dataFormat;
	}

	public int getMemoryAddress() {
		return memoryAddress;
	}

	public int getMemorySize() {
		return memorySize;
	}

	@Override
	public byte[] getDatagram() {
		int memoryAddressByteLength = NumberUtil.byteLength(memoryAddress);
		int memorySizeByteLength = NumberUtil.byteLength(memorySize);

		byte[] dataGram = new byte[3 + memoryAddressByteLength
				+ memorySizeByteLength];
		dataGram[0] = (byte) SERVICE_MESSAGE_ID;
		dataGram[1] = (byte) dataFormat;
		dataGram[2] = (byte) memoryAddressByteLength;
		dataGram[2] |= (byte) (memorySizeByteLength << 4);

		if (memoryAddress != 0) {
			byte[] memoryAddressBytes = NumberUtil.toByte(memoryAddress);
			System.arraycopy(memoryAddressBytes, memoryAddressBytes.length
					- memoryAddressByteLength, dataGram, 3,
					memoryAddressByteLength);
		}

		if (memorySize != 0) {
			byte[] memorySizeBytes = NumberUtil.toByte(memorySize);
			System.arraycopy(memorySizeBytes, memorySizeBytes.length
					- memorySizeByteLength, dataGram,
					3 + memoryAddressByteLength, memorySizeByteLength);
		}

		return dataGram;
	}

	// return null on malformed RequestUploadMessage
	public static RequestUploadMessage Parse(byte[] dataGram) {
		// RequestUploadMessage datagram must include at least the 3 bytes
		if (dataGram == null || dataGram.length < 3) {
			return null;
		}

		int dataFormat = dataGram[1];
		int memoryAddressByteLength = dataGram[2] & 0xF;
		int memorySizeByteLength = dataGram[2] >> 4;
		int memoryAddress = 0;
		int memorySize = 0;

		if (memoryAddressByteLength != 0) {
			memoryAddress = NumberUtil.toInt(dataGram, 3,
					memoryAddressByteLength);
		}

		if (memorySizeByteLength != 0) {
			memorySize = NumberUtil.toInt(dataGram,
					3 + memoryAddressByteLength, memorySizeByteLength);
		}

		return new RequestUploadMessage(dataFormat, memoryAddress, memorySize);
	}
}
