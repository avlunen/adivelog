/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: DiveLogParser.java
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
package net.sf.jdivelog.ci.sri.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jdivelog.ci.sri.helper.Buffer;

public final class DiveLogParser {

	private static final int TOTAL_LOG_DATA_LENGTH = 131200;
	private static final int LOG_DATA_ROW_LENGTH = 16;
	private static final int FINAL_RECORD_LENGTH = 128;
	private static final int INTERAL_LENGTH = 2560;
	private static final int LOG_OPENING_BLOCK_LENGTH = 128;
	private static final int LOG_CLOSING_BLOCK_LENGTH = 128;
	private static final int RECORD_TIME_INCREMENT = 10; // seconds
	private static final int FINAL_RECORD_ID = 0xFFFD;
	private static final int DIVE_LOG_OPENING_ID = 0xFFFF;
	private static final int DIVE_LOG_CLOSING_ID = 0xFFFE;
	private static final List<Integer> SUPPORTED_LOG_VERSIONS = Arrays
			.<Integer> asList(1, 2, 3, 4, 5);

	public static boolean logVersionSupported(int logVersion) {
		if (SUPPORTED_LOG_VERSIONS.indexOf(logVersion) > -1) {
			return true;
		}

		return false;
	}

	public boolean verifyLogChecksum(byte[] logData, int checksum) {
		if (logData.length == TOTAL_LOG_DATA_LENGTH) {
			int logDataSum = 0;

			for (int i = 0; i < (TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH); i++) {
				logDataSum += logData[i];
			}

			// Checksum is the low byte of the total sum
			return (logDataSum & 0xFF) == checksum;
		}
        return false;
	}

	public FinalLog getFinalLog(byte[] logData) {
		FinalLog finalRecord = null;
		int fro = TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH; // Final Record
																// Offset

		Buffer buf = new Buffer(logData, fro, FINAL_RECORD_LENGTH);

		if (buf.readBeInt16() == FINAL_RECORD_ID) {
			int logVersion = DiveLogUtil.getLogVersion(logData);

			finalRecord = new FinalLog();
			finalRecord.setComputerSerialNum(buf.readBeInt32());
			finalRecord.setReserved1(buf.readBeInt24());
			finalRecord.setReserved2(buf.readByte());
			finalRecord.setSoftwareVersion(buf.readByte());
			finalRecord.setComputerModel(buf.readByte());
			finalRecord.setLogVersion(logVersion);
			finalRecord.setProduct(DiveLogUtil.getProduct(logData));

			if (logVersion >= 5) {
				buf.skip(2);
				finalRecord.setFeatures(buf.readBeInt16());
			}
		}

		return finalRecord;
	}

	public List<DiveLog> getDiveLogs(byte[] logData) {
		int logVersion = DiveLogUtil.getLogVersion(logData);

		int firstOpeningBlockOffset = -1;
		int diveLogsEndOffset = TOTAL_LOG_DATA_LENGTH - FINAL_RECORD_LENGTH;

		if (logVersion >= 4) {
			diveLogsEndOffset -= INTERAL_LENGTH;
		}

		List<DiveLog> diveLogs = new ArrayList<DiveLog>();
		DiveLog diveLog = null;
		boolean diveLogOpen = false;

		Buffer buf = new Buffer(logData, 0, logData.length);

		while (buf.getPosition() != firstOpeningBlockOffset) {
			if (isOpeningBlock(logData, buf.getPosition())) {
				// Save the position of the first opening block.
				if (firstOpeningBlockOffset == -1) {
					firstOpeningBlockOffset = buf.getPosition();
				}

				diveLog = new DiveLog();
				diveLog.setDiveLogHeader(createDiveLogHeader(logData,
						buf.getPosition(), logVersion));
				diveLog.setDiveLogRecords(new ArrayList<DiveLogRecord>());

				diveLogOpen = true;
				buf.skip(LOG_OPENING_BLOCK_LENGTH);
			} else if (diveLog != null
					&& isClosingBlock(logData, buf.getPosition())) {
				int closingBlockDiveNumber = buf
						.getBeInt16(buf.getPosition() + 2);

				if (diveLog.getDiveLogHeader().getDiveNumber() == closingBlockDiveNumber) {
					diveLog.setDiveLogFooter(createDiveLogFooter(logData,
							buf.getPosition(), logVersion));
					diveLogs.add(diveLog);
				}

				diveLogOpen = false;
				buf.skip(LOG_CLOSING_BLOCK_LENGTH);
			} else if (diveLogOpen && buf.getByte() != 0xFF
					&& !isEmptyRow(logData, buf.getPosition())) {
				// Dive log records start after the opening block and continue
				// till the closing
				// block. Sometimes there will be empty rows just before the
				// closing block. These rows
				// should be ignored.
				DiveLogRecord diveLogRecord = createDiveLogRecord(logData,
						buf.getPosition(), logVersion);
				diveLogRecord.setTime(diveLog.getDiveLogRecords().size()
						* RECORD_TIME_INCREMENT);
				diveLog.getDiveLogRecords().add(diveLogRecord);

				buf.skip(LOG_DATA_ROW_LENGTH);
			} else {
				// Nothing found on this log data row, jump to the next one.
				buf.skip(LOG_DATA_ROW_LENGTH);
			}

			// Reached the end of the dive logs.
			if (buf.getPosition() >= diveLogsEndOffset) {
				if (firstOpeningBlockOffset == -1) {
					// No opening blocks in entire file, exit loop.
					break;
				} else {
					// The log data will loop back to the beginning when after
					// it reaches the
					// end. This is known as cylindrical memory.
					buf.setPosition(0);
				}
			}
		}

		return diveLogs;
	}

