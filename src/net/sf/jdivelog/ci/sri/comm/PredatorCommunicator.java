/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File:
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.intel.bluetooth.RemoteDeviceHelper;

/**
 * Connects and provides high-level methods to communicate with the Predator.
 * This class scans for devices with the name "Predator". It connects to the
 * first one it finds via the bluetooth SPP profile and pairing code 0000. Note
 * that on some operating systems (linux?), the user will be prompted manually
 * for the pairing code and will have to enter it in.
 * 
 * Messages and responses are wrapped internally using SLIP (RFC 1055). See the
 * methods {@link #encodeSlip(byte[])} and {@link #decodeSlip(byte[])}.
 * 
 * @author Kasra F (Shearwater Research Inc.)
 */
public final class PredatorCommunicator {

	private static final byte END = (byte) (192 & 255);
	private static final byte ESC = (byte) (219 & 255);
	private static final byte ESC_END = (byte) (220 & 255);
	private static final byte ESC_ESC = (byte) (221 & 255);

	private StreamConnection streamConnection;
	private InputStream inputStream;
	private OutputStream outputStream;

	public void connect() throws InterruptedException, IOException {

		if (streamConnection != null) {
			throw new IllegalStateException();
		}

		final List<RemoteDevice> devicesDiscovered = new ArrayList<RemoteDevice>();
		final Holder<Boolean> deviceDiscoverySuccess = new Holder<Boolean>(
				false);
		final Object deviceDiscoveryCompletedEvent = new Object();

		final List<String> servicesDiscovered = new ArrayList<String>();
		final Holder<Boolean> serviceDiscoverySuccess = new Holder<Boolean>(
				false);
		final Object serviceDiscoveryCompletedEvent = new Object();

		DiscoveryListener discoveryListener = new DiscoveryListener() {

			@Override
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				try {
					String name = btDevice.getFriendlyName(true);
					if ("Predator".equalsIgnoreCase(name) || "Petrel".equalsIgnoreCase(name)) {
						devicesDiscovered.add(btDevice);
					}
				} catch (IOException e) {
					// do nothing
				}
			}

			@Override
			public void inquiryCompleted(int discType) {
				switch (discType) {
				case INQUIRY_TERMINATED:
				case INQUIRY_ERROR:
					deviceDiscoverySuccess.obj = false;
					break;
				case INQUIRY_COMPLETED:
					deviceDiscoverySuccess.obj = true;
					break;

				}

				synchronized (deviceDiscoveryCompletedEvent) {
					deviceDiscoveryCompletedEvent.notifyAll();
				}
			}

			@Override
			public void servicesDiscovered(int transID,
					ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
					String url = servRecord[i].getConnectionURL(
							ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
					if (url == null) {
						continue;
					}
					servicesDiscovered.add(url);
				}
			}

			@Override
			public void serviceSearchCompleted(int transID, int respCode) {
				switch (respCode) {
				case SERVICE_SEARCH_TERMINATED:
				case SERVICE_SEARCH_ERROR:
				case SERVICE_SEARCH_NO_RECORDS:
				case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
					serviceDiscoverySuccess.obj = false;
					break;
				case SERVICE_SEARCH_COMPLETED:
					serviceDiscoverySuccess.obj = true;
					break;
				}

				synchronized (serviceDiscoveryCompletedEvent) {
					serviceDiscoveryCompletedEvent.notifyAll();
				}
			}
		};

		DiscoveryAgent discoveryAgent;
		try {
			discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		} catch (BluetoothStateException bse) {
			throw new IllegalStateException(bse);
		}

		synchronized (deviceDiscoveryCompletedEvent) {
			try {
				boolean started = discoveryAgent.startInquiry(
						DiscoveryAgent.GIAC, discoveryListener);
				if (started) {
					deviceDiscoveryCompletedEvent.wait();
				}

				if (!deviceDiscoverySuccess.obj) {
					throw new IOException("device discovery failed");
				}
			} catch (BluetoothStateException bse) {
				throw new IOException(bse);
			}
		}

		Thread.sleep(5000L); // this pause is required, otherwise it won't work

		String url = null;
		RemoteDevice rd = null;
		synchronized (serviceDiscoveryCompletedEvent) {
			for (RemoteDevice btDevice : devicesDiscovered) {
				try {
					discoveryAgent.searchServices(null, new UUID[] { new UUID(
							0x1101) }, btDevice, discoveryListener);
					serviceDiscoveryCompletedEvent.wait();

					if (!serviceDiscoverySuccess.obj) {
						throw new IOException("service discovery failed");
					}

					if (!servicesDiscovered.isEmpty()) {
						url = servicesDiscovered.get(0);
						rd = btDevice;
						break;
					}
				} catch (BluetoothStateException bse) {
					throw new IOException(bse);
				}
			}
		}

