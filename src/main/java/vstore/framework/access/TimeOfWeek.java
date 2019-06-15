package vstore.framework.access;

import java.util.Calendar;

/**
 * Representation of points in time that a file is accessed or a location is visited.
 * Time representation is distinguished by minute, hour and day of week.
 *
 */
public class TimeOfWeek {
	
	/**
	 * Represents the day of the week from 0 to 6. 
	 * 0 for monday, 6 for sunday. 
	 */
	private int dayOfWeek;
	
	/**
	 * Represents the hour from 00 to 23.
	 */
	private int hour;
	
	/**
	 * Represents the minute from 00 to 59.
	 */
	private int minute;
	
	/**
	 * Creates a TimeOfWeek object for the current time.
	 */
	public TimeOfWeek() {		
		this( Calendar.getInstance() );
	}
	
	/**
	 * Creates a TimeOfWeek object from the given Calendar object
	 * @param c the calendar
	 */
	public TimeOfWeek(Calendar c) {
		
		this.dayOfWeek = getTransformedWeekday(c);
//		c.setFirstDayOfWeek(Calendar.MONDAY);
//		this.dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		this.hour = c.get(Calendar.HOUR_OF_DAY);
		this.minute = c.get(Calendar.MINUTE);
	}
	
	/**
	 * Creates a TimeOfWeek object from a given timestamp in milliseconds
	 * @param milliseconds the timestamp
	 */
	public TimeOfWeek(long milliseconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milliseconds);
		
		this.dayOfWeek = getTransformedWeekday(c);
//		c.setFirstDayOfWeek(Calendar.MONDAY);
//		this.dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		this.hour = c.get(Calendar.HOUR_OF_DAY);
		this.minute = c.get(Calendar.MINUTE);
	}
	
	/**
	 * Creates a TimeOfWeek object from a String representation in the form of dd-hh:mm
	 * @param tow 
	 */
	public TimeOfWeek(String tow) {
		String[] split = tow.split("-");
		
		int day = Integer.valueOf( split[0] );
		int hour = Integer.valueOf(split[1].split(":")[0]);
		int minute =  Integer.valueOf(split[1].split(":")[1]);
		
		this.setDay(day);
		this.setHour(hour);
		this.setMinute(minute);
	}
	
	/**
	 * Transforms the weekday representation of a calendar object from 1/sunday - 7/saturday 
	 * to 0/monday - 6/sunday 
	 * @param c the Calendar
	 * @return 0 for monday, 6 for sunday; remaining days respectively in between
	 */
	private static int getTransformedWeekday(Calendar c) {
		
		int wd = c.get(Calendar.DAY_OF_WEEK);
		if (wd == 1) wd = 6;
		else wd -= 2;
		
		return wd;
	}
	
	
	/**
	 * @return value between 0 (monday) and 6 (sunday)
	 */
	public int getDayOfWeek() {
		return this.dayOfWeek;
	}
	
	/**
	 * @return value between 0 and 23
	 */
	public int getHour() {
		return this.hour;
	}
	
	/**
	 * @return value between 0 and 59
	 */
	public int getMinute() {
		return this.minute;
	}
	
	/**
	 * Returns whether this TimeOfWeek is a working day or not
	 * @return true for monday to friday; false for weekend days (saturday + sunday)
	 */
	public boolean isWorkday() {		
		return (this.dayOfWeek < 6) ? true : false;
	}
	
	/**
	 * Calculates the weighted mean time between this object and the parameter object.  
	 * @param other the other TimeOfWeek object
	 * @param counter number of elements this mean value consists of (for weighting the mean time)
	 * @return the weighted mean time between this and other
	 */
	public TimeOfWeek calculateMeanTime(TimeOfWeek other, int counter) {
		
		TimeOfWeek earlier, latter;
		boolean swapped = false;
		
		// order by order of appearence during week
		if ( this.getTotalMinutes() < other.getTotalMinutes() ) {
			earlier = this;
			latter = other;
		} else {
			earlier = other;
			latter = this;
			swapped = true;
		}		
		
		int minuteDiff = 0;
		
		// get mininal distance in minutes between both objects
		if ( (latter.getTotalMinutes() - earlier.getTotalMinutes()) > 5040 ) {
			// go the other way round:
			int minutesForward = 0;
			for (int i = latter.getTotalMinutes(); i < latter.getTotalMinutes() + 5040; i++) {
				if (i % 10080 == earlier.getTotalMinutes()) {
					break;
				}
				minutesForward++;
			}
			
//			System.out.println("Minutes forward: " + minutesForward);
			minuteDiff = minutesForward * (-1);
		} else {
			minuteDiff = latter.getTotalMinutes() - earlier.getTotalMinutes() ;
		}
		
		double newTotalMinutes = 0;
		double counterDouble = (double) counter;
		if (swapped) {
			newTotalMinutes = (double) other.getTotalMinutes() + ( (double) minuteDiff / (counterDouble + 1) );
		} else {
			newTotalMinutes = (double) this.getTotalMinutes() + ((double) minuteDiff / (counterDouble + 1) );
		}
		
		if (newTotalMinutes < 0) {
			newTotalMinutes += 10080;	// add minutes of a whole week to change negative minute value to positive
		}
				
		int newDays = (int) newTotalMinutes / (24 * 60);
		int newHours = ( (int) newTotalMinutes - (newDays * 24 * 60) ) / 60;
		int newMinutes = (int) newTotalMinutes - newDays * 24 * 60 - newHours * 60;
		
		TimeOfWeek res = new TimeOfWeek();
		res.setDay(newDays);
		res.setHour(newHours);
		res.setMinute(newMinutes);
		
		return res;
		
	}
	
	/**
	 * 
	 * @param other
	 * @return the time difference in minutes between the two objects this and other
	 */
	public int getTimeDiff(TimeOfWeek other) {
		
		int thisMinutes = this.getTotalMinutes();
		int otherMinutes = other.getTotalMinutes();
		
		return thisMinutes - otherMinutes;
		
	}
	
	/**
	 * @return the total count of minutes into the week until this TimeOfWeek object
	 */
	private int getTotalMinutes() {
		return dayOfWeek * 24 * 60 + hour * 60 + minute;
	}
	
	private void setDay(int day) {
		this.dayOfWeek = day % 7;
	}
	private void setHour(int hour) {
		this.hour = hour % 24;
	}
	private void setMinute(int minute) {
		this.minute = minute % 60;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		
		if (obj instanceof TimeOfWeek) {
			TimeOfWeek t = (TimeOfWeek) obj;
			
			// compare object values
			if (this.dayOfWeek == t.getDayOfWeek() &&
					this.minute == t.getMinute() &&
					this.hour == t.getHour() ) {
				return true;
			}
		}		
		
		return false;
	}
	
	/**
	 * returns a textual representation of the object in the format 'd-hh:mm'
	 */
	@Override
	public String toString() {
		return this.dayOfWeek + "-" + this.hour + ":" + this.minute;
	}

}