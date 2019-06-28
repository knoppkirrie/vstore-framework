package vstore.framework.context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ch.hsr.geohash.GeoHash;
import vstore.framework.VStore;
import vstore.framework.access.AccessLocation;
import vstore.framework.access.TimeOfWeek;
import vstore.framework.context.types.location.VLocation;
import vstore.framework.db.table_helper.AccessLocationDBHelper;

public class ContextManager {
    /**
     * This field contains the current context description.
     */
    private static ContextDescription mCurrentContext;

    private static ContextManager instance;

    public static final int TRACKING_INTERVAL = 10;
    public static final int TRACK_LENGTH = 5;
    public static final int GEOHASH_PRECISION = 12;
    public static final double REPLICATION_SCORE_THRESHOLD = 0.8;
    
    private ContextManager() {}

    /**
     * Initializes the context manager by reading the context data
     * from the persistent file, if available.
     */
    public static synchronized void initialize() {
        if(instance == null)
        {
            instance = new ContextManager();
        }

        //Initialize the current context description
        mCurrentContext = new ContextDescription();
        //Check if we have persistent context in the context file
        ContextDescription tmpCtx = ContextFile.getContext();
        if(tmpCtx != null)
        {
            mCurrentContext = tmpCtx;
        }
    }

    /**
     * @return Gets the instance of the context manager.
     */
    public static ContextManager get() {
        if(instance == null)
        {
            initialize();
        }
        return instance;
    }

    /**
     * Use this method to provide new context information to the framework.
     * If the new information should be persistent after a restart of the
     * framework, {@link ContextManager#persistContext(boolean)} should be called.
     *
     * @param context The new context information
     *
     * @return Returns the context manager instance again to simplify method-chaining.
     */
    public ContextManager provideContext(ContextDescription context) {
        mCurrentContext = context;
        return this;
    }

    /**
     * Use this method to make the currently configured usage context
     * persistent after a restart of the framework.
     *
     * @param makePersistent True if the context should be persistent.
     *                       False if you want to undo this.
     *
     * @return Returns the context manager instance again to simplify method-chaining.
     */
    public ContextManager persistContext(boolean makePersistent) {
        if(makePersistent && mCurrentContext != null)
        {
            ContextFile.write(mCurrentContext.getJson());
            return this;
        }
        if(!makePersistent)
        {
            ContextFile.clearContext();
        }
        return this;
    }


    /**
     * This method clears the current usage context and resets it
     * to an empty state.
     *
     * @return Returns the context manager instance again to simplify method-chaining.
     */
    public ContextManager clearCurrentContext() {
        mCurrentContext = new ContextDescription();
        ContextFile.clearContext();
        return this;
    }

    /**
     * This method returns the usage context currently used for matching.
     * If you want to refresh it, use {@link ContextManager#provideContext(ContextDescription)}
     *
     * @return A ContextDescription-object containing the current usage
     *         context description
     */
    public final ContextDescription getCurrentContext() {
        return mCurrentContext;
    }
    
    /**
     * Calculates a probability score for the current VLocation that suggests if a FileAccess may be upcoming or not 
     * @param loc the current VLocation.
     * @param tow the TimeOfWeek object of the location measurement
     * @return a Map<String, Double> with AccessLocation.Id as Key, the score as value 
     */
    public HashMap<String, Double> getPositionScores(VLocation loc, TimeOfWeek tow) {
  
    	HashMap<String, Double> scoreMap = new HashMap<String, Double>();
    	
    	if (loc == null) return scoreMap;
    	
    	double lat = loc.getLatLng().getLatitude();
    	double lng = loc.getLatLng().getLongitude();
    	GeoHash geo = GeoHash.withCharacterPrecision(lat, lng, ContextManager.GEOHASH_PRECISION);
    	
//    	TimeOfWeek now = new TimeOfWeek();
    	
    	
    	try {
    		
			ArrayList<AccessLocation> alList = AccessLocationDBHelper.getAccessLocationsForLocation(geo);
		
			for (AccessLocation al: alList) {
				
				if (al.contains(geo.toBase32()) && Math.abs( al.getMeanToW().getMinuteDiff(tow) ) < AccessLocation.TIME_THRESHOLD) {
					// location is near known AccessLocation and within timeThreshold window
					double meters = al.getDistance(geo);
					int minutes = al.getMeanToW().getMinuteDiff(tow);
					
					double score = ( (meters / AccessLocation.CIRCLE_RADIUS) + ( (float) Math.abs(minutes) / AccessLocation.TIME_THRESHOLD) ) / 2;
					
					// TODO: penalty if meanToW has already passed?
					
					scoreMap.put(al.getId(), (1 - score) );
					
				}
			}

    	} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return scoreMap;
    }
    
    public void initializePositionTracking() {
    	
    	Timer t = new Timer();
        TrackingTask tracker = instance.new TrackingTask();
        t.scheduleAtFixedRate(tracker, 1000, /* 1000 * 60 * ContextManager.TRACKING_INTERVAL */ 1000);	// transform minutes to milliseconds
        
        System.out.println("Timer scheduled");
        
        
    }

    /**
     * Is responsible for tracking the device's location and save it into the DB
     * 
     */
    class TrackingTask extends TimerTask {
    	
		@Override
		public void run() {
		
			// TODO: when running as separate Thread, loc always is null 
			// --> first time works, from then on loc == null
		
			VLocation loc = ContextManager.get().getCurrentContext().getLocationContext();
			
			if (loc == null) {
				System.out.println("LOCATION NULL -- ABORT");
				return;
			}

			TimeOfWeek tow = new TimeOfWeek( loc.getTimestamp() ); // TimeOfWeek of location measurement
			
			// calculate score for this location:
			HashMap<String, Double> scoreMap = ContextManager.get().getPositionScores(loc, tow);
			
			// go through Map and remove all AccessLocations below threshold
			System.out.println("HashMap size before sorting out: " + scoreMap.size());
			
			Iterator it = scoreMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Double> pair = (Map.Entry<String, Double>)it.next();
		        
		        if (pair.getValue() < REPLICATION_SCORE_THRESHOLD) {
		        	it.remove();
		        	System.out.println("Entry removed (Id: " + pair.getKey() + ")");
		        }

		        System.out.println(pair.getKey() + " = " + pair.getValue());
		    }
			
		    System.out.println("HashMap size after sorting out: " + scoreMap.size());

		    if (scoreMap.size() == 0) return;	// no matching accessLocations are being approached

		    it = scoreMap.entrySet().iterator();
		    while (it.hasNext()) {
		    	Map.Entry<String, Double> pair = (Map.Entry<String, Double>)it.next();
		    	String file = "";
		    	
		    	try {
					file = AccessLocationDBHelper.getFileIdForAccessLocation(pair.getKey());
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	
		    	if (VStore.getInstance().getFileManager().isMyFile(file) ) return;	// user owns this file; no replication needed
		    	
		    	// TODO: trigger replication 
		    }
		    
		   
			// TODO: detecting AccessLocations not only for own requested files, but also from others?

		}
    }
    
}
