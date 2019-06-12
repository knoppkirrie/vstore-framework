package vstore.framework.db.table_helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vstore.framework.access.AccessLocation;
import vstore.framework.db.DBHelper;
import vstore.framework.db.DBSchema;
import vstore.framework.db.row_wrapper.AccessLocationRowWrapper;

public class AccessLocationDBHelper {

	public void insertAccessLocation(AccessLocation al) {
		// TODO
	}
	
	public ArrayList<AccessLocation> getAccessLocationsForFile(String fileUuid) throws SQLException {
		
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
}