		StreamConnection stream = null;
		InputStream is = null;
		OutputStream os = null;

		try {
			RemoteDeviceHelper.authenticate(rd, "0000");
			stream = (StreamConnection) Connector.open(url);
			is = stream.openInputStream();
			os = stream.openOutputStream();
		} finally{
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		streamConnection = stream;
		inputStream = is;
		outputStream = os;
	}

	@SuppressWarnings("unchecked")
	public String getSerialNumber() throws IOException {
		ReadDataByIdentifierMessage msg = new ReadDataByIdentifierMessage(
				DiveCANDataIdentifier.SerialNumber);
		ReadDataByIdentifierResponse resp = (ReadDataByIdentifierResponse) sendMessage(
				DiveCANDeviceID.WIRELESS_EXTERNAL_DEVICE,
				DiveCANDeviceID.GATEWAY, msg,
				ReadDataByIdentifierResponse.class);
		return new String(resp.getDataRecords(), "US-ASCII");
	}

	@SuppressWarnings("unchecked")
	public byte[] getMemoryDump() throws IOException {
		// initialize
		RequestUploadMessage initMsg = new RequestUploadMessage(0, 0xDD000000,
				0x20080);
		sendMessage(
				DiveCANDeviceID.WIRELESS_EXTERNAL_DEVICE,
				DiveCANDeviceID.HANDSET, initMsg, RequestUploadResponse.class);

		// receieve
		ByteArrayOutputStream baos = new ByteArrayOutputStream(0x20080);
		int blockCntr = 0;
		while (baos.size() < 0x20080) {
			blockCntr++;
			TransferDataMessage dataMsg = new TransferDataMessage(
					blockCntr & 0xFF, null);

			TransferDataResponse dataResp = (TransferDataResponse) sendMessage(
					DiveCANDeviceID.WIRELESS_EXTERNAL_DEVICE,
					DiveCANDeviceID.HANDSET, dataMsg,
					TransferDataResponse.class);

			baos.write(dataResp.getData());
		}

		// shutdown
		RequestTransferExitMessage deinitMsg = new RequestTransferExitMessage(
				null);
		sendMessage(
				DiveCANDeviceID.WIRELESS_EXTERNAL_DEVICE,
				DiveCANDeviceID.HANDSET, deinitMsg,
				RequestTransferExitResponse.class);

		return baos.toByteArray();
	}

	private ResponseDatagram sendMessage(int fromDeviceId, int toDeviceId,
			MessageDatagram message,
			Class<? extends ResponseDatagram>... acceptableResponses)
			throws IOException {

		if (streamConnection == null) {
			throw new IllegalStateException();
		}

		// encapsulate message and write
		Datagram encapsulatedMessage = new LinkMessage(fromDeviceId,
				toDeviceId, LinkMessage.UDS, message.getDatagram());
		byte[] outData = encodeSlip(encapsulatedMessage.getDatagram());
		outputStream.write(outData);
		outputStream.flush();

		// read response
		Set<Class<? extends ResponseDatagram>> acceptableResponsesSet = new HashSet<Class<? extends ResponseDatagram>>(
				Arrays.<Class<? extends ResponseDatagram>> asList(acceptableResponses));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[8192];
		int cnt = 0;
		while ((cnt = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, cnt);

			byte[] slipPacket;
			try {
				byte[] encodedMsg = baos.toByteArray();
				slipPacket = decodeSlip(encodedMsg);
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				// likely an incomplete packet
				continue;
			}

			if (slipPacket == null) {
				continue;
			}

			LinkMessage receivedDCLinkMessage = LinkMessage.Parse(slipPacket);

			if (receivedDCLinkMessage == null) {
				throw new IOException("cannot parse response");
			}

			if (receivedDCLinkMessage.getMessageType() != LinkMessage.UDS) {
				throw new IOException("non-UDS response");
			}

			ResponseDatagram response = null;
			switch (receivedDCLinkMessage.getServiceMessageId()) {
			case ServiceMessage.NegativeResponse:
				response = NegativeResponse.Parse(receivedDCLinkMessage
						.getData());
				break;
			case ServiceMessage.ReadDataByIdentifierResponse:
				response = ReadDataByIdentifierResponse
						.Parse(receivedDCLinkMessage.getData());
				break;
			case ServiceMessage.RequestUploadResponse:
				response = RequestUploadResponse.Parse(receivedDCLinkMessage
						.getData());
				break;
			case ServiceMessage.TransferDataResponse:
				response = TransferDataResponse.Parse(receivedDCLinkMessage
						.getData());
				break;
			case ServiceMessage.RequestTransferExitResponse:
				response = RequestTransferExitResponse
						.Parse(receivedDCLinkMessage.getData());
				break;
			default:
				throw new IOException("unsupported response type encountered:"
						+ receivedDCLinkMessage.getServiceMessageId());
			}

			if (!acceptableResponsesSet.isEmpty()
					&& !acceptableResponsesSet.contains(response.getClass())) {
				throw new IOException("unexpected response type encountered:"
						+ response.getClass().getSimpleName());
			}

			return response;
		}

		throw new IOException("non-slip packet encountered");

	}

