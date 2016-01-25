package exercise1.part3;

import java.util.ArrayList;

import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.util.Delay;
import rp.config.RangeFinderDescription;
import rp.robotics.EventBasedTouchSensor;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.util.Rate;



public class WallSensor implements EventBasedTouchSensor, Runnable {

	/*
	 * The left-facing ultrasonic sensor attached to the robot.
	 * This won't change, so we set it to final.
	 */
	private final UltrasonicSensor sensor;
 
	/*
	 * An ArrayList containing the listeners attached to the sensor.
	 */
	private ArrayList<TouchSensorListener> listeners;
	
	/*
	 * The maximum distance a wall can be from the robot 
	 * before the robot makes a left turn.
	 */
	private float wallRange;
	
	/*
	 * The thread that the run method runs on.
	 */
	private Thread mainThread;
	
	/*
	 * Is the WallSensor running? 
	 * (Set to false initially, but set to true once the run method is called).
	 */
	private boolean isRunning = false;
	
	/*
	 * Is there a wall within wallRange?
	 */
	private boolean wallInRange = false;

	/*
	 * The amount of time (in milliseconds) that delays should last.
	 */
	private long msDelayAmount = 40;
	
	/**
	 * Create a new instance of the WallSensor class.
	 * @param wallRange the maximum distance a wall can be from the robot before the robot makes a left turn.
	 * @param sensor the UltrasonicSensor attached to the left of the robot.
	 */
	public WallSensor(float wallRange, 
			          UltrasonicSensor sensor) {

		this.wallRange = wallRange;
        this.sensor = sensor;
		
        /*
         * Assign listeners a new ArrayList
         */
		this.listeners = new ArrayList<TouchSensorListener>();

		/*
		 * Assign mainThread a new thread;
		 * Make the thread close alongside java;
		 * Start the thread;
		 */
		mainThread = new Thread(this);
		mainThread.setDaemon(true);
		mainThread.start();
	}



	@Override
	public boolean isPressed() {

		/*
		 * TODO : Change to use sensor.
		 */
		if(ranger.getRange() <= (touchRange + rangeDescription.getNoise())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	/*
	 * TODO: Refactor to use sensor.
	 */
	public void addTouchSensorListener(TouchSensorListener _listener) {
		this.listeners.add(_listener);
	}
	
	@Override
	public void run() {
		while(this.isRunning)
		{
			/*
			 * TODO : Refactor to use sensor.
			 * If there is a wall within range, and there was previously:
			 * >> (1) loop through each listener
			 * >> (2) notify the listeners of the event
			 * >> (3) set the boolean, wallInRange, to true
			 */
			if((ranger.getRange() <= (touchRange + rangeDescription.getNoise())) && !this.wallInRange) {
				/*
				 * (1)
				 */
				for( int i = 0; i < listeners.size(); i++) {
					/*
					 * (2)
					 */
					listeners.get(i).sensorPressed(new TouchSensorEvent(this.OldValue, ranger.getRange()));
				}
				/*
				 * (3)
				 */
				this.wallInRange = true;
			}
			/*
			 * If there isn't a wall within range, and there was previously:
			 * >> (1) loop through each listener 
			 * >> (2) notify the listeners of the events
			 * >> (3) set the boolean, wallInRange, to false
			 */
			if((ranger.getRange() > (touchRange + rangeDescription.getNoise())) && wallInRange) {
				/*
				 * (1)
				 */
				for( int i = 0; i < listeners.size(); i++) {
					/*
					 * (2)
					 */
					listeners.get(i).sensorReleased(new TouchSensorEvent(this.OldValue, ranger.getRange()));
					listeners.get(i).sensorBumped(new TouchSensorEvent(this.OldValue, ranger.getRange()));
				}
				/*
				 * (3)
				 */
				wallInRange = false;

			}
			/*
			 * Delay by msDelayAmount to prevent this thread from
			 * dominating the CPU.
			 */
		    Delay(this.msDelayAmount);
		}
	}