package tests;
import lejos.nxt.Button;
import lejos.util.Delay;

/**
 * 
 */

/**
 * @author mjw553
 *
 */
public class Test {

	/**
	 * 
	 */
	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Button.waitForAnyPress();
		System.out.println("Hello World");
		Delay.msDelay(2000);
	}

}
