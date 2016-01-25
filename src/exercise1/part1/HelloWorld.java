package exercise1.part1;
import lejos.nxt.Button;
import lejos.util.Delay;


/**
 * @author mjw553
 *
 */
public class HelloWorld {

	/**
	 * 
	 */
	public HelloWorld() {
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
