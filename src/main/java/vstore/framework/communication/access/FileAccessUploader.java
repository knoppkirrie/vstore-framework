package vstore.framework.communication.access;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vstore.framework.access.FileAccess;
import vstore.framework.db.table_helper.FileAccessDBHelper;

public class FileAccessUploader {

	private static FileAccessUploader instance;
	
	private FileAccessUploader() {}
	
	public static FileAccessUploader get() {
		if (instance == null) {
			instance = new FileAccessUploader();
		}
		return instance;
	}

	/**
	 * Gets all FileAccess objects from local DB that have not been uploaded yet and sends them to the corresponding
	 * storage nodes.
	 * @return
	 */
	public void uploadFileAccesses() {
		
		try {
			List<FileAccess> faList = FileAccessDBHelper.getNotUploadedFileAccesses();
			
			// split faList into sub lists, sorted by storage nodes
			List<List<FileAccess>> listOfLists = new ArrayList<List<FileAccess>>();
			String lastNode = "";
			List<FileAccess> subList = null;
			for (FileAccess fa: faList) {
				
				if (fa.getNodeId().equals(lastNode)) {
					subList.add(fa);
				} else {
					if (subList != null && subList.size() > 0) {
						listOfLists.add(subList);
					}
										
					subList = new ArrayList<FileAccess>();
					subList.add(fa);
				}
				
				lastNode = fa.getNodeId();
			}
			
			// add last subList
			if (subList != null && subList.size() > 0) {
				listOfLists.add(subList);
			}
			
			for(List<FileAccess> fal: listOfLists) {
				// send to InsertAccessThread
				InsertAccessThread iat = new InsertAccessThread(fal);
				iat.start();
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
}
