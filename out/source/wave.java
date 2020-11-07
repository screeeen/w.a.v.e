import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import themidibus.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class wave extends PApplet {










Minim minim;
//AudioPlayer in;
AudioInput input;
AudioInput in;
MidiBus myBus;

int rows = 8;
int columns = 8;
int currentShape = 0;
int currentSize = 2;
float currentSpeed = 2;
boolean currentRotation = false; 
Module[] mods;
int count;
boolean overlay = true;
String consoleText = "";

public void setup() 
{
  
  //fullScreen(P3D);
  minim = new Minim(this);
  //in = minim.loadFile("song.mp3");
  //input = minim.getLineIn();
  //in.play();
  in = minim.getLineIn();
  // List all our MIDI devices
  MidiBus.list();
  // Connect to one of the devices
  myBus = new MidiBus(this, 0, 1);

  noStroke();
  int highCount = height / rows;
  int wideCount =  width / columns;
  count = wideCount * highCount;
  mods = new Module[count];

  int index = 0;
  for (int yCor = 0; yCor < highCount; yCor++) {
    for(int xCor = 0; xCor < wideCount ; xCor++) // in.bufferSize() - 1
    {
      mods[index++] = new Module (xCor*rows,yCor*columns);
      // consoleText = y,x;
    }
  }
}

public void draw()
{
  background(0);
  for (Module mod : mods) {
    mod.update();
    mod.display();
  }
   if (overlay) {
      fill(100);
      rect(0,height - 22,width,20);
      fill(255);
       text("Z	enable input mode - 1	changes shape	- r	rotates " + consoleText, 0, height-10);


  }

}

// THIS IS THE MODULE

class Module {
  int x;
  int y;
  int colorRange = 1;
  float strokeRange = 255;
  int shape = 0;  
  int size = 2;
  boolean rotation = false;
  // boolean fil = false;
  // boolean str = true;
  // boolean sin = false;
  // float freq = 10;
  float speed = 1;

  Module(int xPos,int yPos){
  x = xPos;
  y =  yPos;
  }

  public void update (){
    if (rotation)
    translate(width/2, height/2);
    rotate(PI*speed);
  }

  public void changeShape (int i) {
    shape = i;
  }

  public void changeRotation (boolean r) {
    rotation = r;
  }

  public void changeSize (int s) {
    size = s;
  }

  public void changeSpeed (float spd) {
    speed = spd;
  }

  public void display() {
    float magnitude = in.left.get(x);
    fill(255);
    switch (shape) {
      case 0: // screen dots      
        ellipse(x, magnitude*size*y+y, size, size);
        break;
      case 1: // waveform
        ellipse(x, width/2 + magnitude*width/2, size,size);
        break;
         case 2: // blocks
        rect(x + size/2, y+size/2+magnitude,size,size);
        break;
      case 3: //triangles
        triangle(x, y+magnitude*size, x+size/2,y+ size+magnitude*size, x+size + magnitude*size, y-size + magnitude*size );
        break;
      case 4: // circles
          ellipse(x, y, magnitude*size, magnitude*size);
        break;
      case 5: // circles
          //line(x, y, magnitude*size, magnitude*size);
          line(x,y, x+1,0 + in.left.get(x+1)*y+y);
        break;
      case 6:
        line(x, height/2, x, (height/2)+sin(TWO_PI/0.180f)*abs(magnitude*1000));    
        break;
      case 7:
        // beginShape();
        // int angle = x * 360 / 6 * (PI / 180);
        //  x = x + height * cos(size);
        //  y = y + width * sin(size);
        // vertex(x + width, y + height);
        // endShape(CLOSE);
        break;
  }
  }
}


// INPUT
public void keyPressed()
{
   if (key == 'z') {
      if (overlay)
        overlay = false; 
      else 
        overlay = true;
  }

  if (key == '1')
    currentShape += 1;
    currentShape = currentShape %8;
    consoleText = ("shape " + currentShape);
      for (Module mod : mods) {
    mod.changeShape(currentShape);
    }

    if (key == 'r')
      if (!currentRotation) 
        currentRotation = true;
      else 
        currentRotation = false;
      consoleText = ("rot " + currentRotation);
    for (Module mod : mods) {
    mod.changeRotation(currentRotation);
    }   

    if (key == 's'){
    currentSize += 1;
    currentSize = currentSize %10;
    consoleText = ("size " + currentSize);
    }
    for (Module mod : mods) {
    mod.changeSize(currentSize);
    }     

    if (key == 'f'){
    currentSpeed += .1f;
    currentSpeed = currentSpeed %10;
    consoleText = ("speed " + currentSpeed);
    }
    for (Module mod : mods) {
    mod.changeSpeed(currentSpeed);
    } 
  }





  public void settings() {  size(800, 500, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "wave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
