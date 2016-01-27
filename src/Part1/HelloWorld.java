package Part1;
import lejos.nxt.Button;
import lejos.util.Delay;

/**
 * @author mjw553
 */
public class HelloWorld {

	/**
	 * Main method of HelloWorld.
	 * Waits for any button press, then prints "Hello World" on the screen.
	 * @param args
	 */
	public static void main(String[] args) {
		Button.waitForAnyPress();
		System.out.println("Hello World");
		Delay.msDelay(2000);
	}

}
