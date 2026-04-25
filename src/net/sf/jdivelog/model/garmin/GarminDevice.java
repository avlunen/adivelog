package net.sf.jdivelog.model.garmin;

public class GarminDevice {
   private String m_manufacturer;
   private Integer m_productNumber;
   private String m_productName;
   private Long m_serialNumber;
   private Float m_softwareVersion;
   private String m_batteryStatus;
   private Integer m_batteryLevel;
   
   public Integer getM_productNumber() {
      return m_productNumber;
   }
   public void setM_productNumber(Integer m_productNumber) {
      this.m_productNumber = m_productNumber;
   }
   public String getM_productName() {
      return m_productName;
   }
   public void setM_productName(String m_productName) {
      this.m_productName = m_productName;
   }
   public Long getM_serialNumber() {
      return m_serialNumber;
   }
   public void setM_serialNumber(Long m_serialNumber) {
      this.m_serialNumber = m_serialNumber;
   }
   public Float getM_softwareVersion() {
      return m_softwareVersion;
   }
   public void setM_softwareVersion(Float m_softwareVersion) {
      this.m_softwareVersion = m_softwareVersion;
   }
   public String getM_batteryStatus() {
      return m_batteryStatus;
   }
   public void setM_batteryStatus(String m_batteryStatus) {
      this.m_batteryStatus = m_batteryStatus;
   }
   public Integer getM_batteryLevel() {
      return m_batteryLevel;
   }
   public void setM_batteryLevel(Integer m_batteryLevel) {
      this.m_batteryLevel = m_batteryLevel;
   }
   public String getM_manufacturer() {
      return m_manufacturer;
   }
   public void setM_manufacturer(String m_manufacturer) {
      this.m_manufacturer = m_manufacturer;
   }
}
