package exercise1.part3;
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
	 * The configuration/pilot aren't going to change, so set these to final.
	 */
	private final WheeledRobotConfiguration config;
	private final DifferentialPilot pilot;
	
	private static UltrasonicSensor USSensor;
	
	/*
	 * In meters.
	 */
	private static float touchRange;
	
	private static boolean wallInRange = true;
	
	private static boolean turnedLeft = true;
	
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

	public static Runnable WallSensor = new Runnable()
	{	
		@Override
		public void run()
		{
			while(true)
			{
				if(turnedLeft)
				{
					if((USSensor.getRange())/100 <= touchRange) {
						wallInRange = true;
						turnedLeft = false;
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
	
	private boolean turningLeft = false;
	
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
			if(!wallInRange && this.isRunning && !turnedLeft)
			{
				turningLeft = true;
				Delay.msDelay(100);
				pilot.stop();
				pilot.rotate(90.0);
				turnedLeft = true;
				turningLeft = false;
			}
			if(this.isPressed && this.isRunning)
			{	
				pilot.stop();
				pilot.travel(-0.08F);
				pilot.rotate(-90.0);
				this.isPressed = false;
				turnedLeft = true;
				//wallInRange = true;
			}	
			
			
			//Delay.msDelay(20);
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
	
	public static final WheeledRobotConfiguration Robit = new WheeledRobotConfiguration(
			0.054f, 0.107f, 0.245f, Motor.C, Motor.B);
	
	/**
	 * Create an instance of RobotBumperProgram, and run the program.
	 */
	public static void main(String[] args) {
		RobotEscape program = new RobotEscape(Robit, 0.2f);
		/*
		 * Add a TouchSensorListener to the correct SensorPort.
		 */
		SensorPort.S1.addSensorPortListener(program);
		//SensorPort.S2.addSensorPortListener(program);
		USSensor = new UltrasonicSensor(SensorPort.S2);
		Thread wallSensorThread = new Thread(WallSensor);
		wallSensorThread.start();
		program.run();
	}
	
	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
		if(!turningLeft)
		{
			if(aNewValue < aOldValue)
				this.isPressed = true;
		}
	}
}