	private boolean isOpeningBlock(byte[] logData, int offset) {
		Buffer buf = new Buffer(logData, offset);

		return buf.getBeInt16() == DIVE_LOG_OPENING_ID
				&& buf.getByte(16) != 0xFF && buf.getByte(32) != 0xFF
				&& buf.getByte(48) != 0xFF && buf.getByte(64) != 0xFF
				&& buf.getByte(80) != 0xFF && buf.getByte(96) != 0xFF
				&& buf.getByte(112) != 0xFF;
	}

	private boolean isClosingBlock(byte[] logData, int offset) {
		Buffer buf = new Buffer(logData, offset);

		return buf.getBeInt16() == DIVE_LOG_CLOSING_ID
				&& buf.getByte(16) != 0xFF && buf.getByte(32) != 0xFF
				&& buf.getByte(48) != 0xFF && buf.getByte(64) != 0xFF
				&& buf.getByte(80) != 0xFF && buf.getByte(96) != 0xFF
				&& buf.getByte(112) != 0xFF;
	}

	private boolean isEmptyRow(byte[] logData, int offset) {
		Buffer buf = new Buffer(logData, offset);

		return buf.getByte(0) == 0 && buf.getByte(1) == 0
				&& buf.getByte(2) == 0 && buf.getByte(3) == 0
				&& buf.getByte(4) == 0 && buf.getByte(5) == 0
				&& buf.getByte(6) == 0 && buf.getByte(7) == 0
				&& buf.getByte(8) == 0 && buf.getByte(9) == 0
				&& buf.getByte(10) == 0 && buf.getByte(11) == 0
				&& buf.getByte(12) == 0 && buf.getByte(13) == 0
				&& buf.getByte(14) == 0 && buf.getByte(15) == 0;
	}

