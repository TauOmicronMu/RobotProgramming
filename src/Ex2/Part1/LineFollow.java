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
		
		Double MyConst = 2.5;
		
		Button.waitForAnyPress();
		int setBlackLeft = leftSensor.getLightValue();
		System.out.println(setBlackLeft);
		
		Button.waitForAnyPress();
		int setBlackRight = rightSensor.getLightValue();
		System.out.println(setBlackRight);
		
		
		Button.waitForAnyPress();
		int setWhiteLeft = leftSensor.getLightValue();
		System.out.println(setWhiteLeft);
		
		Button.waitForAnyPress();
		int setWhiteRight = rightSensor.getLightValue();
		System.out.println(setWhiteRight);
		
		int leftRange = setWhiteLeft - setBlackLeft;
		int rightRange = setWhiteRight - setBlackRight;
		
		double propDiff = (double)(leftRange - rightRange)*MyConst;
		System.out.println(propDiff);
		
		Button.waitForAnyPress();

		
		
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
		while(this.isRunning){
			
			
			
			pilot.setTravelSpeed(0.1);
			
			double rightError = rightSensor.getLightValue() - setBlackRight;
			double leftError = leftSensor.getLightValue() - setBlackLeft;
			
			double arc = 1/((rightError- leftError)*MyConst + propDiff);
			
			
			System.out.println(arc);
			
			if(arc == Double.POSITIVE_INFINITY)
				pilot.forward();
			else
				pilot.arcForward(arc);
			
			//System.out.println("Waiting");
			Delay.msDelay(10);
			
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
