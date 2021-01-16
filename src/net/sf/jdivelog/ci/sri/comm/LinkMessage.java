/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: LinkMessage.java
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
 * A message that encapsulates another message. Required by the underlying protocol.
 * @author Kasra F (Shearwater Research Inc.)
 */
final class LinkMessage implements Datagram {

    public static final int UDS = 0x00;
    private int sourceAddress;
    private int targetAddress;
    private int messageType;
    private byte[] data;

    LinkMessage(int sourceAddress, int targetAddress, int messageType,
            byte[] data) {
        this.sourceAddress = sourceAddress;
        this.targetAddress = targetAddress;
        this.messageType = messageType;
        this.data = data;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public int getTargetAddress() {
        return targetAddress;
    }

    public int getMessageType() {
        return messageType;
    }

    public byte[] getData() {
        return data;
    }

    public int getServiceMessageId() {
        return data[0] & 0xFF;
    }

    @Override
    public byte[] getDatagram() {
        byte[] dataGram = new byte[4 + data.length];
        dataGram[0] = (byte) sourceAddress;
        dataGram[1] = (byte) targetAddress;
        dataGram[2] = (byte) (data.length + 1); // include the messageType byte in the data length
        dataGram[3] = (byte) messageType;

        System.arraycopy(data, 0, dataGram, 4, data.length);

        return dataGram;
    }

    // return null on malformed DC Link message
    public static LinkMessage Parse(byte[] dataGram) throws IOException {
        // DC Link datagram must include at least the 4 header bytes
        if (dataGram == null || dataGram.length < 4) {
            throw new IOException("failed to parse");
        }

        int sourceAddress = dataGram[0];
        int targetAddress = dataGram[1];
        int dataLength = dataGram[2] & 0xFF;
        int messageType = dataGram[3];

        // check dataLength at least includes the messageType byte length
        // check dataLength is no longer than dataGram without headers including messageType
        if (dataLength < 1 || dataLength > dataGram.length - 3) {
            throw new IOException("failed to parse");
        }

        // don't include the messageType in the copied data
        byte[] data = new byte[dataLength - 1];
        System.arraycopy(dataGram, 4, data, 0, data.length);

        return new LinkMessage(sourceAddress, targetAddress, messageType,
                data);
    }
}