	private DiveLogHeader createDiveLogHeader(byte[] logData, int offset,
			int logVersion) {
		Buffer buf = new Buffer(logData, offset);

		buf.skip(2);

		DiveLogHeader diveLogHeader = new DiveLogHeader();
		diveLogHeader.setDiveNumber(buf.readBeInt16());
		diveLogHeader.setGradientFactorLow(buf.readByte());
		diveLogHeader.setGradientFactorHigh(buf.readByte());
		diveLogHeader.setSurfaceTime(buf.readBeInt16());
		diveLogHeader.setUnitSystem(buf.readByte());
		diveLogHeader.setInternalBatteryVoltage(buf.readByte() / 10.0);
		diveLogHeader.setCnsPercent(buf.readBeInt16());
		diveLogHeader.setTimestamp(buf.readBeInt32());
		int sensorStatuses = buf.readByte();
		diveLogHeader.setO2Sensor1Status((sensorStatuses & 0x1) != 0);
		diveLogHeader.setO2Sensor2Status((sensorStatuses & 0x2) != 0);
		diveLogHeader.setO2Sensor3Status((sensorStatuses & 0x4) != 0);
		diveLogHeader.setLowPPO2Setpoint(buf.readByte() / 100.0);
		diveLogHeader.setHighPPO2Setpoint(buf.readByte() / 100.0);
		diveLogHeader.setFirmwareVersion(buf.readByte());
		diveLogHeader.setOcGas0O2Percent(buf.readByte());
		diveLogHeader.setOcGas1O2Percent(buf.readByte());
		diveLogHeader.setOcGas2O2Percent(buf.readByte());
		diveLogHeader.setOcGas3O2Percent(buf.readByte());
		diveLogHeader.setOcGas4O2Percent(buf.readByte());
		diveLogHeader.setCcGas0O2Percent(buf.readByte());
		diveLogHeader.setCcGas1O2Percent(buf.readByte());
		diveLogHeader.setCcGas2O2Percent(buf.readByte());
		diveLogHeader.setCcGas3O2Percent(buf.readByte());
		diveLogHeader.setCcGas4O2Percent(buf.readByte());
		diveLogHeader.setOcGas0HePercent(buf.readByte());
		diveLogHeader.setOcGas1HePercent(buf.readByte());
		diveLogHeader.setOcGas2HePercent(buf.readByte());
		diveLogHeader.setOcGas3HePercent(buf.readByte());
		diveLogHeader.setOcGas4HePercent(buf.readByte());
		diveLogHeader.setCcGas0HePercent(buf.readByte());
		diveLogHeader.setCcGas1HePercent(buf.readByte());
		diveLogHeader.setCcGas2HePercent(buf.readByte());
		diveLogHeader.setCcGas3HePercent(buf.readByte());
		diveLogHeader.setCcGas4HePercent(buf.readByte());
		diveLogHeader.setSwitchUpSetting(buf.readByte());
		diveLogHeader.setSwitchUpDepth(buf.readByte());
		diveLogHeader.setSwitchDownSetting(buf.readByte());
		diveLogHeader.setSwitchDownDepth(buf.readByte());
		diveLogHeader.setO2SensorMode(buf.readByte());

		if (logVersion >= 5) {
			buf.skip(2);

			diveLogHeader.setSurfacePressure(buf.readBeInt16());
			byte[] errFlags = buf.readBytes(8);
			byte[] errAcks = buf.readBytes(8);
			diveLogHeader.setErrorFlags(getErrorCodes(errFlags));
			diveLogHeader.setErrorAcks(getErrorCodes(errAcks));
			diveLogHeader.setCurrentEventLogNumber(buf.readByte());
			diveLogHeader.setDecoModel(buf.readByte());
			diveLogHeader.setVpmbConservatism(buf.readByte());
			diveLogHeader.setSolenoidDepthCompensation(buf.readByte() != 0);
			diveLogHeader.setOcMinimumPPO2(buf.readByte() / 100.0);
			diveLogHeader.setOcMaximumPPO2(buf.readByte() / 100.0);
			diveLogHeader.setOcDecoPPO2(buf.readByte() / 100.0);
			diveLogHeader.setCcMinimumPPO2(buf.readByte() / 100.0);
			diveLogHeader.setCcMaximumPPO2(buf.readByte() / 100.0);
			diveLogHeader.setSensorDisplay(buf.readByte());
			diveLogHeader.setDecoViolatedDisplayed(buf.readByte() != 0);
			diveLogHeader.setLastStopDepth(buf.readByte());
			diveLogHeader.setEndDiveDelay(buf.readBeInt16());
			diveLogHeader.setClockFormat(buf.readByte());
			diveLogHeader.setTitleColor(buf.readByte());
			diveLogHeader.setShowOCOnlyPPO2(buf.readByte() != 0);
			diveLogHeader.setSalinity(buf.readBeInt16());
		}

		return diveLogHeader;
	}

