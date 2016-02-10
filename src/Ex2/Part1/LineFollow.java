package Ex2.Part1;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class LineFollow implements StoppableRunnable {
	
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

	private boolean isRunning;
	
	/*
	 * Set the two light sensors- left and right.
	 */
	private static LightSensor leftSensor;
	private static LightSensor rightSensor;
	
	
	/**
	 * Create a new instance of the RobotEscape class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public LineFollow(WheeledRobotConfiguration config) {
		
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
		
		Double MyConst = 0.07;
		
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
		
		Button.waitForAnyPress();

		
		
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
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
			
			
			System.out.println(rightError - leftError);
			
			if(arc == Double.POSITIVE_INFINITY)
				pilot.forward();
				
			else
				pilot.arcForward(arc);
			
			
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
		LineFollow program = new LineFollow(Robit);
		
		
		leftSensor = new LightSensor(SensorPort.S3);
		rightSensor = new LightSensor(SensorPort.S1);

		
		program.run();
	}
	
	
}
