import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.RobotConfigs;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.systems.ControllerWithTouchSensor;
import rp.systems.StoppableRunnable;

public class RobotBumperProgram implements StoppableRunnable, ControllerWithTouchSensor,TouchSensorListener {

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
	public RobotBumperProgram(WheeledRobotConfiguration config) {
		
		this.config = config;
		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
	}

	@Override
	public void run() {
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
				pilot.travel(-0.1f);
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

	@Override
	/*
	 * The sensor was pressed - set the isPressed boolean to true.
	 * @see rp.robotics.TouchSensorListener#sensorPressed(rp.robotics.TouchSensorEvent)
	 */
	public void sensorPressed(TouchSensorEvent _e) {
		this.isPressed = true;
	}

	@Override
	/*
	 * @see rp.robotics.TouchSensorListener#sensorReleased(rp.robotics.TouchSensorEvent)
	 */
	public void sensorReleased(TouchSensorEvent _e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/*
	 * @see rp.robotics.TouchSensorListener#sensorBumped(rp.robotics.TouchSensorEvent)
	 */
	public void sensorBumped(TouchSensorEvent _e) {
		// TODO Auto-generated method stub
		
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
		RobotBumperProgram program = new RobotBumperProgram(RobotConfigs.EXPRESS_BOT);
		program.run();
	}
}
