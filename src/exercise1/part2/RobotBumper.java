package exercise1.part2;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.RobotConfigs;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class RobotBumper implements StoppableRunnable, SensorPortListener {

	/*
	 * The configuration/pilot aren't going to change, so set these to final.
	 */
	private final WheeledRobotConfiguration config;
	private final DifferentialPilot pilot;
	
	/*
	 * Is the robot running?
	 */
	private boolean isRunning = false;
	
	/*
	 * Has the touch sensor been depressed?
	 */	
	private boolean isPressed = false;
	
	/**
	 * Create a new instance of the RobotBumperController class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public RobotBumper(WheeledRobotConfiguration config) {
		
		this.config = config;
		
		/*
		 * Create a new pilot, based on the WheeledRobotConfiguration.
		 */
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
		
		
		//m_system = new WheeledRobotSystem(_config);
		//m_pilot = m_system.getPilot();
		
	}

	@Override
    public void run() {
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
		while(this.isRunning){
			/*
			 * Drive pilot forward until another action is taken.
			 */
			pilot.forward();
			
			if(this.isRunning)
			{
				/*
				 * Prevent this thread from dominating the CPU.
				 */
				Delay.msDelay(40);
			}
			/*
			 * If the robot is running and has hit an obstacle: 
			 * >> Stop the robot;
			 * >> Reverse the robot a small amount;
			 * >> Rotate the robot 180 degrees;
			 * >> Set the isPressed boolean to false, because the touch
			 *    sensor should no longer be depressed (and if it is, it
			 *    will be set back to true when an event is created).
			 */
			if(this.isPressed && this.isRunning)
			{
				pilot.stop();
				pilot.travel(-0.1F);
				pilot.rotate(180.0);
				this.isPressed = false;
			}
		}
	}
	
	@Override
	/* Stop the robot - set the isRunning boolean to false.
	 * @see rp.systems.StoppableRunnable#stop()
	 */
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
	 * Create an instance of RobotBumperProgram, and run the program.
	 */
	public static void main(String[] args) {
		RobotBumper program = new RobotBumper(RobotConfigs.EXPRESS_BOT);
		
		//BumperController demo = new BumperController(new WheeledRobotConfiguration(0.056f, 0.175f, 0.230f, Motor.B, Motor.C));
		
		/*
		 * Add a TouchSensorListener to the correct SensorPort.
		 */
		SensorPort.S1.addSensorPortListener(program);
		program.run();
	}

	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
		//might need to change this
		if(aNewValue < aOldValue)
			this.isPressed = true;
		
	}
}
