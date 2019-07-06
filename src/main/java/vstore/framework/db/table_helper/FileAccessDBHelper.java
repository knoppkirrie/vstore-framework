package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import vstore.framework.access.FileAccess;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.utils.IdentifierUtils;

public class FileAccessDBHelper {

	/**
	 * Inserts a FileAccess object into the database with the current TimeOfWeek parameter
	 * @param file the VStoreFile that has been accessed
	 */
	public static void insertFileAccess(FileAccess fa) throws SQLException {
		
		String sql = "INSERT INTO "
				+ DBSchema.FileAccessTable.__NAME + " ("
				+ DBSchema.FileAccessTable.ID + ", "
				+ DBSchema.FileAccessTable.FILE + ", "
				+ DBSchema.FileAccessTable.GEOHASH + ", "
				+ DBSchema.FileAccessTable.TIMEOFWEEK + ", "
				+ DBSchema.FileAccessTable.TOTALMINUTES + ")"
				+ " VALUES (?,?,?,?,?)";
		
		
		try(PreparedStatement pstmt = DBHelper.get().getConnection().prepareStatement(sql)) {
			pstmt.setString(1, IdentifierUtils.getNewUniqueIdentifier());
			pstmt.setString(2, fa.getFileUuid());
			pstmt.setString(3, fa.getGeohash().toBase32());
			pstmt.setString(4, fa.getTimeOfWeek().toString());
			pstmt.setInt(5, fa.getTimeOfWeek().getTotalMinutes());
			
			pstmt.execute();
            pstmt.close();
		}
		
	}
	
}
