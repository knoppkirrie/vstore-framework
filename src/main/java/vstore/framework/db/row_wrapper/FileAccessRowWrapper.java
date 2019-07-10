package vstore.framework.db.row_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ch.hsr.geohash.GeoHash;
import vstore.framework.access.FileAccess;
import vstore.framework.access.TimeOfWeek;
import vstore.framework.db.DBSchema;

public class FileAccessRowWrapper {

	private ResultSet rs;
	
	public FileAccessRowWrapper(ResultSet rs) {
		this.rs = rs;
	}
	
	public FileAccess getFileAccess() {
		
		FileAccess fa = null;
		try {
			String id = rs.getString(DBSchema.FileAccessTable.ID);
			String file = rs.getString(DBSchema.FileAccessTable.FILE);
			String geohashString = rs.getString(DBSchema.FileAccessTable.GEOHASH);
			String towString = rs.getString(DBSchema.FileAccessTable.TIMEOFWEEK);
//			int totalMinutes = rs.getInt(DBSchema.FileAccessTable.TOTALMINUTES);
			String node = rs.getString(DBSchema.FileAccessTable.NODE_ID);
			
			TimeOfWeek tow = new TimeOfWeek(towString);
			GeoHash geo = GeoHash.fromGeohashString(geohashString);
			
			fa = new FileAccess(file, geo, tow, node);
			fa.setUuid(id);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fa;
		
	}
	
}
