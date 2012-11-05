import dynamicprogramming.*;


String text1 = "GCCCTAGCG";
String text2 = "GCGCAATG";

LongestCommonSubsequence lcs;

void setup() {
  size(500, 500);
  lcs = new LongestCommonSubsequence(text1, text2);
  println(lcs.getLongestCommonSubsequence());
}

void draw() {
}

