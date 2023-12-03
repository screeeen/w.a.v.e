import themidibus.*;

import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

Minim minim;
AudioInput input;

int cols,rows;
int w = 1800;
int h = 600;
int scl = 16;
float fly = 0;
float [][] terrain ;
int sensitivity = 50;


void setup() {
  //size(400, 400, P3D);q
  fullScreen(P3D);
  frameRate(30);
   cols = w/scl;
   rows = h /scl;
  terrain = new float[cols][rows];
  
  minim = new Minim(this);
  input = minim.getLineIn();
  //in = minim.getLineIn();
}


void draw () {
  fly -=.05;
  float yoff = fly;
  for ( int y = 0; y<rows; y++) {
    float xoff = 0;
    for (int x = 0; x<cols-1; x++) {
      terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100,abs(input.left.get(y)*sensitivity));
      //terrain[x][y] = map(-in.left.get(y)*yoff,0,1,-100,100);
      xoff +=0.1;
    }
    yoff += 0.1;
  }

  background(0);
  rotateX(PI/3.5);
  translate(-width/2, h/2);
  stroke(255);
  noFill();

    for (int y = 0; y<rows-1; y++) {
      beginShape(TRIANGLE_STRIP);
      for ( int x = 0; x<cols; x++) {

      vertex(x*scl, y*scl, terrain[x][y]);
      vertex(x*scl, (y+1)*scl, terrain[x][y+1]);
    }
    endShape();
  }
}