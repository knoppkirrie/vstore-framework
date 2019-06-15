package vstore.framework.access;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.queries.GeoHashCircleQuery;

/**
 * Models the point and surrounding radius for the location of a file access.
 * If the location of a FileAccess can not be assigned to an existing AccessLocation, 
 * a new one should be created.
 * 
 */
public class AccessLocation {
	
	public static final int CIRCLE_RADIUS = 1000;
	public static final int TIME_THRESHOLD = 120;

	private String id;
	private GeoHash geohash;
	private int radius;
	private GeoHashCircleQuery circle;
	private String fileUuid;
	private int count;
	private TimeOfWeek meanToW;
	
	public AccessLocation(String id) {
		this.id = id;
	}

//	public AccessLocation(String geohashString, int radius) {
//		GeoHash geohash = GeoHash.fromGeohashString(geohashString);
//		
//		this.radius = radius;
//		this.circle = new GeoHashCircleQuery(geohash.getPoint(), radius);
//	}
	
	/**
	 * @param geoHashString the geohash String of the location 
	 * @return true if the geohash location is inside the radius of this AccessLocation, false otherwise
	 */
	public boolean contains(String geoHashString) {
		GeoHash geohash = GeoHash.fromGeohashString(geoHashString);
		
		return circle.contains(geohash);
	}
	
	public int getRadius() {
		return this.radius;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeoHash getGeohash() {
		return geohash;
	}
	
	public String geohashString() {
		return geohash.toBase32();
	}

	public void setGeohash(String geohashString) {
		GeoHash geohash = GeoHash.fromGeohashString(geohashString);
		this.geohash = geohash;
		
		setCircle();
	}
	
	public void setGeohash(GeoHash geohash) {
		this.geohash = geohash;
		
		setCircle();
	}
	
	private void setCircle() {
		this.circle = new GeoHashCircleQuery(geohash.getPoint(), radius);
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public void setFileUuid(String fileUuid) {
		this.fileUuid = fileUuid;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Increases the counter for this AccessLocation object by 1.
	 */
	public void increaseCount() {
		this.count++;
	}

	public TimeOfWeek getMeanToW() {
		return meanToW;
	}

	public void setMeanToW(TimeOfWeek meanToW) {
		this.meanToW = meanToW;
	}

	public void setMeanToW(String meanToWString) {
		TimeOfWeek tow = new TimeOfWeek(meanToWString);
		this.meanToW = tow;		
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	 * Calculates the distance between the geohashed locations of this and other using haversine formula
	 * @param other 
	 * @return 
	 */
	public double getDistance(GeoHash other) {
		
		double thisLat = this.getGeohash().getPoint().getLatitude();
		double thisLng = this.getGeohash().getPoint().getLongitude();
		double otherLat = other.getPoint().getLatitude();
		double otherLng = other.getPoint().getLongitude();
		
		final int R = 6371000;	// earth radius in meters
		double rho1 = Math.toRadians(thisLat);
		double rho2 = Math.toRadians(otherLat);
		double deltaRho = Math.toRadians(thisLat - otherLat);
		double deltaLambda = Math.toRadians(thisLng - otherLng);
		
		double a = Math.sin(deltaRho / 2) * Math.sin(deltaRho / 2) 
					+ Math.cos(rho1) * Math.cos(rho2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		return R * c;
	}
	
	
}
