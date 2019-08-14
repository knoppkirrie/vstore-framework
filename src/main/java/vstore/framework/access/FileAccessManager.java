package vstore.framework.access;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import ch.hsr.geohash.GeoHash;
import vstore.framework.communication.access.FileAccessUploader;
import vstore.framework.context.ContextManager;
import vstore.framework.db.table_helper.FileAccessDBHelper;

public class FileAccessManager {

	private static FileAccessManager instance;
	
	/** Interval in minutes when to upload new FileAccess objects from this device. */
	private static final int FILE_ACCESS_UPLOAD_INTERVAL = 1440;
	
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
		
		FileAccess fa = new FileAccess(fileUuid, geo, timestamp, nodeId);
		
		
		// insert fileAccess into local DB
		try {
			FileAccessDBHelper.insertFileAccess(fa);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Schedules a TimerTask that uploads FileAccesses that have been detected on this device to the storage nodes.
	 */
	public void initializeFileAccessUploading() {
		Timer t = new Timer();
		FileAccessUploadTask upload = instance.new FileAccessUploadTask();
		t.scheduleAtFixedRate(upload, 0, 1000 * 60 * FILE_ACCESS_UPLOAD_INTERVAL );	// transforming from min to ms 
	}
	
	/**
	 * Initializes uploading all new FileAccess objects to the respective storage nodes when running.
	 */
	class FileAccessUploadTask extends TimerTask {
    	
		@Override
		public void run() {
		
			getFileAccessUploader().uploadFileAccesses();

		}
    }
	
}
