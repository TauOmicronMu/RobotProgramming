package Ex2.Part2;


import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

//COMIBINATION OF LINE FOLLOW IN PART 1 AND PART 2 - SELECTED BY ROBOT USER INPUT.

public class LineFollow2 implements StoppableRunnable {
	
	/*
	 * The specific WheeledRobotConfiguration for our robot based on our measurements.
	 */
	public static final WheeledRobotConfiguration Robit = new WheeledRobotConfiguration(
			0.054f, 0.107f, 0.245f, Motor.C, Motor.B);
	
	
	/*
	 * The configuration/pilot aren't going to change, so set these to final.
	 */
	private WheeledRobotConfiguration config;
	private static DifferentialPilot pilot;

	private boolean isRunning;
	
	//The two light sensors connected to the front of the robot.
	private static LightSensor leftSensor;
	private static LightSensor rightSensor;
	
	//Listener that monitors the values of the two light sensors.
	private static JunctionDetectorThread junctionThread;
	
	//Are we in user input mode or random mode?
	private boolean userInput;
	
	/**
	 * Create a new instance of the RobotEscape class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public LineFollow2(WheeledRobotConfiguration config) {
		
		this.config = config;
		/*
		 * Create a new pilot, based on the WheeledRobotConfiguration.
		 */
		
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
		
	}
	
	@Override
    public void run() {
		//Proportional control used for movement angle, constant defined here.
		Double MyConst = 0.1;
		
		//TO MAKE A SYSTEM THAT CAN DEAL WITH MULTIPLE SITUATIONS AND LIGHTING LEVELS, WE CALIBRATE LIGHT SENSORS BEFORE STARTING.
		//Calibrate the dark of left sensor
		System.out.println("Calibrate dark left:");
		Button.waitForAnyPress();
		leftSensor.calibrateLow();
		
		//Calibrate the dark of right sensor
		System.out.println("Calibrate dark right:");
		Button.waitForAnyPress();
		rightSensor.calibrateLow();
		
		//Calibrate the light of left sensor
		System.out.println("Calibrate light left:");
		Button.waitForAnyPress();
		leftSensor.calibrateHigh();
		
		//Calibrate the light of right sensor.
		System.out.println("Calibrate light right:");
		Button.waitForAnyPress();
		rightSensor.calibrateHigh();
		
		double rightError;
		double leftError;
		

		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
	    //Allow the user to select if they want random or user input movement mode.
	    System.out.println("Enter for User, Escape for Random.");
	    Button.waitForAnyPress();
	    if(Button.ENTER.isDown()) {
	    	userInput = true;
		}
		else if(Button.ESCAPE.isDown()) {
			userInput = false;
		}
	    
	    Button.waitForAnyPress();
	    
	    
		while(this.isRunning){
			//working values - 0.2 travel speed and 0.09 MyConst (decreasing MyConst seems to make smoother)
			pilot.setTravelSpeed(0.2);
			
			//As sometimes the sensors appear to have - values and this should not be possible... normalise to working 0-100 range.
			if(rightSensor.getLightValue() < 0)
			{
				rightError = 0.0;
			}
			else if(rightSensor.getLightValue() > 100)
			{
				rightError = 100.0;
			}
			else
			{
				rightError = rightSensor.getLightValue();
			}

			if(leftSensor.getLightValue() < 0)
			{
				leftError = 0.0;
			}
			else if(leftSensor.getLightValue() > 100)
			{
				leftError = 100.0;
			}
			else
			{
				leftError = leftSensor.getLightValue();
			}
			
			//Angle to turn... proportional control used here.
			double arc = 1/((rightError - leftError)*MyConst);
			
			//If the robot should be stopping
			if(junctionThread.getStop())
			{
				//Stop the robot.
				pilot.stop();
				//Travel 6cm (so that the robot is in a suitable position for rotation onto another line)
				pilot.travel(0.06f);
				//If random mode is selected...
				if(userInput == false)
				{
					//Random number generator.
					Random rand = new Random();
					//Max number
					int max = 2;
					//Min number
					int min = 1;
					//Choose number between 1 and 2.
					int randomNum = rand.nextInt((max - min) + 1) + min;
					if(randomNum == 1)
					{
						//Turn left
						pilot.rotate(90.0);
					}
					//If random number is 2
					else
					{
						//Turn right
						pilot.rotate(-90.0);
					}
				}
				//If user input movement mode
				else
				{
					Button.waitForAnyPress();
					//If user pressed left button
					if(Button.LEFT.isDown()) {
						pilot.rotate(-90.0);
					}
					//If user presses right button
					else if(Button.RIGHT.isDown()) {
						pilot.rotate(90.0);
					}
					//If escape...
					else if(Button.ESCAPE.isDown()) {
						pilot.rotate(180.0);
					}
				}
			}
			//If the arc value is 0, travel forwards.
			else if(arc == Double.POSITIVE_INFINITY)
				pilot.forward();
			//Else, turn at a specified angle.	
			else
				pilot.arcForward(arc);
			
			//Let other things run on the robot.
			Delay.msDelay(20);
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

	
	public static void main(String[] args) {
		LineFollow2 program = new LineFollow2(Robit);
		
		//Assign the sensors their sensorports.
		leftSensor = new LightSensor(SensorPort.S3);
		rightSensor = new LightSensor(SensorPort.S1);
		//Start the listener thread for both sensors on a dark line at the same time.
		junctionThread = new JunctionDetectorThread(leftSensor, rightSensor, pilot);
		junctionThread.start();
		
		program.run();
	}
	
	
}
