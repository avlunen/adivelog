/////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2026 Garmin International, Inc.
// Licensed under the Flexible and Interoperable Data Transfer (FIT) Protocol License; you
// may not use this file except in compliance with the Flexible and Interoperable Data
// Transfer (FIT) Protocol License.
/////////////////////////////////////////////////////////////////////////////////////////////

package net.sf.jdivelog.model.garmin;

import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FitDecoder;
import com.garmin.fit.FitMessages;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SportMesg;
//import com.garmin.fit.SubSport;

import net.sf.jdivelog.model.JDive;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to decode Garmin FIT files
 * 
 * If Scuba dives are present in the FIT file, they will be added to the dive dialog,
 * to then be imported into the ADiveLog app.
 * 
 * Uses the Garmin FIT SDK.
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 */

public class garminFitDecode {
   /** logger instance */
   private static final Logger LOGGER = Logger.getLogger(GarminFitFileLoader.class.getName());
   /** list of imported dives */
   private JDive diveToAdd = new JDive();
   /** Device information */
   GarminDevice myDevice = new GarminDevice();
   /** dive profile */
   DepthProfileEntries dpes = new DepthProfileEntries();
   
   public void decodeFile(String fn, Long divNo) {
      try {
         FileInputStream inputStream = new FileInputStream(fn);
         FitDecoder fitDecoder = new FitDecoder();
         FitMessages fitMessages;

         fitMessages = fitDecoder.decode(inputStream);

         // fitMessages will contain all of the messages decoded from the file.
         decodeMessages(fitMessages, divNo);
      }
      catch (java.io.IOException e) {
         LOGGER.log(Level.SEVERE, "Failed to load Garmin Fit file", e);
         e.printStackTrace();
         return;
      }
      catch (FitRuntimeException e) {
         LOGGER.log(Level.SEVERE, "FitRuntimeException decoding file", e);
         e.printStackTrace();
      }
      catch (Exception e) {
         LOGGER.log(Level.SEVERE, "Exception decoding file", e);
         e.printStackTrace();
      }

      return;
   }
   
   private void decodeMessages(FitMessages fitMessages, Long diveNo) {
      // sport messages
      for(SportMesg spocht : fitMessages.getSportMesgs()) {
         Sport sp = spocht.getSport();
         //SubSport ssp = spocht.getSubSport();
         
         if(sp.getValue() != Sport.DIVING.getValue()) {
            return;
         }
      }

      if (!fitMessages.getFileIdMesgs().isEmpty()) {     
         for(FileIdMesg fm : fitMessages.getFileIdMesgs()) {
            myDevice.setM_manufacturer(Manufacturer.getStringFromValue(fm.getManufacturer()));
            myDevice.setM_productName(GarminProduct.getStringFromValue(fm.getGarminProduct()));
            myDevice.setM_productNumber(fm.getGarminProduct());
            myDevice.setM_serialNumber(fm.getSerialNumber());
         }
         
         // device info
         for(DeviceInfoMesg dev : fitMessages.getDeviceInfoMesgs()) {
            if(dev.getGarminProduct() != null) {
               if(dev.getGarminProduct().intValue() == myDevice.getM_productNumber().intValue()) {
                  if(dev.getSoftwareVersion() != null) {
                     myDevice.setM_softwareVersion(dev.getSoftwareVersion());
                     break;
                  }
               }
            }
         }
         
         // TODO check units (metric etc?)
         // Garmin Records       
         for(RecordMesg rec : fitMessages.getRecordMesgs()) {
            depthProfileEntry dpe = new depthProfileEntry();
            dpe.setM_timestamp(rec.getTimestamp().getInstant());
            dpe.setM_pressure(rec.getAbsolutePressure());
            dpe.setM_depth(rec.getDepth());
            dpe.setM_temperature(rec.getTemperature());
            dpes.addEntry(dpe);
         }
      }
      
      // set dive data
      if(dpes.getDepthProfileEntries().size() > 0) {
         //diveToAdd.setDiveNumber(0L);
         diveToAdd.setDiveNumber(diveNo);
         diveToAdd.setAverageDepth(dpes.avgDepth());
         diveToAdd.setDate(dpes.getDate());
         diveToAdd.setDepth(dpes.maxDepth());
         diveToAdd.setDuration(dpes.duration());
         diveToAdd.setTemperature(dpes.minTemperature());
         diveToAdd.setUnits(null); // BUBU
      }
      else diveToAdd.setDiveNumber(-1L);
   }
   
   public GarminDevice getMyDevice() {
      return myDevice;
   }

   public JDive getDiveToAdd() {
      return diveToAdd;
   }

}
