package Ex2.Part3;

import lejos.nxt.addon.NXTCam;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import rp.robotics.DifferentialDriveRobot;

/*
 * If the robot is not "looking at" a "brightly-coloured"
 * object, spins the robot around on the spot until it 
 * has detected one.
 */
public class LookAround implements Behavior {
    /*
	 * Follow bright objects - true
	 * Follow dark objects? - false
	 */
	private final boolean lightDark = true;
	
	/*
	 * Has the robot been suppressed?
	 * Used to exit the action method promptly, as recommended
	 * in the official leJOS documentation.
	 */
	private boolean suppressed = false;

	private DifferentialDriveRobot robot;
    private DifferentialPilot pilot;
    
	/**
	 * @param robot The DifferentialDriveRobot.
	 * @param camera The attached NXTCam.
	 */
	public LookAround(DifferentialDriveRobot robot) {
		this.robot = robot;
		this.camera = camera;
		
		this.pilot = this.robot.getDifferentialPilot();
	}

	@Override
	public boolean takeControl() {
		return //TODO : is there something bright in the camera?
	}

	@Override
	public void action() {
        this.suppressed = false;
		while(!this.suppressed && //TODO : there is something bright in the camera) {
	        //TODO : Drive in the direction of the object.
	    }		

	@Override
	public void suppress() {
		this.suppressed = false;
	}

}
