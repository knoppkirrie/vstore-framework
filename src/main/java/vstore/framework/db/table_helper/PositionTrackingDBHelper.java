package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vstore.framework.context.types.location.VLocation;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.db.row_wrapper.FileRowWrapper;
import vstore.framework.error.ErrorMessages;
import vstore.framework.exceptions.DatabaseException;
import vstore.framework.utils.IdentifierUtils;

import ch.hsr.geohash.*;

public class PositionTrackingDBHelper {

	/**
	 * Inserts a VLocation object into the PositionTracking database.
	 * @param loc the VLocation to store 
	 * @throws DatabaseException 
	 */
	public static void insertLocation(VLocation loc) throws SQLException, DatabaseException {
		
		if (loc == null) throw new DatabaseException(ErrorMessages.PARAMETERS_MUST_NOT_BE_NULL);
		
		String sql = "INSERT INTO "
				+ DBSchema.PositionTrackingTable.__NAME + " "
				+ "("
				+ DBSchema.PositionTrackingTable.ID + ", "
				+ DBSchema.PositionTrackingTable.LAT + ", "
				+ DBSchema.PositionTrackingTable.LNG + ", "
				+ DBSchema.PositionTrackingTable.TIMESTAMP + ", "
				+ DBSchema.PositionTrackingTable.GEOHASH + ")"
				+ "VALUES (?,?,?,?,?)";
		
		double lat = loc.getLatLng().getLatitude();
		double lng = loc.getLatLng().getLongitude();
		
		GeoHash geohash = GeoHash.withCharacterPrecision(lat, lng, 12);
//		System.out.println("SHOULD BE TRUE: " + geohash.getBoundingBox().contains(geohash.getPoint()));
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, IdentifierUtils.getNewUniqueIdentifier());
            pstmt.setDouble(2, lat);
            pstmt.setDouble(3, lng);
            pstmt.setLong(4, loc.getTimestamp());
            pstmt.setString(5, geohash.toBase32());

            pstmt.execute();
            pstmt.close();
        }
		
	}
	
	public static void getPositions() throws SQLException {
		String sql = "SELECT * FROM " + DBSchema.PositionTrackingTable.__NAME ;
	
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
//            pstmt.setInt(1, 0);
            ResultSet rs = null;
            try {
                rs = pstmt.executeQuery();
            }
            catch(NullPointerException e) {
                e.printStackTrace();
//                return files;
            }
            while (rs.next())
            {
            	System.out.print("Row: ");
            	System.out.print( rs.getString(DBSchema.PositionTrackingTable.ID) + "; " );
            	System.out.print( rs.getString(DBSchema.PositionTrackingTable.LAT) + "; " );
            	System.out.print( rs.getString(DBSchema.PositionTrackingTable.LNG) + "; " );
            	System.out.print( rs.getString(DBSchema.PositionTrackingTable.GEOHASH) + "; " );
            	System.out.println( rs.getString(DBSchema.PositionTrackingTable.TIMESTAMP) + "; " );
//                FileRowWrapper wrp = new FileRowWrapper(rs);
//                files.add(wrp.getFile());
            	
            	
            }
            pstmt.close();

//            return files;
        }
	
	}
	
}
