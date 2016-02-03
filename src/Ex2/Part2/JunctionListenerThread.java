package Ex2.Part2;

import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorListener;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.robotics.navigation.DifferentialPilot;

public class JunctionListenerThread extends Thread {

	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private DifferentialPilot pilot;
	
	public JunctionListenerThread(LightSensor leftSensor, LightSensor rightSensor, DifferentialPilot pilot)
	{
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.pilot = pilot;
				
	}
	
	public void run()
	{	
		System.out.println("Im added!");
		boolean m_running = true;
		while(m_running)
		{
			if(leftSensor.readValue() < 10 && rightSensor.readValue() < 10)
			{
				pilot.stop();
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Woops i couldn't sleep... ahhh insomnia!... HEY JACK!!!");
			}
		}
	}
}
