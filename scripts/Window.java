package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;



/**
 * Window class
 * 
 * This class deals with the use of GUI in the game.
 * Functions simply include basic GUI functions: opening, closing, updating the window.
 * 
 * @author Kevin1031
 */
public class Window {
	
	private static FrameComponents fc;

	static void open() {
		fc = new FrameComponents("2048", new PanelComponents(Grid.grid));
		fc.refreshSize();
	}
	
	static void update() {
		fc.repaint();
		fc.validate();
	}
	
	static void close() {
		fc.dispose();
		System.exit(0);
	}
}






/**
 * FrameComponents class
 * 
 * This class simply creates the GUI for the game.
 * No additional unique functions are performed by this class.
 * 
 * @author Kevin1031
 */
class FrameComponents extends JFrame {

	private static final long serialVersionUID = 1L;
	private final int defaultWidth = 500, defaultHeight = 500;
	
	FrameComponents(String name, JPanel contentPane) {
		setTitle(name);
		setSize(defaultWidth, defaultHeight);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setContentPane(contentPane);
		setVisible(true);
		addKeyListener(new KeyInput());
		contentPane.repaint();
	}
	
	public void refreshSize() {
		int decorWidth = getInsets().left + getInsets().right;
		int decorHeight = getInsets().top + getInsets().bottom;
		setSize(defaultWidth + decorWidth, defaultHeight + decorHeight);
	}
	
}






/**
 * PanelComponents class
 * 
 * This class deals with the graphic display of the game.
 * Functions include displaying all the components of the Grid and labels, creating animations, etc.
 * 
 * @author Kevin1031
 */
