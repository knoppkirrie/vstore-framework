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

//	private GeoHash geohash;
	private int radius;
	private GeoHashCircleQuery circle;
	
	public AccessLocation(String geohashString, int radius) {
		GeoHash geohash = GeoHash.fromGeohashString(geohashString);
		
		this.radius = radius;
		this.circle = new GeoHashCircleQuery(geohash.getPoint(), radius);
	}
	
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
	
	
}
