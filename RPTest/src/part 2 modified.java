package rp.assignments.individual.ex1;


import lejos.util.Delay;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.TouchSensorEvent;
import rp.robotics.TouchSensorListener;
import rp.systems.ControllerWithTouchSensor;

public class BumperController implements ControllerWithTouchSensor,TouchSensorListener {

	private final DifferentialDriveRobot robot;
	private final DifferentialPilot pilot;
	private boolean m_run = true;
	private boolean m_changed = false;
	
	public BumperController(DifferentialDriveRobot _robot) {
		
		this.robot = _robot;
		// used pilot to make code more aestetic and easier to understand.
		this.pilot = this.robot.getDifferentialPilot();
		
	}

	@Override
	public void run() {
		while(m_run){
			pilot.forward();
			// check m_run alot for responsiveness
			if(m_run)
			{
				// decreased delay for quicker responsiveness
				Delay.msDelay(40);
			}
			if(m_changed && m_run)
			{
				// add stop here instead of listener
				pilot().stop();
				// CHECK FOR ROBOT //
				pilot().travel(-0.1f);
				pilot().rotate(180.0);
				m_changed = false;
			}
		}
	}
	
	@Override
	public void stop() {
		m_run = false;
	}

	@Override
	public void sensorPressed(TouchSensorEvent _e) {
		m_changed = true;
	}

	@Override
	public void sensorReleased(TouchSensorEvent _e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sensorBumped(TouchSensorEvent _e) {
		// TODO Auto-generated method stub
		
	}

}
