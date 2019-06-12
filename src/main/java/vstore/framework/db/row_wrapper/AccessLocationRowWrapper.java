package vstore.framework.db.row_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import vstore.framework.access.AccessLocation;
import vstore.framework.db.DBSchema;

public class AccessLocationRowWrapper {

	private ResultSet res; 
	
	public AccessLocationRowWrapper(ResultSet rs) {
		res = rs;
	}
	
	public AccessLocation getAccessLocation() {
		
		AccessLocation al = null;
		try {
			String id = res.getString(DBSchema.AccessLocationTable.ID);
			al = new AccessLocation(id);
			
			String geohashString = res.getString(DBSchema.AccessLocationTable.GEOHASH);
			int radius = res.getInt(DBSchema.AccessLocationTable.RADIUS);
			String fileUuid = res.getString(DBSchema.AccessLocationTable.FILE);
			int count = res.getInt(DBSchema.AccessLocationTable.COUNT);
			String meanToW = res.getString(DBSchema.AccessLocationTable.MEAN_TOW);
			
			al.setGeohash(geohashString);
			al.setRadius(radius);
			al.setFileUuid(fileUuid);
			al.setCount(count);
			al.setMeanToW(meanToW);			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return al;
	}
	
}
