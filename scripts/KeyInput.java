package scripts;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput extends Main implements KeyListener {
	
	/**
	 * Gets keyboard input and records in Main.keyInput for them to be identified and actions to take place respectively
	 */
	public void keyPressed(KeyEvent e) {		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W: Main.keyInput = 'W'; break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S: Main.keyInput = 'S'; break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D: Main.keyInput = 'D'; break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A: Main.keyInput = 'A'; break;
		case KeyEvent.VK_Q: Main.keyInput = 'Q'; Main.printLog("Input received: 'Q'."); break;
		case KeyEvent.VK_R: Main.keyInput = 'R'; Main.printLog("Input received: 'R'.");break;
		case KeyEvent.VK_ESCAPE: Main.keyInput = 'E'; Main.printLog("Input received: 'ESC'."); break;
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
}
