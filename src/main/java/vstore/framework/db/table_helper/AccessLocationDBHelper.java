package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ch.hsr.geohash.GeoHash;
import vstore.framework.access.AccessLocation;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.db.row_wrapper.AccessLocationRowWrapper;

public class AccessLocationDBHelper {
	
	/** The number of characters to compare two GeoHashes with each other */
	public static final int GEOHASH_MATCHING_CHARACTER_PRECISION = 4;

	public static void insertAccessLocation(AccessLocation al) throws SQLException {
		
		String sql = "INSERT INTO " + DBSchema.AccessLocationTable.__NAME + " ("
				+ DBSchema.AccessLocationTable.ID + ", "
				+ DBSchema.AccessLocationTable.FILE + ", "
				+ DBSchema.AccessLocationTable.GEOHASH + ", "
				+ DBSchema.AccessLocationTable.MEAN_TOW + ", "
				+ DBSchema.AccessLocationTable.COUNT + ", "
				+ DBSchema.AccessLocationTable.RADIUS + ") "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setString(1, al.getId());
			pstmt.setString(2, al.getFileUuid());
			pstmt.setString(3, al.geohashString());
			pstmt.setString(4, al.getMeanToW().toString());
			pstmt.setInt(5, al.getCount());
			pstmt.setInt(6, al.getRadius());
			
			pstmt.execute();
			pstmt.close();		
			
		}
		
		
	}
	
	public static void updateAccessLocation(AccessLocation al) throws SQLException {
		
		String sql = "UPDATE " + DBSchema.AccessLocationTable.__NAME + " SET " 
				+ DBSchema.AccessLocationTable.FILE + " = ?, "
				+ DBSchema.AccessLocationTable.GEOHASH + " = ?, "
				+ DBSchema.AccessLocationTable.RADIUS + " = ?, "
				+ DBSchema.AccessLocationTable.MEAN_TOW + " = ?, "
				+ DBSchema.AccessLocationTable.COUNT + " = ? "
				+ "WHERE " + DBSchema.AccessLocationTable.ID + " = ?";
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, al.getFileUuid());
            pstmt.setString(2, al.geohashString());
            pstmt.setInt(3, al.getRadius());
            pstmt.setString(4, al.getMeanToW().toString());
            pstmt.setInt(5, al.getCount());
            
            pstmt.setString(6, al.getId());
            
            pstmt.executeUpdate();
            pstmt.close();
		}
		
		
	}
	
	public static ArrayList<AccessLocation> getAccessLocationsForFile(String fileUuid) throws SQLException {
		
		// TODO: test
		
		String sql = "SELECT * FROM "
				+ DBSchema.AccessLocationTable.__NAME
				+ " WHERE " + DBSchema.AccessLocationTable.FILE + " = ? ";
		
		ArrayList<AccessLocation> result = new ArrayList<AccessLocation>();
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {

            pstmt.setString(1, fileUuid);
            ResultSet rs  = pstmt.executeQuery();
			
            while (rs.next()) {
            	AccessLocationRowWrapper alrw = new AccessLocationRowWrapper(rs);
            	result.add( alrw.getAccessLocation() );
            }
            
            pstmt.close();
		
		}
		
		return result;
	}
	
	/**
	 * Gets AccessLocations from DB that match the GeoHash value of the parameter
	 * @param geo the GeoHash to match against
	 * @return a list of all matching AccessLocations from the database
	 * @throws SQLException
	 */
	public static ArrayList<AccessLocation> getAccessLocationsForLocation(GeoHash geo) throws SQLException {
		ArrayList<AccessLocation> res = new ArrayList<AccessLocation>();
		if (geo == null) return res;
		
		String geoString = geo.toBase32().substring(0, GEOHASH_MATCHING_CHARACTER_PRECISION);

		String sql = "SELECT * FROM " + DBSchema.AccessLocationTable.__NAME
				+ " WHERE "
				+ DBSchema.AccessLocationTable.GEOHASH + " LIKE ? ";
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			
			pstmt.setString(1, geoString + "%");
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
            	AccessLocationRowWrapper alrw = new AccessLocationRowWrapper(rs);
            	res.add( alrw.getAccessLocation() );
            }
			pstmt.close();
			
		}
		
		return res;
	}
	
	public static String getFileIdForAccessLocation(String accessLocationId) throws SQLException{

		String res = "";
		
		String sql = "SELECT " + DBSchema.AccessLocationTable.FILE + " FROM " 
				+ DBSchema.AccessLocationTable.__NAME 
				+ " WHERE "
				+ DBSchema.AccessLocationTable.ID + " = ? ";
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			
			pstmt.setString(1, accessLocationId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				res = rs.getString(DBSchema.AccessLocationTable.FILE);
			}
			
		}
		
		
		return res;
	}
	
	
	
	
	
	
	
	
	
}
