/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: ReadDataByIdentifierMessage.java
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
 * A message to get an identifier from the Predator (e.g. the serial number).
 * @author Kasra F (Shearwater Research Inc.)
 */
final class ReadDataByIdentifierMessage implements MessageDatagram {

    private static final int SERVICE_MESSAGE_ID = 0x22;
    private int dataIdentifier; // each data identifier is 2 bytes long, ie 0xFFFF

    public ReadDataByIdentifierMessage(int dataIdentifier) {
        this.dataIdentifier = dataIdentifier;
    }

    public int getDataIdentifier() {
        return dataIdentifier;
    }

    @Override
    public byte[] getDatagram() {
        byte[] dataIdentifierBytes = NumberUtil.toByte(dataIdentifier);

        byte[] dataGram = new byte[3];
        dataGram[0] = (byte) SERVICE_MESSAGE_ID;
        dataGram[1] = dataIdentifierBytes[2];
        dataGram[2] = dataIdentifierBytes[3];

        return dataGram;
    }

    public static ReadDataByIdentifierMessage Parse(byte[] dataGram) throws IOException {
        if (dataGram != null && dataGram[0] == SERVICE_MESSAGE_ID && dataGram.length >= 3) {
            int dataIdentifier;
            dataIdentifier = dataGram[2];
            dataIdentifier |= dataGram[1] << 8;

            return new ReadDataByIdentifierMessage(dataIdentifier);
        }
        throw new IOException();
    }
}
