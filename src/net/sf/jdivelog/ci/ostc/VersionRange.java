package net.sf.jdivelog.ci.ostc;

/**
 * Description: Describes a range of firmware versions.
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.10 $
 */
public class VersionRange {
    private static final int VERSION_UNBOUNDED = 99999;

    private final int minVersion;
    private final int maxVersion;

    /**
     * Construct a new VersionRange object without an upper bound. This can be
     * used for the newest firmware version.
     * 
     * @param minVersion
     *            minimum version to match
     */
    public VersionRange(int minVersion) {
        this.minVersion = minVersion;
        this.maxVersion = VERSION_UNBOUNDED;
    }

    /**
     * Construct a new VersionRange object with lower and upper bound.
     * 
     * @param minVersion
     *            minimum version to match
     * @param maxVersion
     *            maximum version to match
     */
    public VersionRange(int minVersion, int maxVersion) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    /**
     * Generate a version number. Use this method to get the version number out
     * of a memory dump from the dive computer.
     * 
     * @param highByte
     * @param lowByte
     * 
     * @return version number
     */
    public static int getVersion(int highByte, int lowByte) {
        return (highByte & 0xff) * 100 + (lowByte & 0xff);
    }

    /**
     * Generate a version number.
     * 
     * @param versionString
     *            string in the form of e.g. "1.20"
     * 
     * @return version number
     */
    public static int getVersion(String versionString) {
        String[] parts = versionString.split("\\.");

        return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
    }

    /**
     * Check if the given version number is between minVersion and maxVersion.
     * 
     * @param version
     *            version number
     * 
     * @return true if the given version is between minVersion and maxVersion
     */
    public boolean isInRange(int version) {
        return version >= minVersion && version <= maxVersion;
    }

    /**
     * Get a string representation of this object.
     */
    public String toString() {
        return toString(minVersion) + " - " + (maxVersion == VERSION_UNBOUNDED ? "X" : toString(maxVersion));
    }

    /**
     * Get a string representation of the given version number.
     * 
     * @param version
     *            version number
     * 
     * @return string representation of the version number
     */
    private String toString(int version) {
        return String.format("%d.%d", version / 100, version % 100);
    }
}
