package dynamicprogramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import processing.core.*;


public class SeamCarving extends DynamicProgramming {
	private PApplet parent;
	private PImage img;
	private float[][] weights;
	
	public SeamCarving(PApplet parent, PImage img){
		super(img.width, img.height);

		this.parent = parent;
		this.img = img;
		this.weights = pixelGradients(img);
	}
	
	public void setImage(PImage img){
		this.img = img;
		weights = pixelGradients(img);
		resetTable(img.width, img.height);
	}
	
	public PImage removeColumn(){
		PImage result = parent.createImage(img.width - 1, img.height, parent.RGB);
		ArrayList<PVector> seam = findMinSeam();
		for(PVector p : seam){
			
			int origRowBeginning  = (int)p.y * img.width;
			int destRowBeginning = (int)p.y * result.width;
			
			// if there are pixels to copy to the left of the seam
			// copy them into the new image
			if(p.x > 0){
				
				
				System.arraycopy(img.pixels, origRowBeginning, result.pixels, destRowBeginning, (int)p.x);				 
			}
			
			// if there are pixels to copy to the right of the seam
			// copy them in as well
			if(p.x < img.width - 1){				
				System.arraycopy(img.pixels, origRowBeginning + (int)p.x+1, result.pixels, destRowBeginning + (int)p.x, result.width - (int)p.x - 1);				 
			}
			
		}
		
		return result;
	}
	
	public ArrayList<PVector> findMinSeam(){
		ArrayList<PVector> result = new ArrayList<PVector>();
		
		int[][] scoreTable = getScoreTable();

		int[] seamIndices = new int[img.height];
		int firstIndex = findMinSeamIndex(scoreTable);
		
		//seamIndices[img.height - 1] = firstIndex;
		
		result.add(new PVector(firstIndex, img.height - 1));

		
		int nextCol = firstIndex;
		
		for (int row = scoreTable.length - 1; row > 0; row--) {
			//seamIndices[row] = nextCol;
			
			result.add(new PVector(nextCol, row));

			nextCol = findNextCol(scoreTable, row, nextCol);
		}
		
		//seamIndices[0] = nextCol;
		result.add(new PVector(nextCol, 0));
		
		//return seamIndices;
		return result;
	}
	
	private int findMinSeamIndex(int[][] table) {
		int lastRow = table.length - 1;

		int minIndex = 0;
		float minValue = table[lastRow][0];

		for (int j = 1; j < table[0].length; j++) {
			if (table[lastRow][j] < minValue) {
				minIndex = j;
				minValue = table[lastRow][j];
			}
		}

		return minIndex;
	}
	
	/**
	 * From a certain (row,col) in the DP table, determines the previous smallest
	 * path and returns which column it is in.
	 */
	private int findNextCol(int[][] table, int row, int col) {
		// Checks for DP table of width 1
		if (table[0].length == 1)
			return 0;

		// if at the left border only considers above and above to the right
		if (col == 0) {
			if (table[row - 1][0] < table[row - 1][1])
				return 0;
			else
				return 1;
		}

		// if at the right border only considers above and above to the left
		if (col == table[0].length - 1) {
			if (table[row - 1][table[0].length - 2] < table[row - 1][table[0].length - 1])
				return table[0].length - 2;
			else
				return table[0].length - 1;
		}

		// otherwise looks at above left, center, and right
		return scanThreeAbove(table, row, col);

	}
	
	/**
	 * Determines which column leads to smallest Cell for a certain
	 * (row,col) and returns the column
	 */
	private int scanThreeAbove(int[][] table, int row, int col) {
		int minIndex = col - 1;
		float minValue = table[row - 1][col - 1];

		for (int k = 0; k < 3; k++) {
			if (table[row - 1][col - 1 + k] < minValue) {
				minValue = table[row - 1][col - 1 + k];
				minIndex = col - 1 + k;
			}
		}

		return minIndex;
	}
	
	/* BEGIN DynamicProgramming methods */
	
	protected Cell getInitialPointer(int row, int col){
		return new Cell(0,0);   // i.e. who cares?
	}

	protected int getInitialScore(int row, int col){
		return (int)weights[row][col];
	}