	private DiveLogFooter createDiveLogFooter(byte[] logData, int offset,
			int logVersion) {
		Buffer buf = new Buffer(logData, offset);

		buf.skip(4);

		DiveLogFooter diveLogFooter = new DiveLogFooter();
		diveLogFooter.setMaxDiveDepth(buf.readBeInt16());
		diveLogFooter.setDiveTime(buf.readBeInt16());
		buf.skip(1);
		diveLogFooter.setInternalBatteryVoltage(buf.readByte() / 10.0);
		diveLogFooter.setCnsPercent(buf.readBeInt16());
		diveLogFooter.setTimestamp(buf.readBeInt32());
		int sensorStatuses = buf.readByte();
		diveLogFooter.setO2Sensor1Status((sensorStatuses & 0x1) != 0);
		diveLogFooter.setO2Sensor2Status((sensorStatuses & 0x2) != 0);
		diveLogFooter.setO2Sensor3Status((sensorStatuses & 0x4) != 0);
		diveLogFooter.setLowPPO2Setpoint(buf.readByte() / 100.0);
		diveLogFooter.setHighPPO2Setpoint(buf.readByte() / 100.0);
		buf.skip(1);
		diveLogFooter.setOcGas0O2Percent(buf.readByte());
		diveLogFooter.setOcGas1O2Percent(buf.readByte());
		diveLogFooter.setOcGas2O2Percent(buf.readByte());
		diveLogFooter.setOcGas3O2Percent(buf.readByte());
		diveLogFooter.setOcGas4O2Percent(buf.readByte());
		diveLogFooter.setCcGas0O2Percent(buf.readByte());
		diveLogFooter.setCcGas1O2Percent(buf.readByte());
		diveLogFooter.setCcGas2O2Percent(buf.readByte());
		diveLogFooter.setCcGas3O2Percent(buf.readByte());
		diveLogFooter.setCcGas4O2Percent(buf.readByte());
		diveLogFooter.setOcGas0HePercent(buf.readByte());
		diveLogFooter.setOcGas1HePercent(buf.readByte());
		diveLogFooter.setOcGas2HePercent(buf.readByte());
		diveLogFooter.setOcGas3HePercent(buf.readByte());
		diveLogFooter.setOcGas4HePercent(buf.readByte());
		diveLogFooter.setCcGas0HePercent(buf.readByte());
		diveLogFooter.setCcGas1HePercent(buf.readByte());
		diveLogFooter.setCcGas2HePercent(buf.readByte());
		diveLogFooter.setCcGas3HePercent(buf.readByte());
		diveLogFooter.setCcGas4HePercent(buf.readByte());
		diveLogFooter.setSwitchUpSetting(buf.readByte());
		diveLogFooter.setSwitchUpDepth(buf.readByte());
		diveLogFooter.setSwitchDownSetting(buf.readByte());
		diveLogFooter.setSwitchDownDepth(buf.readByte());
		diveLogFooter.setO2SensorMode(buf.readByte());

		if (logVersion >= 4) {
			diveLogFooter.setSurfacePressure(buf.readByte()); // 46
			buf.skip(4);
			byte[] errFlags = buf.readBytes(8);
			buf.skip(1);
			byte[] errAcks = buf.readBytes(8);
			diveLogFooter.setErrorFlags(getErrorCodes(errFlags));
			diveLogFooter.setErrorAcks(getErrorCodes(errAcks));

			diveLogFooter.setCurrentEventLogNumber(buf.readByte());
			diveLogFooter.setAverageDiveDepth(buf.readBeInt16());
		}

		return diveLogFooter;
	}

	private List<Boolean> getErrorCodes(byte[] errBuf) {
		// The error codes are 8 bytes long and read from right to left so start
		// at the last byte.
		int offset = 7;

		List<Boolean> errorCodes = new ArrayList<Boolean>(64);

		// Loop through each byte with i
		for (int i = 0; i < 8; i++) {
			int mask = 1;

			// Loop through each bit of each byte with j
			for (int j = 0; j < 8; j++) {
				// Offsets will go from the last to first byte
				errorCodes.add((errBuf[offset - i] & mask) != 0);

				// Mask doubles each loop to select the correct bit
				mask = mask << 1;
			}
		}

		return errorCodes;
	}

	private DiveLogRecord createDiveLogRecord(byte[] logData, int offset,
			int logVersion) {
		Buffer buf = new Buffer(logData, offset);

		DiveLogRecord diveLogRecord = new DiveLogRecord();
		diveLogRecord.setDepth(buf.readBeInt16() / 10.0);
		diveLogRecord.setNextStopDepth(buf.readBeInt16());
		diveLogRecord.setTimeToSurface(buf.readBeInt16());
		diveLogRecord.setAveragePPO2(buf.readByte() / 100.0);
		diveLogRecord.setCurrentGasO2Percent(buf.readByte());
		diveLogRecord.setCurrentGasHePercent(buf.readByte());

		if (logVersion < 5) {
			diveLogRecord.setNextStopTime(buf.readByte());
			diveLogRecord.setCurrentNoDecoLimit(buf.readByte());
		} else {
			if (diveLogRecord.getNextStopDepth() == 0) {
				diveLogRecord.setCurrentNoDecoLimit(buf.readByte());
			} else {
				diveLogRecord.setNextStopTime(buf.readByte());
			}

			diveLogRecord.setBatteryVoltage((buf.readByte() + 0xFF) / 100.0);
		}

		int flag = buf.readByte();
		diveLogRecord.setGasSwitchNeeded((flag & 0x1) != 0);
		diveLogRecord.setExternalPPO2((flag & 0x2) != 0);
		diveLogRecord.setSetPointType(flag >> 2 & 0x1);

		if (logVersion < 4) {
			diveLogRecord.setCircuitMode(buf.readByte());
			diveLogRecord.setWaterTemperature(buf.readByte());
		} else {
			diveLogRecord.setCircuitSwitchType(flag >> 3 & 0x1);
			diveLogRecord.setCircuitMode(flag >> 4 & 0x1);
			diveLogRecord.setSensor1Millivolts(buf.readByte());
			diveLogRecord.setWaterTemperature(buf.readByte());
			diveLogRecord.setSensor2Millivolts(buf.readByte());
			diveLogRecord.setSensor3Millivolts(buf.readByte());
		}

		return diveLogRecord;
	}
}