class PanelComponents extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int defaultWidth = 500, defaultHeight = 500;
	
	// The gridData from the Grid class is copied into this 2D array in case some temporary changes in the data are needed
	// For now, such temporary data changes do not occur
	private static int[][] gridData = new int[4][4];
	
	// Boolean variables indicating whether to display a specific panel on the window
	static boolean restart = false, quit = false, end = false;
	
	// Text fonts
	Font bigFont = new Font("Rockwell", Font.BOLD, 16), smallFont = new Font("Rockwell", Font.BOLD, 13), hugeFont = new Font("Rockwell", Font.BOLD, 34);
	
	// Sets size and brings gridData
	PanelComponents(int[][] grid) {
		setSize(defaultWidth,defaultHeight);
		gridData = grid;
	}
	
	
	
	/**
	 * Basic overriding paint method
	 * Called automatically when window is updated
	 * Determines which graphical functions to execute
	 */
	@Override
	public void paintComponent(Graphics g) {
		
		if(getWidth() != defaultWidth || getHeight() != defaultHeight)
			setSize(defaultWidth, defaultHeight);
		
		// Antialializing for smooth texts and graphics
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Bottom layer: Grid
		drawGrid(g);
		
		// Middle layer: Blocks
		if(Main.inMotion) {
			drawMovingBlocks(g);
			drawSpawningBlock(g);
		} else drawBlocks(g);
		
		// Top layer: Panels
		if(quit) drawQuitPanel(g);
		else if(restart) drawRestartPanel(g);
		else if(end) drawEndPanel(g);
	}
	
	
	
	/**
	 * Creates a panel asking for confirmation regarding "Quit"
	 * Completely for display purposes; does not listen input
	 * 
	 * @param g
	 */
	void drawQuitPanel(Graphics g) {
		
		// Drawing panel box
		g.setColor(new Color(191,191,191,191));
		g.fillRect(100, 200, 300, 100);
		g.setColor(Color.BLACK);
		g.drawRect(100, 200, 300, 100);
		
		// Writing labels and descriptions
		g.setFont(bigFont);
		g.drawString("Quit Game?", 200, 225);
		
		g.setFont(smallFont);
		g.drawString("Press Q to quit.", 195, 265);
		g.drawString("Press ESC to cancel.", 185, 285);
	}
	
	
	
	/**
	 * Creates a panel asking for confirmation regarding "Restart"
	 * Completely for display purposes; does not listen input
	 * 
	 * @param g
	 */
	void drawRestartPanel(Graphics g) {
		
		// Drawing panel box
		g.setColor(new Color(191,191,191,191));
		g.fillRect(100, 200, 300, 100);
		g.setColor(Color.BLACK);
		g.drawRect(100, 200, 300, 100);
		
		// Writing labels and descriptions
		g.setFont(bigFont);
		g.drawString("Restart?", 215, 225);
		
		g.setFont(smallFont);
		g.drawString("Press R to restart.", 190, 265);
		g.drawString("Press ESC to cancel.", 185, 285);
	}
	
	
	
	/**
	 * Creates a "Game Over" panel
	 * Completely for display purposes; does not listen input
	 * 
	 * @param g
	 */
	void drawEndPanel(Graphics g) {
		
		// Drawing panel box
		g.setColor(new Color(191,191,191,191));
		g.fillRect(100, 100, 300, 300);
		g.setColor(Color.BLACK);
		g.drawRect(100, 100, 300, 300);
		
		// Writing labels and descriptions
		g.setFont(bigFont);
		g.drawString("Game Over", 195, 175);
		
		g.setFont(smallFont);
		g.drawString("Your Score: " + Main.score, 195, 245);
		g.drawString("Largest Tile: " + Main.max, 190, 265);
		g.drawString("Press Q to quit.", 195, 310);
		g.drawString("Press R to restart.", 190, 330);
	}
	
	
	
	/**
	 * Displays all blocks that are included in gridData
	 * In other words: displays all "existing" blocks
	 * 
	 * @param g
	 */
	void drawBlocks(Graphics g) {
		Color c;
		
		// Nested loop for searching and drawing all existing blocks
		for(int x = 0; x < gridData.length; x++) {
			for(int y = 0; y < gridData[x].length; y++) {
				
				// Converting data into useful variables
				int value = gridData[x][y];
				String label = value+"";
				if(value == -1) continue;
				
				// Grabbing custom set colors/labels modifications for each block value
				switch(value) {
				case 2: c = new Color(0,127,0,127); label = "   " + label; break;
				case 4: c = new Color(0,255,0,127); label = "   " + label; break;
				case 8: c = new Color(255,255,0,127); label = "   " + label; break;
				case 16: c = new Color(255,196,0,127); label = "  " + label; break;
				case 32: c = new Color(255,127,0,127); label = "  " + label; break;
				case 64: c = new Color(255,0,0,127); label = "  " + label; break;
				case 128: c = new Color(255,0,127,191); label = " " + label; break;
				case 256: c = new Color(255,0,255,191); label = " " + label; break;
				case 512: c = new Color(127,0,255,191); label = " " + label; break;
				case 1024: c = new Color(0,0,255,255); break;
				case 2048: c = new Color(0,191,255,255); break;
				default: c = new Color(255,255,255,127); break;
				}
				
				// Drawing block with the selected color
				drawBlock(50+x*100, 50+y*100, 10, c, label, g);
			}
		}
	}
	
	
	
	/**
	 * Draws virtual, moving and static blocks that are in between grid slots
	 * Is not called along with drawBlocks method
	 * 
	 * The entire animation is divided into <Main.maxMotionFrame> frames, and the current frame is <Main.motionFrame>
	 * Main.motionFrame increases upto Main.maxMotionFrame as Main.animate method iterates window update and calls 
	 * this method frame by frame
	 * 
	 * @param g
	 */
	void drawMovingBlocks(Graphics g) {
		
		// Drawing all moving blocks from animation queue
		for(int i = 0; i < Grid.motionList.size(); i++) {
			
			// Converting data into useful variables
			Integer[] data = Grid.motionList.get(i);
			Color c;
			int x1 = data[0]*100+50, y1 = data[1]*100+50, x2 = data[2]*100+50, y2 = data[3]*100+50, value = data[4];
			String label = value+"";
			if(value == -1) continue;
			
			// Grabbing custom set colors/labels modifications for each block value
			switch(value) {
			case 2: c = new Color(0,127,0,127); label = "   " + label; break;
			case 4: c = new Color(0,255,0,127); label = "   " + label; break;
			case 8: c = new Color(255,255,0,127); label = "   " + label; break;
			case 16: c = new Color(255,196,0,127); label = "  " + label; break;
			case 32: c = new Color(255,127,0,127); label = "  " + label; break;
			case 64: c = new Color(255,0,0,127); label = "  " + label; break;
			case 128: c = new Color(255,0,127,191); label = " " + label; break;
			case 256: c = new Color(255,0,255,191); label = " " + label; break;
			case 512: c = new Color(127,0,255,191); label = " " + label; break;
			case 1024: c = new Color(0,0,255,255); break;
			case 2048: c = new Color(0,191,255,255); break;
			default: c = new Color(255,255,255,127); break;
			}
			
			// Drawing block with the selected color
			drawBlock(x1+((x2-x1)/Main.maxMotionFrame*Main.motionFrame), y1+((y2-y1)/Main.maxMotionFrame*Main.motionFrame), 10, c, label, g);
		}
		
		// Drawing static blocks (unanimated blocks)
		Color c;
		for(int x = 0; x < Grid.staticGrid.length; x++) {
			for(int y = 0; y < Grid.staticGrid[x].length; y++) {
				
				// Converting data into useful variables
				int value = Grid.staticGrid[x][y];
				String label = value+"";
				if(value == -1) continue;
				
				// Grabbing custom set colors/labels modifications for each block value
				switch(value) {
				case 2: c = new Color(0,127,0,127); label = "   " + label; break;
				case 4: c = new Color(0,255,0,127); label = "   " + label; break;
				case 8: c = new Color(255,255,0,127); label = "   " + label; break;
				case 16: c = new Color(255,196,0,127); label = "  " + label; break;
				case 32: c = new Color(255,127,0,127); label = "  " + label; break;
				case 64: c = new Color(255,0,0,127); label = "  " + label; break;
				case 128: c = new Color(255,0,127,191); label = " " + label; break;
				case 256: c = new Color(255,0,255,191); label = " " + label; break;
				case 512: c = new Color(127,0,255,191); label = " " + label; break;
				case 1024: c = new Color(0,0,255,255); break;
				case 2048: c = new Color(0,191,255,255); break;
				default: c = new Color(255,255,255,127); break;
				}
				
				// Drawing block with the selected color
				drawBlock(50+x*100, 50+y*100, 10, c, label, g);
			}
		}
	}
	
	
	
	/**
	 * Draws virtual spawning blocks that appear as slowly increasing in height
	 * Is not called along with drawBlocks method
	 * Is called after drawMovingBlocks method
	 * 
	 * Animation works similarly to drawMovingBlocks method (how Main.maxMotionFrame and Main.motionFrame variables
	 * determine the current frame of this spawning animation)
	 * 
	 * @param g
	 */
	void drawSpawningBlock(Graphics g) {
		
		// Converting data into useful variables
		int x = Grid.newBlock[0]*100+50, y = Grid.newBlock[1]*100+50, value = Grid.newBlock[2];
		String label = value + "";
		Color c;
		
		// Grabbing custom set colors/labels modifications for each block value
		switch(value) {
		case 2: c = new Color(0,127,0,127); label = "   " + label; break;
		case 4: c = new Color(0,255,0,127); label = "   " + label; break;
		case 8: c = new Color(255,255,0,127); label = "   " + label; break;
		case 16: c = new Color(255,196,0,127); label = "  " + label; break;
		case 32: c = new Color(255,127,0,127); label = "  " + label; break;
		case 64: c = new Color(255,0,0,127); label = "  " + label; break;
		case 128: c = new Color(255,0,127,191); label = " " + label; break;
		case 256: c = new Color(255,0,255,191); label = " " + label; break;
		case 512: c = new Color(127,0,255,191); label = " " + label; break;
		case 1024: c = new Color(0,0,255,255); break;
		case 2048: c = new Color(0,191,255,255); break;
		default: c = new Color(255,255,255,127); break;
		}
		
		// Drawing block with the selected color
		drawBlock(x, y, (int)(10/(double)Main.maxMotionFrame*(double)Main.motionFrame), c, label, g);
	}
	
	
	
	/**
	 * Draws a block at specified location and of specified height, color, and label
	 * Coordinates must correspond to actual coordinates in the JPanel, not the 0-4 coordinate
	 * system used in Grid class.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param c
	 * @param label
	 * @param g
	 */
	void drawBlock(int x, int y, int z, Color c, String label, Graphics g) {
		
		// Original coordinates/dimensions of the block
		int x0 = x;
		int y0 = y;
		int l = 99;
		
		// Conversion into coordinates used in Grid class; these will be used for determining how the block will be displayed
		x = x/100;
		y = y/100;
		
		// Extended coordinates of the block; represents the "lifted" square
		int xE = (x-2)*z;
		int yE = (y-2)*z;
		
		// Drawing filled, colored hexagons which represents the 3D block
		g.setColor(c);
		if(x < 2 && y < 2) g.fillPolygon(new int[] {x0+xE,x0+xE+l+z,x0+l,x0+l,x0,x0+xE}, new int[] {y0+yE,y0+yE,y0,y0+l,y0+l,y0+yE+l+z}, 6);
		else if(x > 1 && y < 2) g.fillPolygon(new int[] {x0,x0+xE,x0+xE+l+z,x0+xE+l+z,x0+l,x0}, new int[] {y0,y0+yE,y0+yE,y0+yE+l+z,y0+l,y0+l}, 6);
		else if(x > 1 && y > 1) g.fillPolygon(new int[] {x0,x0+l,x0+xE+l+z,x0+xE+l+z,x0+xE,x0}, new int[] {y0,y0,y0+yE,y0+yE+l+z,y0+yE+l+z,y0+l}, 6);
		else g.fillPolygon(new int[] {x0+xE,x0+xE,x0,x0+l,x0+l,x0+xE+l+z}, new int[] {y0+yE+l+z,y0+yE,y0,y0,y0+l,y0+yE+l+z}, 6);
		
		// Drawing 3D outlines of the block
		g.setColor(Color.BLACK);
		g.drawRect(x0+xE, y0+yE, l+z, l+z);
		g.drawRect(x0, y0, l, l);
		g.drawLine(x0, y0, x0+xE, y0+yE);
		g.drawLine(x0+l, y0, x0+xE+l+z, y0+yE);
		g.drawLine(x0, y0+l, x0+xE, y0+yE+l+z);
		g.drawLine(x0+l, y0+l, x0+xE+l+z, y0+yE+l+z);
		
		// Drawing the label of the block
		g.setFont(hugeFont);
		g.drawString(label, x0+13+xE/2, y0+62+yE/2);
	}
	
	
	
	/**
	 * Draws the grid along with other background features
	 * These featues include:
	 * 	- Grid
	 * 	- Grid border
	 * 	- Scoreboard
	 * 
	 * @param g
	 */
	void drawGrid(Graphics g) {
		
		// Drawing grid and grid borders
		g.setColor(Color.DARK_GRAY);
		g.fillRect(5,5,490,490);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(50,50,400,400);
		g.setColor(Color.BLACK);
		g.drawRect(50,50,400,400);
		g.drawLine(50,150,450,150);
		g.drawLine(50,250,450,250);
		g.drawLine(50,350,450,350);
		g.drawLine(150,50,150,450);
		g.drawLine(250,50,250,450);
		g.drawLine(350,50,350,450);
		
		// Drawing scoreboard
		g.setColor(Color.WHITE);
		g.setFont(bigFont);
		g.drawString("Score: " + Main.score, 60, 27);
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(smallFont);
		g.drawString("Largest Tile: " + Main.max, 60, 43);
	}
}
