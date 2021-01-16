/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: TransferDataMessage.java
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

/**
 * A message to grab a block of the log memory on the Predator.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
public class TransferDataMessage implements MessageDatagram {

	private static final int SERVICE_MESSAGE_ID = 0x36;
	private int blockCounter;
	private byte[] data;

	TransferDataMessage(int blockCounter, byte[] data) {
		this.blockCounter = blockCounter;
		this.data = data;
	}

	public int getBlockCounter() {
		return blockCounter;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public byte[] getDatagram() {
		int dataLength = data == null ? 0 : data.length;
		byte[] dataGram = new byte[2 + dataLength];

		dataGram[0] = SERVICE_MESSAGE_ID;
		dataGram[1] = (byte) blockCounter;

		if (dataLength > 0) {
			System.arraycopy(data, 0, dataGram, 2, data.length);
		}

		return dataGram;
	}

	public static TransferDataMessage Parse(byte[] dataGram) {
		int blockCounter = dataGram[1];
		byte[] data = new byte[dataGram.length - 2];

		System.arraycopy(dataGram, 2, data, 0, data.length);

		return new TransferDataMessage(blockCounter, data);
	}
}
