import dynamicprogramming.*;

SeamCarving carver;
ArrayList<PVector> seam;

PImage img;

void setup() {
  img = loadImage("arch_sunset.jpg");
  size(img.width,img.height);
  
  println(img.width + " " + img.height);
  
  carver = new SeamCarving(this, img);
  seam = carver.findMinSeam();
}

void draw() {
  image(img, 0,0);
  stroke(255,0,0);
  for(PVector p : seam){
    point(p.x, p.y);
  }
}

void keyPressed(){
  img = carver.removeColumn();
  carver.setImage(img);
}