	protected void fillInCell(Cell currentCell, Cell cellAbove, Cell cellToLeft, Cell cellAboveLeft, Cell cellAboveRight){	   

		if(currentCell.isRightEdge()){
			//			//		+ Math.min(table[i - 1][j], table[i - 1][j - 1]);
			
			currentCell.setScore(currentCell.getScore() + Math.min(cellAbove.getScore(), cellAboveLeft.getScore()));
			 
		} else {
		
			currentCell.setScore(currentCell.getScore() + Collections.min(Arrays.asList(cellAboveLeft.getScore(), cellAbove.getScore(), cellAboveRight.getScore())));
		}
		//Collections.min(Arrays.asList(table[i - 1][j - 1],table[i - 1][j], table[i - 1][j + 1]));
		
		//return (int)(weights[col][row] + Collections.min(Arrays.asList(weights[col-1][row-1], weights[col-1][row], weights[col-1][row+1])));

		
		// checks for single col image
		/*if (row == 0 && row == weights[0].length - 1) {
			return (int)(weights[col][row] + weights[col - 1][row]);
			//	return image.get(i, j) + table[i - 1][j];
		}

		// if on the left edge does not consider going to the left
		if (row == 0) {
			//return image.get(i, j)
			//		+ Math.min(table[i - 1][j], table[i - 1][j + 1]);
			
			return (int)(weights[col][row] + Math.min(weights[col-1][row], weights[col-1][row+1]));
		}

		// if on the right edge does not consider going to the right
		if (row == weights[0].length - 1) {
			//return image.get(i, j)
			//		+ Math.min(table[i - 1][j], table[i - 1][j - 1]);
			return (int)(weights[col][row] + Math.min(weights[col-1][row], weights[col-1][row-1]));
		}

		// otherwise looks at the left, center, and right possibilities as mins
		return (int)(weights[col][row] + Collections.min(Arrays.asList(weights[col-1][row-1], weights[col-1][row], weights[col-1][row+1])));
		//return image.get(i, j)
		//		+ Collections.min(Arrays.asList(table[i - 1][j - 1],
		//				table[i - 1][j], table[i - 1][j + 1]));
		 */
		
	}
	
	/* END DynamicProgramming methods */
	
	
	
	public float[][] getWeights(){
		return weights;
	}
	
	public float[][] pixelGradients(PImage original){
		float[][] result = new float[original.height][original.width];
		for (int y = 0; y < original.height; y++) {
		    for (int x = 0; x < original.width; x++) {
		      result[y][x] = parent.color(pixelGradient(original, x, y));
		    }
		}
		
		return result;
	}
	
	public float pixelGradient(PImage img, int x, int y) {
		float point = parent.brightness(img.pixels[x + y*img.width]);
		float p1, p2 = 0;

		if (y == img.height - 1 && x == img.width - 1) {
		    p1 = (float) Math.pow(point, 2);
		    p2 = (float) Math.pow(point, 2);
		    return (float) Math.sqrt(p1 + p2);
		}

		if (y == img.height - 1) {
		    p1 = (float) Math.pow((parent.brightness(img.pixels[x+1 + y*img.width]) - point), 2);
		    p2 = (float) Math.pow(point, 2);
		    return (float) Math.sqrt(p1 + p2);
		}

		if (x == img.width - 1) {
		    p1 = (float) Math.pow(point, 2);      
		    p2 = (float) Math.pow((parent.brightness(img.pixels[x + (y+1)*img.width]) - point), 2);
		    return (float) Math.sqrt(p1 + p2);
		}

		if (x == img.width - 1) {
		    p1 = (float) Math.pow(point, 2);


		    p2 = (float) Math.pow((parent.brightness(img.pixels[x+(y+1)*img.width]) - point), 2);
		    return (float) Math.sqrt(p1 + p2);
		 }


		 p1 = (float) Math.pow((parent.brightness(img.pixels[x+1 + y*img.width]) - point), 2);
		 p2 = (float) Math.pow((parent.brightness(img.pixels[x+(y+1)*img.width]) - point), 2);

		 return (float) Math.sqrt(p1 + p2);
	}
	

}
