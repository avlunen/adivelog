/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: RequestTransferExitMessage.java
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
 * A message to exit the transfer state.
 * @author Kasra F (Shearwater Research Inc.)
 */
class RequestTransferExitMessage implements MessageDatagram {

    private static final int SERVICE_ID = 0x37;
    private byte[] paramRecords;

    public RequestTransferExitMessage(byte[] paramRecords) {
        this.paramRecords = paramRecords;
    }

    public byte[] getParamRecords() {
        return paramRecords;
    }

    @Override
    public byte[] getDatagram() {
        int dataGramLength = 1 + (paramRecords != null
                ? paramRecords.length : 0);
        byte[] dataGram = new byte[dataGramLength];

        dataGram[0] = SERVICE_ID;

        if (paramRecords != null) {
            System.arraycopy(paramRecords, 0, dataGram, 1, dataGramLength);
        }

        return dataGram;
    }

    public static RequestTransferExitMessage Parse(byte[] dataGram) {
        byte[] paramRecords = new byte[dataGram.length - 1];
        System.arraycopy(dataGram, 1, paramRecords, 0, paramRecords.length);

        return new RequestTransferExitMessage(paramRecords);
    }
}