	public void close() {

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException ioe) {
				// do nothing
			}
			inputStream = null;
		}

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException ioe) {
				// do nothing
			}
			outputStream = null;
		}

		if (streamConnection != null) {
			try {
				streamConnection.close();
			} catch (IOException ioe) {
				// do nothing
			}
			streamConnection = null;
		}
	}

	private static byte[] encodeSlip(byte[] data) {
		byte[] encodedData = new byte[(data.length * 2) + 1];
		int encodedDataIndex = 0;
		for (int i = 0; i < data.length; i++) {
			switch (data[i]) {
			case END:
				encodedData[encodedDataIndex++] = ESC;
				encodedData[encodedDataIndex++] = ESC_END;
				break;
			case ESC:
				encodedData[encodedDataIndex++] = ESC;
				encodedData[encodedDataIndex++] = ESC_ESC;
				break;
			default:
				encodedData[encodedDataIndex++] = data[i];
				break;
			}
		}
		encodedData[encodedDataIndex++] = END;
		byte[] resultData = new byte[encodedDataIndex];
		System.arraycopy(encodedData, 0, resultData, 0, encodedDataIndex);
		return resultData;
	}

	private static byte[] decodeSlip(byte[] data) {
		if (data == null || data.length < 1) {
			return null;
		}
		byte[] decodedData = new byte[data.length];
		int decodedDataIndex = 0;
		for (int i = 0; i < data.length; i++) {
			switch (data[i]) {
			case END:
				if (decodedDataIndex > 0) {
					// resize the decoded data to its exact length
					// resize the decoded data to its exact length
					byte[] resultData = new byte[decodedDataIndex];
					System.arraycopy(decodedData, 0, resultData, 0,
							decodedDataIndex);
					return decodedData;
				}
				break;
			case ESC:
				// check the next byte
				// check the next byte
				i++;
				switch (data[i]) {
				case ESC_END:
					decodedData[decodedDataIndex++] = END;
					break;
				case ESC_ESC:
					decodedData[decodedDataIndex++] = ESC;
					break;
				default:
					decodedData[decodedDataIndex++] = data[i];
					break;
				}
				break;
			default:
				decodedData[decodedDataIndex++] = data[i];
				break;
			}
		}
		return null;
	}

	public static final class DiveCANDataIdentifier {

		public static final int QueryBusDevices = 0x8000;
		public static final int SerialNumber = 0x8010;
		public static final int SoftwareVersion = 0x8011;
		public static final int FirmwareDownloadSupported = 0x8020;
		public static final int LogUploadSupported = 0x8021;
		public static final int ForwardCANFrames = 0x8030;
		public static final int AddressConfiguration = 0x8040;
		public static final int QueryDeviceInfoLow = 0x8100;
		public static final int QueryDeviceInfoHigh = 0x81FF;
	}

	public static final class ServiceMessage {

		public static final int ReadDataByIdentifier = 0x22;
		public static final int WriteDataByIdentifier = 0x2E;
		public static final int RequestDownload = 0x34;
		public static final int RequestDownloadResponse = 0x74;
		public static final int RequestUpload = 0x35;
		public static final int TransferData = 0x36;
		public static final int RequestTransferExit = 0x37;
		public static final int ReadDataByIdentifierResponse = 0x62;
		public static final int WriteDataByIdentifierResponse = 0x6E;
		public static final int TransferDataResponse = 0x76;
		public static final int RequestTransferExitResponse = 0x77;
		public static final int RequestUploadResponse = 0x75;
		public static final int NegativeResponse = 0x7F;
	}

	public static final class DiveCANDeviceID {

		public static final int BROADCAST = 0x00;
		public static final int HANDSET = 0x01;
		public static final int O2_SENSOR = 0x02;
		public static final int HUD = 0x03;
		public static final int SOLENOID = 0x04;
		public static final int BATTERY_CONTROL = 0x05;
		public static final int WIRED_EXTERNAL_DEVICE = 0x7F;
		public static final int GATEWAY = 0x80;
		public static final int SECONDARY_HANDSET = 0x81;
		public static final int SECONDARY_O2_SENSOR = 0x82;
		public static final int SECONDARY_HUD = 0x83;
		public static final int SECONDARY_SOLENOID = 0x84;
		public static final int SECONDARY_BATTERY_CONTROL = 0x85;
		public static final int WIRELESS_EXTERNAL_DEVICE = 0xFF;
	}

	private static class Holder<T> {

		protected T obj;

		protected Holder(T defaultVal) {
			obj = defaultVal;
		}
	}
}
