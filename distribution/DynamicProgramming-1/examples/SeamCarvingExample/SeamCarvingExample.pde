import dynamicprogramming.*;

SeamCarving carver;
ArrayList<PVector> seam;

PImage img, energyImage, scoreImage;

void setup() {
  img = loadImage("arch_sunset.jpg");
  
  size(img.width*3,img.height);
  
  println(img.width + " " + img.height);
  
    carver = new SeamCarving(this, img);
    seam = carver.findMinSeam();
    
    println("calculating energy image");
    energyImage = carver.getGradientImage();
    
    println("calculating score image");
    scoreImage = carver.getScoreImage();

   image(scoreImage,img.width,0);

}

boolean newSeam = false;

void draw() {
  // draw a white "background"
  // behind the final result (since it shrinks)
  fill(255);
  noStroke();
  rect(energyImage.width*2, 0, energyImage.width, img.height);

  image(energyImage, 0, 0);
  image(img, energyImage.width*2, 0);

  if(newSeam){
    pushMatrix();
    translate(energyImage.width,0);
    // draw a white "background"
    // behind the energy image (since it also shrinks)
    fill(255);
    noStroke();
    rect(0, 0, energyImage.width, img.height);
    
    // draw the score image
    image(scoreImage, 0,0 );
    
    // draw the next seam to remove
    stroke(255,0,0);
    for(PVector p : seam){
      point(p.x, p.y);
    }
    popMatrix();
    newSeam = false;
  }
}

void keyPressed(){
  img.loadPixels();
  img = carver.removeColumn();
  img.updatePixels();
  
  carver.setImage(img);
  seam = carver.findMinSeam();
  newSeam = true;
  
  println("calculating score image");
  scoreImage = carver.getScoreImage();

}
