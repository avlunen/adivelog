/////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2026 Garmin International, Inc.
// Licensed under the Flexible and Interoperable Data Transfer (FIT) Protocol License; you
// may not use this file except in compliance with the Flexible and Interoperable Data
// Transfer (FIT) Protocol License.
/////////////////////////////////////////////////////////////////////////////////////////////

package net.sf.jdivelog.model.garmin;

import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.DeviceSettingsMesg;
import com.garmin.fit.DiveGasMesg;
import com.garmin.fit.DiveSettingsMesg;
import com.garmin.fit.Event;
import com.garmin.fit.EventMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FitDecoder;
import com.garmin.fit.FitMessages;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SportMesg;
import com.garmin.fit.SubSport;

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
 * @version 1.1
 * @since 25 Apr 2026
 */

public class garminFitDecode {
   /** logger instance */
   private static final Logger LOGGER = Logger.getLogger(GarminFitFileLoader.class.getName());
   /** Device information */
   GarminDevice myDevice = new GarminDevice();
   /** Dive settings */
   GarminDiveSettings dvs = new GarminDiveSettings();
   /** dive profile */
   DepthProfileEntries dpes = new DepthProfileEntries();
   
   public void decodeFile(String fn) {
      try {
         FileInputStream inputStream = new FileInputStream(fn);
         FitDecoder fitDecoder = new FitDecoder();
         FitMessages fitMessages;

         fitMessages = fitDecoder.decode(inputStream);

         // fitMessages will contain all of the messages decoded from the file.
         decodeMessages(fitMessages);
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
   
   private void decodeMessages(FitMessages fitMessages) {
      // sport messages
      for(SportMesg spocht : fitMessages.getSportMesgs()) {
         Sport sp = spocht.getSport();
         SubSport ssp = spocht.getSubSport();
         
         if(sp.getValue() != Sport.DIVING.getValue()) {
            return;
         }
         
         dpes.setM_divetype(SubSport.getStringFromValue(ssp));
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
         
         // device settings
         for(DeviceSettingsMesg ds : fitMessages.getDeviceSettingsMesgs()) {
            myDevice.setM_utc_offset(ds.getUtcOffset().intValue());
         }
         
         // dive settings
         for(DiveSettingsMesg dv : fitMessages.getDiveSettingsMesgs()) {
            dvs.setM_water_density(dv.getWaterDensity().doubleValue());
            dpes.setM_water_density(dv.getWaterDensity().doubleValue());
         }
         
         // gas settings
         for(DiveGasMesg dg : fitMessages.getDiveGasMesgs()) {
            dpes.getM_start_gas().setHelium(Double.valueOf(dg.getHeliumContent()/100.0));
            dpes.getM_start_gas().setOxygen(Double.valueOf(dg.getOxygenContent()/100.0));
            dpes.getM_start_gas().setNitrogen(Double.valueOf((100-dg.getHeliumContent()-dg.getOxygenContent())/100.0));
         }
         
         // TODO the Garmin Fit SDK seems to always return metric, double-check
         // Garmin Records       
         for(RecordMesg rec : fitMessages.getRecordMesgs()) {
            depthProfileEntry dpe = new depthProfileEntry();
            dpe.setM_timestamp(rec.getTimestamp().getInstant());
            dpe.setM_pressure(rec.getAbsolutePressure());
            dpe.setM_depth(rec.getDepth());
            dpe.setM_temperature(rec.getTemperature());
            dpe.setM_cns_load(rec.getCnsLoad().floatValue());
            dpe.setM_n2_load(rec.getN2Load().floatValue());
            dpe.setM_po2(rec.getPo2());
            dpe.setM_ndl(rec.getNdlTime() == null ? 999 : rec.getNdlTime());
            dpe.setM_tts(rec.getTimeToSurface());
            dpe.setM_next_stop(rec.getNextStopDepth());
            dpes.addEntry(dpe);
         }
         
         // dive events
         for(EventMesg em : fitMessages.getEventMesgs()) {
            if(em.getEvent() != Event.DIVE_ALERT) {
               dpes.addAlert(em.getTimestamp().getInstant(), Integer.valueOf(em.getEvent().getValue()));
            }
         }
      }      
   }
   
   public DepthProfileEntries getDpes() {
      return dpes;
   }

   public GarminDevice getMyDevice() {
      return myDevice;
   }

   public JDive getDiveToAdd(Long diveNo) {
      JDive diveToAdd = new JDive();
      
      // set dive data
      if(dpes.getDepthProfileEntries().size() > 0) {
         diveToAdd.setDiveNumber(diveNo);
         diveToAdd.setAverageDepth(dpes.avgDepth());
         diveToAdd.setDate(dpes.getDate());
         diveToAdd.setDepth(dpes.maxDepth());
         diveToAdd.setDuration(dpes.duration());
         diveToAdd.setTemperature(dpes.minTemperature());
         diveToAdd.setUnits("metric");
      }
      else diveToAdd.setDiveNumber(-1L);

      return diveToAdd;
   }

}
