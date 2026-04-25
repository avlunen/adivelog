package net.sf.jdivelog.model.garmin;

import java.util.ArrayList;
import java.util.Date;
import java.time.Duration;
import java.util.List;
import java.util.LongSummaryStatistics;

/**
 * Class to aggregate the dive profile data points
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 */
public class DepthProfileEntries {  
   /** Dive profile */
   private List<depthProfileEntry> depthProfileEntries =
         new ArrayList<depthProfileEntry> ();

   // methods
   public List<depthProfileEntry> getDepthProfileEntries() {
      return depthProfileEntries;
   }

   public void setDepthProfileEntries(List<depthProfileEntry> depthProfileEntries) {
      this.depthProfileEntries = depthProfileEntries;
   }

   public void addEntry(depthProfileEntry dpe) {
      depthProfileEntries.add(dpe);
   }
   
   /**
    * Function to retrieve the maximum depth of the dive profile
    * @return double
    */
   public double maxDepth() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_depth).max().getAsDouble();
   }

   /**
    * Function to retrieve the average depth of the dive profile
    * @return double
    */
   public double avgDepth() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_depth).average().getAsDouble();
   }
   
   public double minTemperature() {
      return depthProfileEntries.stream().mapToDouble(depthProfileEntry::getM_temperature).min().getAsDouble();
   }
   
   /**
    * Function to calculate the duration of the dive in minutes
    * @return double
    */
   public double duration() {
      LongSummaryStatistics sin = depthProfileEntries.stream().mapToLong(depthProfileEntries -> depthProfileEntries.getM_timestamp().getEpochSecond()).summaryStatistics();
      Duration dur = Duration.ofSeconds(sin.getMax() - sin.getMin());

      return dur.getSeconds()/60;
   }
   
   /**
    * Get the date of the dive
    * @return Date
    */
   public Date getDate() {
      return new Date((depthProfileEntries.stream().mapToLong(depthProfileEntries -> depthProfileEntries.getM_timestamp().getEpochSecond()).min().getAsLong()*1000));
   }
}
