package Ex1.Part3;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class RobotEscape implements StoppableRunnable, SensorPortListener {
	
	/*
	 * The specific WheeledRobotConfiguration for our robot based on our measurements.
	 */
	public static final WheeledRobotConfiguration Robit = new WheeledRobotConfiguration(
			0.054f, 0.107f, 0.245f, Motor.C, Motor.B);
	
	/*
	 * The configuration/pilot aren't going to change, so set these to final.
	 */
	private final WheeledRobotConfiguration config;
	private final DifferentialPilot pilot;
	
	/*
	 * The ultrasonic sensor that will be used on the robot.
	 * It is on the left of the robot and will be used to detect whether there is a wall in range.
	 * If there is, it will continue forward.
	 * If not, it will turn left.
	 * This is to ensure that the robot follows the left wall.
	 */
	private static UltrasonicSensor USSensor;
	
	/*
	 * Touch range of the ultrasonic sensor.
	 * In meters.
	 */
	private static float touchRange;
	
	/*
	 * Is there a wall in range of the ultrasonic sensor?
	 * This is updated continuously as the robot is running.
	 */
	private static boolean wallInRange = true;
	
	/*
	 * Is the robot in search mode?
	 */
	private static boolean searchMode = true;
	
	/*
	 * Is the robot currently turning left?
	 */
	private boolean turningLeft = false;
	
	/*
	 * Is the robot running?
	 */
	private boolean isRunning = false;
	
	/*
	 * Has the touch sensor been depressed?
	 */	
	private boolean isPressed = false;

	/**
	 * Create a new instance of the RobotEscape class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public RobotEscape(WheeledRobotConfiguration config, float touchRange) {
		
		this.config = config;
		RobotEscape.touchRange = touchRange;
		
		/*
		 * Create a new pilot, based on the WheeledRobotConfiguration.
		 */
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
		
	}

	/*
	 * Anonymous WallSensor class that updates the wallInRange boolean.
	 * If in searchMode the robot will ignore the fact that there is no wall in range UNTIL a wall comes within range of the sensor. Then it returns to normal functionality.
	 * Out of searchMode the sensor will function normally, checking whether a wall is in range.
	 */
	public static Runnable WallSensor = new Runnable()
	{	
		@Override
		public void run()
		{
			while(true)
			{
				if(searchMode)
				{
					if((USSensor.getRange())/100 <= touchRange) {
						wallInRange = true;
						searchMode = false;
					}	
				}
				else
				{
					if((USSensor.getRange())/100 <= touchRange) {
						wallInRange = true;
					}

					if((USSensor.getRange())/100 > touchRange) {
						wallInRange = false;
					}
				}
				//System.out.println(USSensor.getDistance());
				Delay.msDelay(40);
			}
		}
	};
	
	/**
	 * Main run method of the robot.
	 * The robot will follow the left wall out of a maze by turning left when there is nothing on the left,
	 * and turning right when the front touch sensor is bumped.
	 */
	@Override
    public void run() {
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
		while(this.isRunning){
			/*
			 * Drive pilot forward until another action is executed.
			 */
			pilot.forward();
			
			if(this.isRunning)
			{
				/*
				 * Prevent this thread from dominating the CPU.
				 * (allows other actions to be performed)
				 */
				Delay.msDelay(40);
			}

			/*
			 * Wall out of range so turn left method.
			 * If a wall is not in range of the ultrasonic sensor (facing left), and the robot is not in search mode,
			 * it will initiate turningLeft (set to true), stop and rotate 90 degrees left. It also sets searchmode to true and stops turningLeft (set to false).
			 * A delay is also used so that the robot drives clear of the wall it was detecting before it drove out of range.
			 */
			if(!wallInRange && this.isRunning && !searchMode)
			{
				turningLeft = true;
				Delay.msDelay(100);
				pilot.stop();
				pilot.rotate(90.0);
				searchMode = true;
				turningLeft = false;
			}
			
			/*
			 * Bump so turn right method.
			 * Same bumper method as part 2, but doesn't reverse as far and only rotates 90 degrees to the right.
			 * Also sets searchMode to true.
			 */
			if(this.isPressed && this.isRunning)
			{	
				pilot.stop();
				pilot.travel(-0.08F);
				pilot.rotate(-90.0);
				this.isPressed = false;
				searchMode = true;
			}	
		}
	}
	
	/**
	 * Stop method for the robot.
	 */
	@Override
	public void stop() {
		this.isRunning = false;
	}

	/**
	 * Returns the WheeledRobotConfig of the current robot.
	 * @return the WheeledRobotConfig of the current robot.
	 */
	public WheeledRobotConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Create an instance of RobotEscape program, and run the program.
	 * Adds a listener to the touch sensor.
	 * Adds an ultrasonic sensor.
	 * Creates and starts the wallSensorThread.
	 */
	public static void main(String[] args) {
		
		RobotEscape program = new RobotEscape(Robit, 0.2f);
		SensorPort.S1.addSensorPortListener(program);
		USSensor = new UltrasonicSensor(SensorPort.S2);
		Thread wallSensorThread = new Thread(WallSensor);
		wallSensorThread.start();
		program.run();
	}
	
	/**
	 * Listens for when the bumper is pressed, and when it is isPressed is set to true.
	 * The listener cannot throw a state changed if the robot is turning left,
	 * this stops both the Wall out of range, and Bump method from occurring simultaneously/interrupting each other.
	 * This means that the Wall out of range so turn left method has priority.
	 */
	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
		if(!turningLeft)
		{
			if(aNewValue < aOldValue)
				this.isPressed = true;
		}
	}
}
