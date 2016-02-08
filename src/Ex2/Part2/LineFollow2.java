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
	
	private static LightSensor leftSensor;
	private static LightSensor rightSensor;
	
	private static JunctionListenerThread myThread;
	
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
		
		Double MyConst = 0.09;
		
		System.out.println("Calibrate dark left:");
		Button.waitForAnyPress();
		leftSensor.calibrateLow();
		
		System.out.println("Calibrate dark right:");
		Button.waitForAnyPress();
		rightSensor.calibrateLow();
		
		System.out.println("Calibrate light left:");
		Button.waitForAnyPress();
		leftSensor.calibrateHigh();
		
		System.out.println("Calibrate light right:");
		Button.waitForAnyPress();
		rightSensor.calibrateHigh();
		
		double rightError;
		double leftError;
		

		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
	    System.out.println("Do you want Random or user Input movement? Enter for User, Escape for Random.");
	    Button.waitForAnyPress();
	    if(Button.ENTER.isDown()) {
	    	userInput = true;
		}
		else if(Button.ESCAPE.isDown()) {
			userInput = false;
		}
	    
	    Button.waitForAnyPress();
	    
	    
		while(this.isRunning){
			//working values - 0.2 travel speed and 0.07 MyConst (decreasing MyConst seems to make smoother)
			pilot.setTravelSpeed(0.2);
			
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
			
			double arc = 1/((rightError - leftError)*MyConst);
			
			//System.out.println(leftSensor.readValue());
			if(myThread.getStop())
			{
				pilot.stop();
				pilot.travel(0.06f);
				
				if(userInput == false)
				{
					//Random left/right line selection Part 2 Extension code.
					//Butteon.waitForAnyPress();
					Random rand = new Random();
					int max = 2;
					int min = 1;
					int randomNum = rand.nextInt((max - min) + 1) + min;
					if(randomNum == 1)
					{
						pilot.rotate(90.0);
					}
					else
					{
						pilot.rotate(-90.0);
					}
				}
				else
				{
					//User input Part 2 Extension code.
					Button.waitForAnyPress();
					if(Button.LEFT.isDown()) {
						pilot.rotate(-90.0);
					}
					else if(Button.RIGHT.isDown()) {
						pilot.rotate(90.0);
					}
					else if(Button.ESCAPE.isDown()) {
						pilot.rotate(180.0);
					}
				}
			}
			else if(arc == Double.POSITIVE_INFINITY)
				pilot.forward();
				
			else
				pilot.arcForward(arc);
			
			Delay.msDelay(40);
			
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
		
		
		leftSensor = new LightSensor(SensorPort.S3);
		rightSensor = new LightSensor(SensorPort.S1);
		
		myThread = new JunctionListenerThread(leftSensor, rightSensor, pilot);
		myThread.start();
		
		program.run();
	}
	
	
}
