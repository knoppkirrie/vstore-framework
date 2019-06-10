package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ch.hsr.geohash.GeoHash;
import vstore.framework.access.TimeOfWeek;
import vstore.framework.context.ContextManager;
import vstore.framework.context.types.location.VLocation;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.file.VStoreFile;
import vstore.framework.utils.IdentifierUtils;

public class FileAccessDBHelper {

	/**
	 * Inserts a FileAccess object into the database with the current TimeOfWeek parameter
	 * @param file the VStoreFile that has been accessed
	 */
	public static void insertFileAccess(String fileUuid, VLocation loc, String type) throws SQLException {
		
		String sql = "INSERT INTO "
				+ DBSchema.FileAccess.__NAME + " " 
				+ "("
				+ DBSchema.FileAccess.ID + ", "
				+ DBSchema.FileAccess.FILE + ", "
				+ DBSchema.FileAccess.GEOHASH + ", "
				+ DBSchema.FileAccess.TIMEOFWEEK + ", "
				+ DBSchema.FileAccess.TYPE + ")"
				+ "VALUES (?,?,?,?,?)";
		
		String geohashString = "";
		if (loc != null) {
			double lat = loc.getLatLng().getLatitude();
			double lng = loc.getLatLng().getLongitude();
			GeoHash geohash = GeoHash.withCharacterPrecision(lat, lng, 12);
			
			geohashString = geohash.toBase32();
		}
		
		
		TimeOfWeek tow = new TimeOfWeek();
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setString(1, IdentifierUtils.getNewUniqueIdentifier());
			pstmt.setString(2, fileUuid);
			pstmt.setString(3, geohashString);
			pstmt.setString(4, tow.toString());
			pstmt.setString(5, type);
			
			pstmt.execute();
            pstmt.close();
		}
		
	}
	
}
