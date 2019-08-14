package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vstore.framework.access.FileAccess;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.db.row_wrapper.FileAccessRowWrapper;

public class FileAccessDBHelper {

	/**
	 * Inserts a FileAccess object into the database
	 * @param fa the FileAccess object
	 */
	public static void insertFileAccess(FileAccess fa) throws SQLException {
		
		String sql = "INSERT INTO "
				+ DBSchema.FileAccessTable.__NAME + " ("
				+ DBSchema.FileAccessTable.ID + ", "
				+ DBSchema.FileAccessTable.FILE + ", "
				+ DBSchema.FileAccessTable.GEOHASH + ", "
				+ DBSchema.FileAccessTable.TIMESTAMP + ", "
				+ DBSchema.FileAccessTable.IS_UPLOADED + ", "
				+ DBSchema.FileAccessTable.NODE_ID + ")" 
				+ " VALUES (?,?,?,?,?,?)"; 
		
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setString(1, fa.getUuid());
			pstmt.setString(2, fa.getFileUuid());
			pstmt.setString(3, fa.getGeohash().toBase32());
			pstmt.setLong(4, fa.getTimestamp());
			pstmt.setInt(5, 0);
			pstmt.setString(6, fa.getNodeId());
			
			pstmt.execute();
            pstmt.close();
		}
		
	}
	
	/**
	 * @return all FileAccess objects from local DB that have not been uploaded yet
	 * @throws SQLException
	 */
	public static List<FileAccess> getNotUploadedFileAccesses() throws SQLException {
		List<FileAccess> res = new ArrayList<FileAccess>();
		
		String sql = "SELECT * FROM " + DBSchema.FileAccessTable.__NAME + " WHERE "
				+ DBSchema.FileAccessTable.IS_UPLOADED + " = ?"
				+ "ORDER BY " + DBSchema.FileAccessTable.NODE_ID;
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setInt(1, 0);
			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				FileAccessRowWrapper farw = new FileAccessRowWrapper(rs);
				res.add( farw.getFileAccess() );
			}
			
		}
		
		return res;
	}
	
	/**
	 * Reads the NodeId for a file from the database
	 * @param uuid the uuid of the file
	 * @return the uuid of the node where the file is stored
	 * @throws SQLException
	 */
	public static String getNodeForFile(String uuid) throws SQLException {
		String res = null;
		
		String sql = "SELECT " + DBSchema.FilesTable.NODEUUID + " FROM "
				+ DBSchema.FilesTable.__NAME + " WHERE " 
				+ DBSchema.FilesTable.UUID + " = ?"; 
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setString(1, uuid);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				res = rs.getString(DBSchema.FilesTable.NODEUUID);
			}
			
		}
		
		return res;
	}
	
	/**
	 * Updates all FileAccess objects of a given list as "uploaded" in the local database 
	 * @param faList the list of FileAccess objects
	 * @throws SQLException
	 */
	public static void setAsUploaded(List<FileAccess> faList) throws SQLException {
		
		if (faList == null || faList.size() == 0) return;
		
		String sql = "UPDATE " + DBSchema.FileAccessTable.__NAME 
				+ " SET " + DBSchema.FileAccessTable.IS_UPLOADED + " = 1"
				+ " WHERE " + DBSchema.FileAccessTable.ID + " = ";
		
		for (int i = 0; i < faList.size(); i++) {
			sql += "?";
			if (i < faList.size() - 1) {
				sql += " OR " + DBSchema.FileAccessTable.ID + " = ";
			}
		}
				
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
//			pstmt.setInt(1, 1);
			
			for (int i = 0; i < faList.size(); i++) {
				pstmt.setString(i+1, faList.get(i).getUuid());
			}
			
			pstmt.execute();
			pstmt.close();
		}
		
	}
	
}
