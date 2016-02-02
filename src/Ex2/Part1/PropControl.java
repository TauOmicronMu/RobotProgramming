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

public class PropControl implements StoppableRunnable {
	
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
	
	private static OpticalDistanceSensor IRSensor;
	private static UltrasonicSensor ultra;
	
	private boolean UltraMode = true;
	
	/**
	 * Create a new instance of the PropControl class.
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
	
	private  Float error;
	
	
	@Override
    public void run() {
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
	    double myConst = 3.0;
	    
		while(this.isRunning){
			if(UltraMode)
			{
				error = (ultra.getRange()/100.0f) - targetDistance;
				System.out.println("error:" + ultra.getRange());
			}
			else
			{
				error = (IRSensor.getRange()/100.0f) - targetDistance;
				System.out.println("error:" + IRSensor.getRange());
			}
			
			if(error < 0)
			{
				pilot.setTravelSpeed(-1 * (error * myConst));
				pilot.backward();
			}
			else
			{
				pilot.setTravelSpeed(error * myConst);
				pilot.forward();
			}
			
			//System.out.println("Waiting");
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

	
	public static void main(String[] args) {
		PropControl program = new PropControl(Robit,0.2f);
		
		ultra = new UltrasonicSensor(SensorPort.S2);
		
		IRSensor = new OpticalDistanceSensor(SensorPort.S1);
		
		program.run();
	}
	
	
}
