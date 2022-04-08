package com.hhoss.jour;

import com.hhoss.lang.Lang3StopWatch;

//also see org.springframework.util.StopWatch;org.perf4j.LoggingStopWatch
/*
 * 
 * org.apache.commons.lang.time 包括以下几个类：
a. DateFormatUtils        【格式化Calendar与Date并依赖于 FastDateFormat】
b. DateUtils               【围绕Calendar与Date的实用方法】
c. DurationFormatUtils 【毫秒数格式化Calendar与Date】
d. FastDateFormat        【线程安全的SimpleDateFormat】
e. StopWatch               【提供一个方便的定时的API 】
extends from Lang3StopWatch = org.apache.commons.lang3.time.StopWatch
 */
public class StopWatch extends Lang3StopWatch {
    private String label;
    private String message;
    public StopWatch() {
    	this.label=Long.toString(System.currentTimeMillis());
    }
    
    /**
     * Creates a StopWatch with the specified label, no message and started at the instant of creation.
     *
     * @param label The label name for this timing call. Tags are used to group timing logs, thus each block of code being
     *            timed should have a unique label. Note that tags can take a hierarchical format using dot notation.
     */
    public StopWatch(String label) {
        this.label=label;
        this.start();
    }

    /**
     * Creates a StopWatch with the specified label and message, started an the instant of creation.
     *
     * @param label     The label name for this timing call. Tags are used to group timing logs, thus each block of code
     *                being timed should have a unique label. Note that tags can take a hierarchical format using dot
     *                notation.
     * @param message Additional text to be printed with the logging statement of this StopWatch.
     */
    public StopWatch(String label, String message) {
        this.label=label;
        this.message=message;
        this.start();
    }
	
    public StopWatch(String label, boolean runStart) {
        this.label=label;
        this.start();
    }
    
    /**
     * Gets the label used to group this StopWatch instance with other instances used to time the same code block.
     *
     * @return The grouping label.
     */
    public String getLabel() { return label; }

    /**
     * Sets the grouping label for this StopWatch instance.
     *
     * @param label The grouping label.
     * @return this instance, for method chaining if desired
     */
    public StopWatch setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * Gets any additional message that was set on this StopWatch instance.
     *
     * @return The message associated with this StopWatch, which may be null.
     */
    public String getMessage() { return message; }

    /**
     * Sends a message on this StopWatch instance to be printed when this instance is logged.
     *
     * @param message The message associated with this StopWatch, which may be null.
     * @return this instance, for method chaining if desired.
     */
    public StopWatch setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public String reset(String label){
    	super.reset();
        this.label=label;
        this.start();
        return toString();
    }
	
    /**
     * Stops this StopWatch and sets its grouping label.
     *
     * @param label The grouping label for this StopWatch
     * @return this.toString(), which is a message suitable for logging
     */
    public String stop(String label) {
        this.label = label;
        return toString();
    }

    /**
     * Stops this StopWatch and sets its grouping label and message.
     *
     * @param label     The grouping label for this StopWatch
     * @param message A descriptive message about the code being timed, may be null
     * @return this.toString(), which is a message suitable for logging
     */
    public String stop(String label, String message) {
        this.label = label;
        this.message = message;
        return toString();
    }
    /**
     * The lap method is useful when using a single StopWatch to time multiple consecutive blocks. It calls stop()
     * and then immediately calls start(), e.g.:
     * <p/>
     * <pre>
     * StopWatch stopWatch = new StopWatch();
     * ...some code
     * log.info(stopWatch.lap("block1"));
     * ...some more code
     * log.info(stopWatch.lap("block2"));
     * ...even more code
     * log.info(stopWatch.stop("block3"));
     * </pre>
     *
     * @param label The grouping label to use for the execution block that was just stopped.
     * @return A message suitable for logging the previous execution block's execution time.
     */
    public String lap(String label) {
        String retVal = stop(label);
        start();
        return retVal;
    }

    /**
     * The lap method is useful when using a single StopWatch to time multiple consecutive blocks. It calls stop()
     * and then immediately calls start(), e.g.:
     * <p/>
     * <pre>
     * StopWatch stopWatch = new StopWatch();
     * ...some code
     * log.info(stopWatch.lap("block1", "message about block 1"));
     * ...some more code
     * log.info(stopWatch.lap("block2", "message about block 2"));
     * ...even more code
     * log.info(stopWatch.stop("block3", "message about block 3"));
     * </pre>
     *
     * @param label     The grouping label to use for the execution block that was just stopped.
     * @param message A descriptive message about the code being timed, may be null
     * @return A message suitable for logging the previous execution block's execution time.
     */
    public String lap(String label, String message) {
        String retVal = stop(label, message);
        start();
        return retVal;
    }

    
    public String toString() {
    	String message = getMessage();
    	return "timer[" + getStartTime() +
        "] spent[" + getTime() +
        "]ms, label[" + getLabel() +
        ((message == null) ? "]" : "],message[" + message + "]");
    }

	/**
	 * Return a short description of the total running time.
	 */
	public String shortSummary() {
		return "StopWatch '" + this.label + "': running time (millis) = " + getTime();
	}
	   
	public String prettyPrint() {
		return shortSummary();
	}

    
}
