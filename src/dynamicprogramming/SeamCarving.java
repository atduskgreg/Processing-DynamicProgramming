package dynamicprogramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import processing.core.*;


public class SeamCarving extends DynamicProgramming {
	private PApplet parent;
	private PImage img, weightImg;
	private float[][] weights;
	private float[][] gradients;
	
	public SeamCarving(PApplet parent, PImage img){
		super(img.width, img.height);

		this.parent = parent;
		this.img = img;
		
		this.gradients = pixelGradients(img);
		this.weightImg = getGradientImage();
		this.weights = new float[img.height][img.width];//pixelGradients(img);	
	}
	
	public PImage getGradientImage(){
		PImage result = parent.createImage(img.width, img.height, PConstants.GRAY);
		
		for(int row = 0; row < img.height; row++){
			for(int col = 0; col < img.width; col++){
				result.pixels[col + row*result.width] = (int)gradients[row][col];
			}
		}
		
		result.updatePixels();
		return result;
		//return weightImg;
	}
	
	public PImage getScoreImage(){
		PImage result = parent.createImage(img.width, img.height, PConstants.RGB);
		int[][] scores = this.getScoreTable();
		
		int maxScore = 0;
		
		for(int row = 0; row < img.height; row++){
			for(int col = 0; col < img.width; col++){
				result.pixels[col + row*result.width] = parent.color(parent.map(scores[row][col], 0, 1500, 0, 255));//parent.color(r,g,b);
			
				if(scores[row][col] > maxScore){
					maxScore = scores[row][col];
					
				}
			}
		}
		
		System.out.println("max score: " + maxScore);
		
		result.updatePixels();
		return result;
		//return weightImg;
	}
	
	public void setImage(PImage img){
		this.img = img;
		this.gradients = pixelGradients(img);
		this.weightImg = getGradientImage();
		this.weights = new float[img.height][img.width];
		resetTable(img.width, img.height);
	}
	
	public PImage removeColumn(){
		PImage result = parent.createImage(img.width - 1, img.height, PConstants.RGB);
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
				System.arraycopy(img.pixels, origRowBeginning + (int)p.x+1, result.pixels, destRowBeginning + (int)p.x, result.width - (int)p.x);				 
			}
			
		
			
			
			
		}
		
		return result;
	}
	
	public ArrayList<PVector> findMinSeam(){
		ArrayList<PVector> result = new ArrayList<PVector>();
		
		int[][] scoreTable = getScoreTable();

		int firstIndex = findMinSeamIndex(scoreTable);		
		result.add(new PVector(firstIndex, img.height - 1));

		
		int nextCol = firstIndex;
		
		for (int row = scoreTable.length - 1; row > 0; row--) {
			//seamIndices[row] = nextCol;
			
			result.add(new PVector(nextCol, row));

			nextCol = findNextCol(scoreTable, row, nextCol);
		}
		
		result.add(new PVector(nextCol, 0));
		
		return result;
	}
	
	private int findMinSeamIndex(int[][] table) {
		int lastRow = table.length - 1;

		int minIndex = 0;
		float minValue = table[lastRow][0];

		for (int j = 1; j < table[lastRow].length; j++) {
			if (table[lastRow][j] < minValue) {
				minIndex = j;
				minValue = table[lastRow][j];
			}
		}

		return minIndex;
		
		
		//return (int)parent.random(table[0].length - 1   );
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
		int minIndex = col;
		float minValue = table[row - 1][col];

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
		return 0;
	}

	protected void fillInCell(Cell currentCell, Cell cellAbove, Cell cellToLeft, Cell cellAboveLeft, Cell cellAboveRight){	   

		int index = currentCell.getCol() + currentCell.getRow()*weightImg.width;
		//parent.println(index);
		
		if (currentCell.getRow() == 0) {
			 currentCell.setScore(parent.brightness(weightImg.pixels[index]) );//image.get(i, j);
		}

		// checks for single col image
		else if (currentCell.isLeftEdge() && currentCell.isRightEdge()) {
			System.out.println("NEVER!");
			currentCell.setScore(parent.brightness(weightImg.pixels[index]) +  cellAbove.getScore()); 
		}

		// if on the left edge does not consider going to the left
		else if (currentCell.isLeftEdge()) {
			currentCell.setScore(parent.brightness(weightImg.pixels[index])
				+ Math.min(cellAbove.getScore(), cellAboveRight.getScore()));
		}

		// if on the right edge does not consider going to the right
		else if (currentCell.isRightEdge()) {
			currentCell.setScore(parent.brightness(weightImg.pixels[index]) + Math.min(cellAbove.getScore(), cellAboveLeft.getScore()));
		}
		
		else {
		// otherwise looks at the left, center, and right possibilities as mins
			currentCell.setScore(parent.brightness(weightImg.pixels[index]) + Collections.min(Arrays.asList(cellAboveLeft.getScore(), cellAbove.getScore(), cellAboveRight.getScore())));
		}
	
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

	


		 p1 = (float) Math.pow((parent.brightness(img.pixels[x+1 + y*img.width]) - point), 2);
		 p2 = (float) Math.pow((parent.brightness(img.pixels[x+(y+1)*img.width]) - point), 2);

		 return (float) Math.sqrt(p1 + p2);
	}
	

}
