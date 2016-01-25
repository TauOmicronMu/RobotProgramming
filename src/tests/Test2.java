package tests;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.RangeScannerDescription;
import rp.config.WheeledRobotConfiguration;
import rp.config.WheeledRobotDescription;
import rp.robotics.DifferentialDriveRobot;
import rp.systems.StoppableRunnable;

import rp.config.RobotConfigs;

public class Test2 implements StoppableRunnable {

	WheeledRobotConfiguration conf;
	DifferentialPilot pilot;
	
	public Test2(WheeledRobotConfiguration conf) {
		this.conf = conf;
		this.pilot = new DifferentialPilot(conf.getWheelDiameter(), 
				                           conf.getTrackWidth(), 
				                           conf.getLeftWheel(), 
				                           conf.getRightWheel());
	}

	public static void main(String[] args) {
		Test2 test = new Test2(RobotConfigs.EXPRESS_BOT);
		test.run();
	}

	@Override
	public void run() {
		this.pilot.travel(1.0f);
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
