package net.sf.jdivelog.model.garmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;

import net.sf.jdivelog.model.udcf.Gas;

/**
 * Class to aggregate the dive profile data points
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 * @version 1.1
 * @since 26 Apr 2026
 */
public class DepthProfileEntries {
   private String m_unit_system = "metric";
   private Double m_water_density = 1000.0;
   private String m_divetype;
   private Gas m_start_gas = new Gas();

   /** Dive alerts */
   private Map<Instant, Integer> m_alerts = new HashMap<Instant, Integer>();

   /** Dive profile */
   private List<depthProfileEntry> depthProfileEntries = new ArrayList<depthProfileEntry>();

   // methods
   public List<depthProfileEntry> getDepthProfileEntries() {
      return depthProfileEntries;
   }

   public void addEntry(depthProfileEntry dpe) {
      depthProfileEntries.add(dpe);
   }
   
   public void addAlert(Instant timestamp, Integer alert) {
      m_alerts.put(timestamp, alert);
   }

   /**
    * Function to retrieve the maximum depth of the dive profile
    * 
    * @return double
    */
   public double maxDepth() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_depth).max().getAsDouble();
   }

   /**
    * Function to retrieve the average depth of the dive profile
    * 
    * @return double
    */
   public double avgDepth() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_depth).average().getAsDouble();
   }

   /**
    * Function to retrieve the lowest temperature of the dive
    * 
    * @return double
    */
   public double minTemperature() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_temperature).min().getAsDouble();
   }

   /**
    * Function to calculate the duration of the dive in minutes
    * 
    * @return double
    */
   public double duration() {
      LongSummaryStatistics sin = depthProfileEntries.stream()
            .mapToLong(depthProfileEntries -> depthProfileEntries.getM_timestamp().getEpochSecond())
            .summaryStatistics();
      Duration dur = Duration.ofSeconds(sin.getMax() - sin.getMin());

      return dur.getSeconds() / 60;
   }

   /**
    * Function to return the time stamp of the first data point in epoch seconds
    * @return long
    */
   public long startPoint() {
      LongSummaryStatistics sin = depthProfileEntries.stream()
            .mapToLong(depthProfileEntries -> depthProfileEntries.getM_timestamp().getEpochSecond())
            .summaryStatistics();
      
      return sin.getMin();
   }
   /**
    * Get the date of the dive
    * 
    * @return Date
    */
   public Date getDate() {
      return new Date((depthProfileEntries.stream()
            .mapToLong(depthProfileEntries -> depthProfileEntries.getM_timestamp().getEpochSecond()).min().getAsLong()
            * 1000));
   }

   public Double getM_water_density() {
      return m_water_density;
   }

   public void setM_water_density(Double m_water_density) {
      this.m_water_density = m_water_density;
   }

   public String getM_unit_system() {
      return m_unit_system;
   }

   public void setM_unit_system(String m_unit_system) {
      this.m_unit_system = m_unit_system;
   }

   public String getM_divetype() {
      return m_divetype;
   }

   public void setM_divetype(String m_divetype) {
      this.m_divetype = m_divetype;
   }

   public Gas getM_start_gas() {
      return m_start_gas;
   }

   public void setM_start_gas(Gas m_start_gas) {
      this.m_start_gas = m_start_gas;
   }

   public Map<Instant, Integer> getM_alerts() {
      return m_alerts;
   }
}
