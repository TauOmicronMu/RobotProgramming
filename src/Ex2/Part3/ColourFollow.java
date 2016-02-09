package Ex2.Part3;


import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.NXTCam;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class ColourFollow implements StoppableRunnable {
	
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
	
	/**
	 * Create a new instance of the RobotEscape class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public ColourFollow(WheeledRobotConfiguration config) {
		
		this.config = config;
		/*
		 * Create a new pilot, based on the WheeledRobotConfiguration.
		 */
		
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
		
	}
	
	private int numberOfObj;
	private int rectangleWidth;
	private int minWidth;
	
	@Override
    public void run() {
		//Constant to give proportional angle movement
		double MyConst = 0.15;
		//The centre of the camera image
		double imageCentre = 176/2;
		//Is the robot allowed to run?
		isRunning = true;
		//Travel speed of the robot.
		pilot.setTravelSpeed(0.2);
		
		while(isRunning)
		{
			//Number of objects picked up by the camera's tracking.
			numberOfObj = cam.getNumberOfObjects();
			//If number of objects is > 0.
			if(numberOfObj > 0)
			{
				//Get the width of the largest rectangle.
				rectangleWidth = cam.getRectangle(0).width;
				//Set min width of rectange, to remove noise.
				minWidth = 10;
				//If rectange is greater then min width (therefore a relevant rectangle).
				if(rectangleWidth > minWidth)
				{
					//Centre of target rectangle.
					double targetCentre = cam.getRectangle(0).getCenterX();
					//Arc of rotation, proportional to constant for smoothness.
					double arc = 1/((imageCentre - targetCentre)*MyConst);
					//If angle is infinity...
					if(arc == Double.POSITIVE_INFINITY)
						//Just drive forwards
						pilot.forward();
					else
						//Arc to certain degree.
						pilot.arcForward(arc);
				}
			}
			else
			{
				//Stop the robot.
				pilot.stop();
			}
			//Short delay to allow for movements.
			Delay.msDelay(50);
		}
	}
	
	/**
	 * Stop method for the robot.
	 */
	@Override
	public void stop() {
		this.isRunning = false;
	}

	/**	//Listener that monitors the pilot.setTravelSpeed(0.2);values of the two light sensors.
	private static JunctionListenerThread myThread;
	 * Returns the WheeledRobotConfig of the current robot.
	 * @return the WheeledRobotConfig of the current robot.
	 */
	public WheeledRobotConfiguration getConfig() {
		return config;
	}

	
	private static NXTCam cam;
	
	public static void main(String[] args) {
		//Construct program.
		ColourFollow program = new ColourFollow(Robit);
		//Create NXTCam in sensor port 2.
		cam = new NXTCam(SensorPort.S2);
		//Set tracking to objects.
		cam.setTrackingMode(NXTCam.OBJECT_TRACKING);
		//Sort by size, largest rectangles first.
		cam.sortBy(NXTCam.SIZE);
		//Enable rectangle tracking.
		cam.enableTracking(true);
		//Start the program.
		program.run();
	}
}