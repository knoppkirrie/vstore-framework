package vstore.framework.access;

public class FileAccessManager {

	private static FileAccessManager instance;
	
	private FileAccessManager() {}
	
	public static synchronized void initialize() {
		if (instance == null) {
			instance = new FileAccessManager();
		}
	}
	
	/**
	 * @return Gets the instance of the FileAccessManager
	 */
	public static FileAccessManager get() {
		if(instance == null)
        {
            initialize();
        }
        return instance;
	}
	
	private void updateMeanAccessTime(String fileUuid, long timestamp) {
		// TODO
	}
	
	public TimeOfWeek getMeanAccessTime(String fileUuid) {
		// TODO
		return null;
	}
	
	public void newAccess(String fileUuid, long timestamp) {
		// TODO
	}
	
}
