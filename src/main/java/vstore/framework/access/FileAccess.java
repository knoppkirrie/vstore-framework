package vstore.framework.access;

import ch.hsr.geohash.GeoHash;

/**
 * Represents an access of a file with its GeoHash as location and the TimeOfWeek as time parameter
 *
 */
public class FileAccess {
	
	private String fileUuid;
	private GeoHash geohash;
	private TimeOfWeek timeOfWeek;
	
	public FileAccess(String fileUuid, GeoHash geohash, TimeOfWeek tow) {
		this.fileUuid = fileUuid;
		this.geohash = geohash;
		this.timeOfWeek = tow;
	}

	
	// Getters
	
	public String getFileUuid() {
		return fileUuid;
	}

	public GeoHash getGeohash() {
		return geohash;
	}

	public TimeOfWeek getTimeOfWeek() {
		return timeOfWeek;
	}
	
	
}
