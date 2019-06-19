package vstore.framework.db.row_wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import vstore.framework.context.PositionTracking.PositionTrackingPoint;
import vstore.framework.db.DBSchema;

public class PositionTrackingRowWrapper {
	
	private ResultSet res; 
	
	public PositionTrackingRowWrapper(ResultSet rs) {
		res = rs;
	}
	
	public PositionTrackingPoint getPositionTrackingPoint() {
		
		PositionTrackingPoint p = null;
		try {
			String id = res.getString(DBSchema.PositionTrackingTable.ID);
			p = new PositionTrackingPoint(id);
			
			p.setGeoHash( res.getString(DBSchema.PositionTrackingTable.GEOHASH) );
			p.setTimestamp( res.getInt(DBSchema.PositionTrackingTable.TIMESTAMP) );
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return p;
		
	}
}
