package vstore.framework.context;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import vstore.framework.db.table_helper.PositionTrackingDBHelper;
import vstore.framework.exceptions.DatabaseException;

public class ContextManager {
    /**
     * This field contains the current context description.
     */
    private static ContextDescription mCurrentContext;

    private static ContextManager instance;

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
    
    public void initializePositionTracking() {
    	Timer t = new Timer();
        TrackingTask tracker = instance.new TrackingTask();
        t.scheduleAtFixedRate(tracker, 0, 1000 * 60 * 10);	// every 10 minutes
    }

    /**
     * Is responsible for tracking the device's location and save it into the DB
     * 
     */
    class TrackingTask extends TimerTask {

		@Override
		public void run() {
			// Put position with timestamp into DB
			try {
				PositionTrackingDBHelper.insertLocation( ContextManager.get().getCurrentContext().getLocationContext() );
//				System.out.println("Location stored.");
			} catch (SQLException e) {
				System.out.println("Location storing failed:");
				e.printStackTrace();
			} catch (DatabaseException e) {
//				e.printStackTrace();
//				System.out.println("Location was null... nothing stored.");
			}
		}
    }
    
}
