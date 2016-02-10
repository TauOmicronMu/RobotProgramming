package Ex2.Part1;


import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
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
			0.055f, 0.112f, 0.230f, Motor.C, Motor.B);
	
	/*
	 * The configuration/pilot aren't going to change, so set these to final.
	 */
	private final WheeledRobotConfiguration config;
	private final DifferentialPilot pilot;

	private boolean isRunning;

	//Target distance away from an object.
	private float targetDistance;
	
	//The IR sensor being used to measure distance.
	private static OpticalDistanceSensor IRSensor;
	//The ultrasonic sensor being used to measure distance.
	private static UltrasonicSensor ultra;
	
	//Are we using ultrasonic sensor or infrared?
	private boolean UltraMode = false;
	
	//The error between actual distance and target distance from an object.
	private  Float error;
	
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
	
	@Override
    public void run() {
		/*
		 * The robot is now running, so set isRunning to true.
		 */
	    this.isRunning = true;
	    
	    //Proportional constant, changing this varies speed change of robot as distance varies.
	    float myConst = 2.0f;
	    
		while(this.isRunning){
			//If using ultrasonic sensor.
			if(UltraMode)
			{
				//Difference between distance and target distance.
				error = (ultra.getRange()/100.0f) - targetDistance;
			}
			//If using infrared sensor.
			else
			{
				//Difference between distance and target distance.
				error = ((IRSensor.getRange()/100.0f) - targetDistance) * myConst;
			}
			
			//If the error is negative and therefore below target distance.
			if(error < 0)
			{
				//Set travel speed, multiple by -1 to make positive as does not like - speeds. Multiple by error for proportional speed.
				pilot.setTravelSpeed(-1 * (pilot.getMaxTravelSpeed() * error));
				//Move backwards.
				pilot.backward();
			}
			else
			{
				//Set travel speed. Multiple by error for proportional speed.
				pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * error);
				//Move forwards.
				pilot.forward();
			}
			
			//Short delay for halt in updating.
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
		PropControl program = new PropControl(Robit,0.3f);
		//FROM THE INITIAL ULTRASONIC TESTING - USED IR INSTEAD BUT STILL INCLUDED.
		//Initialize the Ultrasonic sensor.
		//ultra = new UltrasonicSensor(SensorPort.S2);
		
		//Initialize the IR sensor.
		IRSensor = new OpticalDistanceSensor(SensorPort.S2);
		
		program.run();
	}
	
	
}
