import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.systems.StoppableRunnable;

public class RobotBumper implements StoppableRunnable, SensorPortListener {

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
	
	/*
	 * Is the robot running?
	 */
	private boolean isRunning = false;
	
	/*
	 * Has the touch sensor been depressed?
	 */	
	private boolean isPressed = false;
	
	
	/**
	 * Create a new instance of the RobotBumper class.
	 * @param config The WheeledRobotConfiguration specific to this robot.
	 */
	public RobotBumper(WheeledRobotConfiguration config) {
		
		this.config = config;

		this.pilot = new DifferentialPilot(config.getWheelDiameter(), 
                                           config.getTrackWidth(), 
                                           config.getLeftWheel(), 
                                           config.getRightWheel());
	}

	/**
	 * Main run method of the robot.
	 * It will drive forward until the bumper is pressed,
	 * at which point it will stop, reverse 10cm (0.1m), turn 180 degrees and drive forward.
	 * This process will repeat forever.
	 */
	@Override
    public void run() {

	    this.isRunning = true;
	    
		while(this.isRunning){

			pilot.forward();
			
			if(this.isRunning)
			{
				Delay.msDelay(40);
			}

			if(this.isPressed && this.isRunning)
			{
				pilot.stop();
				pilot.travel(-0.1F);
				pilot.rotate(180.0);
				this.isPressed = false;
			}
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
	
	
	
	
	/**
	 * Creates an instance of RobotBumper, adds a listener to the bumper in SensorPort.S1 and run the program.
	 */
	public static void main(String[] args) {
		
		RobotBumper program = new RobotBumper(Robit);
		
		SensorPort.S1.addSensorPortListener(program);
		program.run();
	}

	/**
	 * Listens for when the bumper is pressed, and when it is isPressed is set to true.
	 */
	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
		if(aNewValue < aOldValue)
			this.isPressed = true;
	}
}
