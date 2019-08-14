package vstore.framework.access;

import ch.hsr.geohash.GeoHash;
import vstore.framework.utils.IdentifierUtils;

/**
 * Represents an access of a file with its GeoHash as location and the TimeOfWeek as time parameter
 *
 */
public class FileAccess {
	
	private String uuid;
	private String fileUuid;
	private GeoHash geohash;
	private long timestamp;
	private boolean isUploaded;
	private String nodeId;
	
	public FileAccess(String fileUuid, GeoHash geohash, long timestamp, String nodeId) {
		this.uuid = IdentifierUtils.getNewUniqueIdentifier();
		this.fileUuid = fileUuid;
		this.geohash = geohash;
		this.timestamp = timestamp;
		this.isUploaded = false;
		this.nodeId = nodeId;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	// Getters
	public String getUuid() {
		return uuid;
	}
	
	public String getFileUuid() {
		return fileUuid;
	}

	public GeoHash getGeohash() {
		return geohash;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	
}
