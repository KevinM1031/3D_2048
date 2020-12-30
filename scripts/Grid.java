package scripts;

import java.util.ArrayList;
import java.util.List;



/**
 * Grid class
 * 
 * This class is manages and performs calculations regarding the game board (Grid) and its objects (Blocks).
 * Functions include creating new random blocks, calculating new positions when blocks are shifted, and
 * queuing new data to be executed in the next animation.
 * 
 * @author Kevin1031
 */
public class Grid {
	
	// 2D integer array representing the 4x4 game board; each slot value represents the block's number 
	static int[][] grid = new int[][] {
		{-1,-1,-1,-1},
		{-1,-1,-1,-1},
		{-1,-1,-1,-1},
		{-1,-1,-1,-1}};
	
	// Stores positions of static blocks that will not move during next animation
	static int[][] staticGrid = new int[][] {
		{-1,-1,-1,-1},
		{-1,-1,-1,-1},
		{-1,-1,-1,-1},
		{-1,-1,-1,-1}};
	
	// Stores positions of dynamic blocks that will be moved during next animation
	static List<Integer[]> motionList = new ArrayList<Integer[]>();
	
	// Stores the position of new block that will randomly appear; this variable is for the sake of animation only
	static int[] newBlock = new int[3];
	
	
	
	/**
	 * Resets the entire grid into start layout
	 */
	static void refresh() {
		
		// Emptying all slots (-1 indicates "empty")
		for(int x = 0; x <= 3; x++) {
			for(int y = 0; y <= 3; y++) {
				grid[x][y] = -1;
			}
		}
		
		// Generating first 4 random blocks
		addRandomBlock();
		addRandomBlock();
		addRandomBlock();
		addRandomBlock();
	}
	
	
	
	/**
	 * UNUSED (for debugging purposes only)
	 * Adds a block of specific position and value
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	static void addBlock(int x, int y, int value) {
		grid[x][y] = value;
	}
	
	
	
	/**
	 * Adds a block at random empty slot
	 * 80% - block has a value of 2
	 * 20% - block has a value of 4
	 */
	static void addRandomBlock() {
		
		// Temporary list for containing empty positions
		List<Integer[]> emptyList = new ArrayList<Integer[]>();
		
		// Searching all empty positions and storing them in emptyList
		for(int x = 0; x <= 3; x++) {
			for(int y = 0; y <= 3; y++) {
				if(grid[x][y] == -1) {
					
					// Randomizing the block value (2 or 4; 0.8:0.2)
					int r = (int)(Math.random()*4);
					if(r < 3) emptyList.add(new Integer[] {x, y, 2});
					else emptyList.add(new Integer[] {x, y, 4});
				}
			}
		}
		
		// Randomly selecting one empty slot and placing a block into it
		Integer[] block = emptyList.get((int)(Math.random()*emptyList.size()));
		grid[block[0]][block[1]] = block[2];
		spawnBlock(block[0],block[1],block[2]);
	}

	
	
