import dynamicprogramming.*;

SmithWaterman align;
String text1 = "GCCCTAGCG";
String text2 = "GCGC-AATG";

void setup(){
  align = new SmithWaterman(text1, text2);
  println(align.getAlignment());
  println(align.getAlignmentScore());
}

void draw(){
}
