import dynamicprogramming.*;

SeamCarving carver;
ArrayList<PVector> seam;

PImage img, demoImage, weightImage, scoreImage;

void setup() {
  img = loadImage("arch_sunset.jpg");
  demoImage = loadImage("arch_sunset.jpg");
  
  size(img.width*4,img.height);
  
  println(img.width + " " + img.height);
  
    carver = new SeamCarving(this, img);
    seam = carver.findMinSeam();
    
    println("calculating gradient image");
    weightImage = carver.getGradientImage();
    println("calculating  image");
    scoreImage = carver.getScoreImage();

   image(scoreImage,0,0);

}

boolean newSeam = false;

void draw() {
 // background(255,0,0);
  image(weightImage,demoImage.width*2,0);

  fill(255);
  noStroke();
  rect(demoImage.width,0, demoImage.width, demoImage.height);
  image(img, demoImage.width,0);
  stroke(255,0,0);
  if(newSeam){
    //image(scoreImage, 0,0 );
    for(PVector p : seam){
      point(p.x, p.y);
    }
    
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
  
  
  //println("calculating score image");
  //scoreImage = carver.getScoreImage();

}
