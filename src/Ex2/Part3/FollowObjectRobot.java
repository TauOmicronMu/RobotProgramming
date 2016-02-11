/*
 * THIS FILE WAS UNUSED IN THE FINAL SOLUTION FOR THE EXERCISE. DO NOT MARK THIS.
 */

package Ex2.Part3;

import lejos.nxt.addon.NXTCam;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.robotics.DifferentialDriveRobot;

public class FollowObjectRobot {
    private 
	
	public static void main(String[] args) {
    	//TODO : Define the correct config.
    	DifferentialDriveRobot robot = new DifferentialDriveRobot(config);
    	//TODO : Assign the NXTCam to the robot (Add the IC2Port).
    	NXTCam camera = new NXTCam(null);
    	Behavior b1 = new LookAround(robot);
    	Behavior b2 = new DriveTowardsObject(robot, camera);
    	Behavior [] bArray = {b1, b2};
    	Arbitrator arby = new Arbitrator(bArray);
    	arby.start();
    }
}
