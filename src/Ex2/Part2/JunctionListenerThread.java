package Ex2.Part2;

import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class JunctionListenerThread extends Thread {

	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private DifferentialPilot pilot;
	private boolean stopRoBit = false;
	
	public JunctionListenerThread(LightSensor leftSensor, LightSensor rightSensor, DifferentialPilot pilot)
	{
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.pilot = pilot;
				
	}
	
	public void run()
	{	
		boolean m_running = true;
		while(m_running)
		{
			//Value should, in theory, range from 0 to 100, therefore if both are < 20 (on a dark line)...
			if(leftSensor.readValue() < 20 && rightSensor.readValue() < 20)
			{
				//Stop the robot.
				stopRoBit = true;
			}
			//Keep the robot going.
			else
				stopRoBit = false;
			//Sleep to let other things happen on the robot.
			try {
				sleep(30);
			} catch (InterruptedException e) {
				//If interrupted, print message.
				System.out.println("I couldn't sleep.");
			}
		}
	}
	//Set robot stop state.
	public void setStop()
	{
		stopRoBit = true;
	}
	//Get robot stop state.
	public boolean getStop()
	{
		return stopRoBit;
	}
}
