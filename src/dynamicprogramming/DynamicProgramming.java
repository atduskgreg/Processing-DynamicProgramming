/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package dynamicprogramming;


import processing.core.*;

/**
 * This is a template class and can be used to start a new processing library or tool.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own library or tool naming convention.
 * 
 * @example Hello 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */



public abstract class DynamicProgramming {
   public final static String VERSION = "##library.prettyVersion##";
	
   protected int tableWidth;
   protected int tableHeight;
   protected Cell[][] scoreTable;
   protected boolean tableIsFilledIn;
   protected boolean isInitialized;

   public DynamicProgramming(int tableWidth, int tableHeight) {
      this.tableWidth = tableWidth;
      this.tableHeight = tableHeight;
      scoreTable = new Cell[tableHeight][tableWidth];
   }
   
   public int[][] getScoreTable() {
      ensureTableIsFilledIn();

      int[][] matrix = new int[scoreTable.length][scoreTable[0].length];
      for (int i = 0; i < matrix.length; i++) {
         for (int j = 0; j < matrix[i].length; j++) {
            matrix[i][j] = scoreTable[i][j].getScore();
         }
      }

      return matrix;
   }

   protected void initializeScores() {
      for (int i = 0; i < scoreTable.length; i++) {
         for (int j = 0; j < scoreTable[i].length; j++) {
            scoreTable[i][j].setScore(getInitialScore(i, j));
         }
      }
   }

   protected void initializePointers() {
      for (int i = 0; i < scoreTable.length; i++) {
         for (int j = 0; j < scoreTable[i].length; j++) {
            scoreTable[i][j].setPrevCell(getInitialPointer(i, j));
         }
      }
   }

   protected void initialize() {
      for (int i = 0; i < scoreTable.length; i++) {
         for (int j = 0; j < scoreTable[i].length; j++) {
        	Cell c = new Cell(i, j);
            
        	if(j == 0){
            	c.isLeftEdge();
            }
            
            if(j == scoreTable[i].length-1){
            	c.isRightEdge();
            }
            
            scoreTable[i][j] = c;

         }
      }
      initializeScores();
      initializePointers();

      isInitialized = true;
   }

   protected abstract Cell getInitialPointer(int row, int col);

   protected abstract int getInitialScore(int row, int col);

   protected abstract void fillInCell(Cell currentCell, Cell cellAbove,
         Cell cellToLeft, Cell cellAboveLeft, Cell cellAboveRight);

   protected void fillIn() {
      for (int row = 1; row < scoreTable.length; row++) {
         for (int col = 1; col < scoreTable[row].length; col++) {
            Cell currentCell = scoreTable[row][col];
            Cell cellAbove = scoreTable[row - 1][col];
            Cell cellToLeft = scoreTable[row][col - 1];
            Cell cellAboveLeft = scoreTable[row - 1][col - 1];
            Cell cellAboveRight = new Cell(0,0); //f-this
            if(col < scoreTable[row].length-1){
            	cellAboveRight = scoreTable[row - 1][col + 1];
            }
            fillInCell(currentCell, cellAbove, cellToLeft, cellAboveLeft, cellAboveRight);
         }
      }

      tableIsFilledIn = true;
   }
   
   protected void resetTable(int tableWidth, int tableHeight){
	   this.tableWidth = tableWidth;
	   this.tableHeight = tableHeight;
	   scoreTable = new Cell[tableHeight][tableWidth];
	   
	   initialize();
	   fillIn();
   }

	   protected void ensureTableIsFilledIn() {
	      if (!isInitialized) {
	         initialize();
	      }
	      if (!tableIsFilledIn) {
	         fillIn();
	      }
	   }
	}
