package dynamicprogramming;

/*
 * 
 * Based on work by Paul Reiners
 * http://www.ibm.com/developerworks/java/library/j-seqalign/index.html
 * 
 */

public class Cell {
	   private Cell prevCell;
	   private int score;
	   private int row;
	   private int col;
	   private boolean isRightEdge;
	   private boolean exists;
	   
	   public Cell(int row, int col) {
	      this.row = row;
	      this.col = col;
	      this.exists = true;
	   }
	   
	   public boolean doesExist(){
		   return exists;
	   }
	   
	   public void setExists(boolean b){
		   this.exists = b;
	   }
	   
	   public boolean isLeftEdge(){
		   return col == 0;
	   }
	   
	   public boolean isRightEdge(){
		   return isRightEdge;
	   }
	   
	   public void setRightEdge(boolean e){
		   isRightEdge = e;
	   }
	   

	   /**
	    * @param score
	    *           the score to set
	    */
	   public void setScore(int score) {
	      this.score = score;
	   }
	   
	   public void setScore(float score){
		   this.score = (int)score;
		   
	   }

	   /**
	    * @return the score
	    */
	   public int getScore() {
	      return score;
	   }

	   /**
	    * @param prevCell
	    *           the prevCell to set
	    */
	   public void setPrevCell(Cell prevCell) {
	      this.prevCell = prevCell;
	   }

	   /**
	    * @return the row
	    */
	   public int getRow() {
	      return row;
	   }

	   /**
	    * @return the col
	    */
	   public int getCol() {
	      return col;
	   }

	   /**
	    * @return the prevCell
	    */
	   public Cell getPrevCell() {
	      return prevCell;
	   }

	   /*
	    * (non-Javadoc)
	    * 
	    * @see java.lang.Object#toString()
	    */
	   @Override
	   public String toString() {
	      return "Cell(" + row + ", " + col + "): score=" + score + ", prevCell="
	            + prevCell + "]";
	   }
}
