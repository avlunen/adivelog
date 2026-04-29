package net.sf.jdivelog.model.garmin;

import java.time.Instant;

/**
 * Class for holding a single data point in the diveprofile
 * 
 * @author Alexander von Lünen
 * @version 1.0
 * @since 23 Apr 2026
 */
public class depthProfileEntry {
   private Instant m_timestamp;
   private Long m_pressure;
   private Double m_altitude;
   private Float m_depth;
   private Byte m_temperature;
   private Float m_cns_load;
   private Float m_n2_load;
   private Float m_po2;
   private Long m_ndl;
   private Long m_tts;
   private Float m_next_stop;
   private Integer alarm;
   
   
   public Instant getM_timestamp() {
      return m_timestamp;
   }
   public void setM_timestamp(Instant m_timestamp) {
      this.m_timestamp = m_timestamp;
   }
   public Long getM_pressure() {
      return m_pressure;
   }
   public void setM_pressure(Long m_pressure) {
      this.m_pressure = m_pressure;
   }
   public Double getM_altitude() {
      return m_altitude;
   }
   public void setM_altitude(Double m_altitude) {
      this.m_altitude = m_altitude;
   }
   public Float getM_depth() {
      return m_depth;
   }
   public void setM_depth(Float m_depth) {
      this.m_depth = m_depth;
   }
   public Byte getM_temperature() {
      return m_temperature;
   }
   public void setM_temperature(Byte m_temperature) {
      this.m_temperature = m_temperature;
   }
   public Float getM_cns_load() {
      return m_cns_load;
   }
   public void setM_cns_load(Float m_cns_load) {
      this.m_cns_load = m_cns_load;
   }
   public Float getM_n2_load() {
      return m_n2_load;
   }
   public void setM_n2_load(Float m_n2_load) {
      this.m_n2_load = m_n2_load;
   }
   public Float getM_po2() {
      return m_po2;
   }
   public void setM_po2(Float m_po2) {
      this.m_po2 = m_po2;
   }
   public Long getM_ndl() {
      return m_ndl;
   }
   public void setM_ndl(Long m_ndl) {
      this.m_ndl = m_ndl;
   }
   public Long getM_tts() {
      return m_tts;
   }
   public void setM_tts(Long m_tts) {
      this.m_tts = m_tts;
   }
   public Integer getAlarm() {
      return alarm;
   }
   public void setAlarm(Integer alarm) {
      this.alarm = alarm;
   }
   public Float getM_next_stop() {
      return m_next_stop;
   }
   public void setM_next_stop(Float m_next_stop) {
      this.m_next_stop = m_next_stop;
   }

}
