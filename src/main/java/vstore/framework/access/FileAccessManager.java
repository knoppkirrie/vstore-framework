package vstore.framework.access;

import java.sql.SQLException;

import ch.hsr.geohash.GeoHash;
import vstore.framework.communication.access.FileAccessUploader;
import vstore.framework.context.ContextManager;
import vstore.framework.db.table_helper.FileAccessDBHelper;

public class FileAccessManager {

	private static FileAccessManager instance;
	
	private FileAccessManager() {}
	
	public static synchronized void initialize() {
		if (instance == null) {
			instance = new FileAccessManager();
		}
	}
	
	/**
	 * @return Gets the instance of the FileAccessManager
	 */
	public static FileAccessManager get() {
		if(instance == null)
        {
            initialize();
        }
        return instance;
	}
	
	public FileAccessUploader getFileAccessUploader() {
		return FileAccessUploader.get();
	}
	
	/*
	 * TODO: makes sense only with fileUuid as parameter, without location?
	 
	private void updateMeanAccessTime(String fileUuid, long timestamp) {
		// TODO
	}
	
	public TimeOfWeek getMeanAccessTime(String fileUuid) {
		// TODO
		return null;
	}
	*/
	
	/**
	 * Logs a get()-request of a file via the vStore-framework into the local database
	 * @param fileUuid the Uuid of the requested file
	 * @param timestamp timestamp when the request occurs
	 * @param nodeId ID of the storage node where the requested file is stored
	 */
	public void newAccess(String fileUuid, long timestamp, String nodeId) {
		
		// return if no location context available; location comparison seems pointless without location
		if (ContextManager.get().getCurrentContext().getLocationContext() == null) return;
		
		// create FileAccess obj
		double lat = ContextManager.get().getCurrentContext().getLocationContext().getLatLng().getLatitude();
		double lng = ContextManager.get().getCurrentContext().getLocationContext().getLatLng().getLongitude();
		GeoHash geo = GeoHash.withCharacterPrecision(lat, lng, ContextManager.GEOHASH_PRECISION);
		TimeOfWeek tow = new TimeOfWeek(timestamp);
		
		FileAccess fa = new FileAccess(fileUuid, geo, tow, nodeId);
		
		
		// insert fileAccess into local DB
		
		try {
			FileAccessDBHelper.insertFileAccess(fa);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		/* DEPRECATED ?
		// check if access for this file and Context().Location (as geohash) is already present in DB
		
		try {
			ArrayList<AccessLocation> alList = AccessLocationDBHelper.getAccessLocationsForFile(fileUuid);
			boolean alUpdated = false;
			
			for (AccessLocation al: alList) {
				if ( al.contains(geo.toBase32()) ) {
					
					// we have a match; update ToW and counter for this AccessLocation object
					TimeOfWeek meanToW = al.getMeanToW().calculateMeanTime(tow, al.getCount());
					al.setMeanToW(meanToW);
					al.increaseCount();
					
					// update record in DB
					AccessLocationDBHelper.updateAccessLocation(al);
					alUpdated = true;
				
				}
			}
			
			if (alUpdated == false) {
				// no AccessLocation was found for this file; create a new one
				AccessLocation al = new AccessLocation(IdentifierUtils.getNewUniqueIdentifier());
				
				al.setFileUuid(fileUuid);
				al.setGeohash(geo);
				al.setCount(1);
				al.setMeanToW(tow);
				al.setRadius(AccessLocation.CIRCLE_RADIUS);
								
				AccessLocationDBHelper.insertAccessLocation(al);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// TODO: --> "confidence level" for Location/TimeOfWeek combinations?
		*/
		
	}
	
}
