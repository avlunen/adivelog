/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: NegativeResponse.java
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
 * A response that indicates something went wrong with the last sent message.
 * @author Kasra F (Shearwater Research Inc.)
 */
class NegativeResponse implements ResponseDatagram {

    private static final int SERVICE_MESSAGE_ID = 0x7F;
    private int requestServiceId;
    private int responseCode;

    public NegativeResponse(int requestServiceId, int responseCode) {
        this.requestServiceId = requestServiceId;
        this.responseCode = responseCode;
    }

    public int getRequestServiceId() {
        return requestServiceId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public byte[] getDatagram() {
        byte[] dataGram = new byte[3];

        dataGram[0] = SERVICE_MESSAGE_ID;
        dataGram[1] = (byte) requestServiceId;
        dataGram[2] = (byte) responseCode;

        return dataGram;
    }

    // returns null on invalid format
    public static NegativeResponse Parse(byte[] dataGram) throws IOException {
        if (dataGram != null && dataGram.length == 3 && dataGram[0]
                == SERVICE_MESSAGE_ID) {
            int requestServiceId = dataGram[1];
            int responseCode = dataGram[2];

            return new NegativeResponse(requestServiceId, responseCode);
        }
        throw new IOException("failed to parse");
    }
}
