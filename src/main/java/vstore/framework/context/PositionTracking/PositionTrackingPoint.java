package vstore.framework.context.PositionTracking;

import ch.hsr.geohash.GeoHash;

public class PositionTrackingPoint {

	private String id;
//	private float lat;
//	private float lng;
	private long timestamp;
	private GeoHash geohash;
	
	
	public PositionTrackingPoint(String id) {
		this.id = id;
	}
	
	public void setTimestamp(long ts) {
		this.timestamp = ts;
	}
	
	public void setGeoHash(String geohashString) {
		this.geohash = GeoHash.fromGeohashString(geohashString);
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public GeoHash getGeoHash() {
		return this.geohash;
	}
	
	public String getGeohashString() {
		return this.geohash.toBase32();
	}
	
	public String getId() {
		return this.id;
	}
	
}
