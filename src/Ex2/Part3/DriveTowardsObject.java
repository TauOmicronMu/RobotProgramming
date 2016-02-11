/*
 * THIS FILE WAS UNUSED IN THE FINAL SOLUTION FOR THE EXERCISE. DO NOT MARK THIS.
 */

package Ex2.Part3;

import lejos.nxt.addon.NXTCam;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import rp.robotics.DifferentialDriveRobot;

/*
 * Handles the behaviour of the robot once it has found a 
 * "brightly coloured object", and moves it in the direction
 * of that object. Subsumes LookAroundBehavior.
 */
public class DriveTowardsObject implements Behavior{
	
	/*
	 * Has the robot been suppressed?
	 * Used to exit the action method promptly, as recommended
	 * in the official leJOS documentation.
	 */
	private boolean suppressed = false;

	private DifferentialDriveRobot robot;
	private DifferentialPilot pilot;
	
	private NXTCam camera;
	
	public DriveTowardsObject(DifferentialDriveRobot robot, NXTCam camera) {
		this.robot = robot;
		this.camera = camera;
		
		this.pilot = this.robot.getDifferentialPilot();
	}
	
	@Override
	/**
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 * If no other behaviours have taken control, take control.
	 */
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		this.suppressed = false;
		while(!this.suppressed) {
			//TODO : Spin around and look for a coloured ball.
		}
		
	}

	@Override
	/**
	 * @see lejos.robotics.subsumption.Behavior#suppress()
	 * Stop the action method of the robot.
	 */
	public void suppress() {
		this.suppressed = true; 
	}

}
