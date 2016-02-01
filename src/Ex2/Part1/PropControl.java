package Ex2.Part1;

import java.lang.annotation.Target;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.addon.OpticalDistanceSensor;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class PropControl implements StoppableRunnable, SensorPortListener {
	
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

	private float targetDistance;
	private float currentDistance; 
	
	/**
	 * Create a new instance of the RobotEscape class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public PropControl(WheeledRobotConfiguration config, float targetDistance) {
		
		this.config = config;
		this.targetDistance = targetDistance;
		
		/*
		 * Create a new pilot, based on the WheeledRobotConfiguration.
		 */
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
		
	}
	
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
			Float error = (IRSensor.getRange()/100) - targetDistance;
			System.out.println("error:" + currentDistance);
			if(error < 0)
			{
				pilot.setTravelSpeed(-1 * (error * 5));
				pilot.backward();
			}
			else
			{
				pilot.setTravelSpeed(error * 5);
				pilot.forward();
			}
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

	/**
	 * Returns the WheeledRobotConfig of the current robot.
	 * @return the WheeledRobotConfig of the current robot.
	 */
	public WheeledRobotConfiguration getConfig() {
		return config;
	}

	private static OpticalDistanceSensor IRSensor;
	
	public static void main(String[] args) {
		PropControl program = new PropControl(Robit,0.2f);
		SensorPort.S2.addSensorPortListener(program);
		IRSensor = new OpticalDistanceSensor(SensorPort.S2);
		program.run();
	}
	
	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
			currentDistance = aNewValue;
	}
	
}
