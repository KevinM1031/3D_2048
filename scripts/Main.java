package scripts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



/**
 * Main class (Main)
 * 
 * This class is responsible for running the game and executing rudimentary functions for the game.
 * These functions include quitting, restarting, ending, and animating the game.
 * 
 * @author Kevin1031
 */
public class Main extends Window {
	
	// Stores keyboard input
	static char keyInput = 'X';
	
	// Indicates that user inputs regarding shifting the tiles are ignored
	static boolean frozen = false;
	
	// Indicates that tiles are shifting; virtual animations will be displayed instead of the tiles themselves
	static boolean inMotion = false;
	
	// Stores number of valid moves (score) and highest tile value (max)
	static int score = 0, max = 0;
	
	// Frames for animation; increase maxMotionFrame for smoother but heavier (and slower) animations
	static int motionFrame = 0, maxMotionFrame = 20;
	
	
	
	/**
	 * Main Method
	 * Deals with general game events, user inputs, and basic runtime management
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Window.open();
		boolean running = true;
		
//		 Generating first 4 random blocks
		Grid.addRandomBlock();
		Grid.addRandomBlock();
		Grid.addRandomBlock();
		Grid.addRandomBlock();
		Window.update();
		
		// Game runtime loop
		while(running) {
			
			// Detecting for user inputs and calling methods respectively
			if(keyInput != 'X') {
				switch(keyInput) {
				case 'Q': if(!PanelComponents.restart) quit(); break;
				case 'R': if(!PanelComponents.quit) restart(); break;
				case 'E': escape(); break;
				default: if(!frozen) Grid.shift(keyInput);
				} keyInput = 'X';
				
				if(Grid.motionList.size() > 0) animate();
				Window.update();
			}
			
			Thread.sleep(50);
		}
	}
	
	
	
	/**
	 * Quits and shuts down the program
	 * Asks for confirmation once
	 */
	static void quit() {
		frozen = true;
		
		// Quits game when user confirms this action
		if(PanelComponents.quit) close();
		
		// Opens confirmation panel if this action was called for the first time
		else PanelComponents.quit = true;
	}
	
	
	
	/**
	 * Restarts the game
	 * Overrides data including those related to Grid and scores
	 * Asks for confirmation once
	 */
	static void restart() {
		frozen = true;
		
		// Restarts game when user confirms this action
		if(PanelComponents.restart) {
			Main.max = 0;
			Main.score = 0;
			Grid.refresh();
			escape();
			
		// Opens confirmation panel if this action was called for the first time
		} else PanelComponents.restart = true;
	}
	
	
	
	/**
	 * Opens game over panel
	 */
	static void over() {
		Window.update();
		PanelComponents.end = true;
	}
	
	
	
	/**
	 * Escapes from confirmation panels for quit, restart, and end
	 * Unfreezes the game
	 */
	static void escape() {
		PanelComponents.quit = false;
		PanelComponents.restart = false;
		PanelComponents.end = false;
		frozen = false;
	}
	
	
	
	/**
	 * Prints the given string as a log to the console
	 * Includes date and time of print
	 * 
	 * @param log
	 */
	static void printLog(String log) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		System.out.println("[" + dtf.format(now) + "] " + log);
	}
	
	
	
	/**
	 * Animates blocks
	 * Deals with displaying block moving/spawning animations
	 * When this method is called, only the virtual blocks are shown; not the actual blocks themselves
	 */
	static void animate() {
		inMotion = true;
		
		// Separates animation into "maxMotionFrame" amount of frames
		for(int i = 1; i < maxMotionFrame; i++) {
			motionFrame = i;
			Window.update();
			
			// Delay per frame of animation; adjust this value to alter the speed of animation
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Clearing queued animation data after executing them
		Grid.motionList.clear();
		
		motionFrame = 0;
		inMotion = false;
	}
}