	/**
	 * Shifts the blocks to one of the four directions (up, down, left, right)
	 * W = up
	 * S = down
	 * D = right
	 * A = left
	 * 
	 * Also deals with merging blocks of same values
	 * When a block was merged once during a shift, it cannot merge again until the next shift
	 * Such "merged blocks" are marked by temporarily converting its value into a negative; negative value blocks are merge-proof
	 * 
	 * @param dir
	 */
	static void shift(char dir) {
		boolean moved = false;
		String log = "";
		
		// First copying data from grid into staticGrid
		// When moving blocks are found, its coordinates will be erased (set as -1) in staticGrid
		for(int x = 0; x <= 3; x++) {
			for(int y = 0; y <= 3; y++) {
				staticGrid[x][y] = grid[x][y];
			}
		}
		
		switch(dir) {
		
		// UP
		case 'W':
			
			log += "Input received: 'W'. ";
			for(int x = 0; x <= 3; x++) {
				
				// Recheck indicates that the entire column must be checked again since one movement
				// may allow for new movement possibilities
				boolean recheck = true;
				while(recheck) {
					recheck = false;
					
					// Inner loop: checks individual blocks in the column
					for(int y = 1; y <= 3; y++) {
						int value = grid[x][y], next = grid[x][y-1];
						if(value == -1) continue;
						
						// Checks if there is an empty slot above; shifts if true
						if(next == -1) {
							grid[x][y] = -1;
							grid[x][y-1] = value;
							moveBlock(dir,x,y,x,y-1,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
							
						// Checks if there is an identical block above; merges if true
						} else if(next == value && value > 0) {
							grid[x][y] = -1;
							grid[x][y-1] = -(value+next);
							moveBlock(dir,x,y,x,y-1,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
						}
					}
				}
			}
			
			break;
			
		// DOWN
		case 'S':
			
			log += "Input received: 'S'. ";
			for(int x = 0; x <= 3; x++) {
				
				// Recheck indicates that the entire column must be checked again since one movement
				// may allow for new movement possibilities
				boolean recheck = true;
				while(recheck) {
					recheck = false;
					
					// Inner loop: checks individual blocks in the column
					for(int y = 2; y >= 0; y--) {
						int value = grid[x][y], next = grid[x][y+1];
						if(value == -1) continue;
						
						// Checks if there is an empty slot below; shifts if true
						if(next == -1) {
							grid[x][y] = -1;
							grid[x][y+1] = value;
							moveBlock(dir,x,y,x,y+1,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
							
						// Checks if there is an identical block below; merges if true
						} else if(next == value && value > 0) {
							grid[x][y] = -1;
							grid[x][y+1] = -(value+next);
							moveBlock(dir,x,y,x,y+1,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
						}
					}
				}
			}
			
			break;
			
		// RIGHT
		case 'D':
			
			log += "Input received: 'D'. ";
			for(int y = 0; y <= 3; y++) {
				
				// Recheck indicates that the entire row must be checked again since one movement
				// may allow for new movement possibilities
				boolean recheck = true;
				while(recheck) {
					recheck = false;
					
					// Inner loop: checks individual blocks in the row
					for(int x = 2; x >= 0; x--) {
						int value = grid[x][y], next = grid[x+1][y];
						if(value == -1) continue;
						
						// Checks if there is an empty slot on the right; shifts if true
						if(next == -1) {
							grid[x][y] = -1;
							grid[x+1][y] = value;
							moveBlock(dir,x,y,x+1,y,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
							
						// Checks if there is an identical block on the right; merges if true
						} else if(next == value && value > 0) {
							grid[x][y] = -1;
							grid[x+1][y] = -(value+next);
							moveBlock(dir,x,y,x+1,y,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
						}
					}
				}
			}
			
			break;
			
		// LEFT
		case 'A':
			
			log += "Input received: 'A'. ";
			for(int y = 0; y <= 3; y++) {
				
				// Recheck indicates that the entire row must be checked again since one movement
				// may allow for new movement possibilities
				boolean recheck = true;
				while(recheck) {
					recheck = false;
					
					// Inner loop: checks individual blocks in the row
					for(int x = 1; x <= 3; x++) {
						int value = grid[x][y], next = grid[x-1][y];
						if(value == -1) continue;
						
						// Checks if there is an empty slot on the left; shifts if true
						if(next == -1) {
							grid[x][y] = -1;
							grid[x-1][y] = value;
							moveBlock(dir,x,y,x-1,y,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
							
						// Checks if there is an identical block on the left; merges if true
						} else if(next == value && value > 0) {
							grid[x][y] = -1;
							grid[x-1][y] = -(value+next);
							moveBlock(dir,x,y,x-1,y,value);
							staticGrid[x][y] = -1;
							moved = recheck = true;
						}
					}
				}
			}
			
			break;
		}
		
		// Restoring negative value "merged blocks" back with their positive values
		for(int x = 0; x <= 3; x++) {
			for(int y = 0; y <= 3; y++) {
				if(grid[x][y] < -1) {
					grid[x][y] *= -1;
				}
			}
		}
		
		// Checking if valid move occurred; if so, perform actions below:
		if(moved) {
			addRandomBlock();
			Main.score++;
			Main.max = getMax();
			log += "Valid move. Current max: " + Main.max + ". Total move count (score): " + Main.score;
			
		// If no valid moves occurred:
		} else {
			log += "Invalid move.";
			
			// If nothing can be moved anymore -> game over
			if(!canMove()) Main.over();
		}
		
		// Print log
		Main.printLog(log);
	}
	
	
	
	/**
	 * Searches and returns the block with the highest value in the current Grid
	 * 
	 * @return max
	 */
	static int getMax() {
		int max = 0;
		for(int x = 0; x <= 3; x++) {
			for(int y = 0; y <= 3; y++) {
				if(grid[x][y] > max) {
					max = grid[x][y];
				}
			}
		}
		return max;
	}
	
	
	
	/**
	 * Quickly attempts all four possible moves and checks if any valid moves are possible in the 
	 * 
	 * @return canMove
	 */
	static boolean canMove() {
			
		for(int x = 0; x <= 3; x++) {
			for(int y = 1; y <= 3; y++) {
				int value = grid[x][y], next = grid[x][y-1];
				if(value == -1) continue;
				
				if(next == -1 || next == value && value > 0) return true;
			}
		}
		
		for(int x = 0; x <= 3; x++) {
			for(int y = 2; y >= 0; y--) {
				int value = grid[x][y], next = grid[x][y+1];
				if(value == -1) continue;
				
				if(next == -1 || next == value && value > 0) return true;
			}
		}
		
		for(int y = 0; y <= 3; y++) {
			for(int x = 2; x >= 0; x--) {
				int value = grid[x][y], next = grid[x+1][y];
				if(value == -1) continue;
				
				if(next == -1 || next == value && value > 0) return true;
			}
		}
		
		for(int y = 0; y <= 3; y++) {
			for(int x = 1; x <= 3; x++) {
				int value = grid[x][y], next = grid[x-1][y];
				if(value == -1) continue;
				
				if(next == -1 || next == value && value > 0) return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Manages storing new data queue for block moving animations
	 * If there are multiple queue requests that are "chained", such as:
	 * 		(1) 1 -> 2
	 * 		(2) 2 -> 3
	 * 		(3) 4 -> 5
	 * the two requests are combined into:
	 * 		(1+2+3) 1 -> 5
	 * instead of adding a new animation queue
	 * 
	 * @param dir
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param value
	 */
	static void moveBlock(char dir, int x1, int y1, int x2, int y2, int value) {
		boolean continued = false;
		
		// Attempting to find a "chained" queue with the newly requested queue
		// If found, only the data of the "chained" queue is modified; new queue is not made
		switch(dir) {
		
		// UP
		case 'W':
			for(int i = 0; i < motionList.size(); i++) {
				if(motionList.get(i)[0] == x1 && motionList.get(i)[1] > y1) {
					motionList.get(i)[2] = x2;
					motionList.get(i)[3] = y2;
					continued = true;
				}
			}
			break;
			
		// DOWN
		case 'S':
			for(int i = 0; i < motionList.size(); i++) {
				if(motionList.get(i)[0] == x1 && motionList.get(i)[1] < y1) {
					motionList.get(i)[2] = x2;
					motionList.get(i)[3] = y2;
					continued = true;
				}
			}
			break;
			
		// RIGHT
		case 'D':
			for(int i = 0; i < motionList.size(); i++) {
				if(motionList.get(i)[0] < x1 && motionList.get(i)[1] == y1) {
					motionList.get(i)[2] = x2;
					motionList.get(i)[3] = y2;
					continued = true;
				}
			}
			break;
			
		// LEFT
		case 'A':
			for(int i = 0; i < motionList.size(); i++) {
				if(motionList.get(i)[0] > x1 && motionList.get(i)[1] == y1) {
					motionList.get(i)[2] = x2;
					motionList.get(i)[3] = y2;
					continued = true;
				}
			}
			break;
		}
		
		// If the new queue is not "chained" with any other queues, it becomes a new queue
		if(!continued) motionList.add(new Integer[] {x1,y1,x2,y2,value});
	}
	
	
	
	/**
	 * Stores data of the newly generated tile
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	static void spawnBlock(int x, int y, int value) {
		newBlock[0] = x;
		newBlock[1] = y;
		newBlock[2] = value;
	}

}