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

int rows = 6;
int columns = 14;
int currentShape = 0;
int currentSize = 2;
int currentDir = 1;
float currentRate = 1;
float currentSpeed = 2;
boolean currentRotation = false; 
boolean currentRandom= false; 
Module[] mods;
int count;
boolean overlay = true;
String consoleText = "";

public void setup() 
{
  
  // fullScreen(P3D);
  minim = new Minim(this);
  //in = minim.loadFile("song.mp3");
  //input = minim.getLineIn();
  //in.play();
  in = minim.getLineIn();
  // List all our MIDI devices
  // MidiBus.list();
  // // Connect to one of the devices
  // myBus = new MidiBus(this, 0, 1);

  noStroke();
  generate();
}

public void generate(){
    int highCount = height / columns;
    int wideCount =  width / rows;
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


public void destroyMods(){
  mods = null;
    generate();
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
      text(consoleText + "Z-show this -1-changes shape - A- audioMagnification - S - size	- R	-rotates -D-randomize - F- faster rotate - G- rotate direction ", 0, height-10);
  }
}

// THIS IS THE MODULE

class Module {
  int x;
  int y;
  int colorRange = 1;
  float strokeRange = 150;
  int shape = 0;  
  int size = 2;
  boolean rotation = false;
  boolean randomize = false;
  float speed = 1;
  int rate = 1;
  int direction = 1;

  Module(int xPos,int yPos){
  x = xPos;
  y =  yPos;
  }

  public void update (){
      if (rotation) {
        overlay = false;
        translate(width/2,height/2);
        rotate(radians(speed+=1*direction));
      }

      if (randomize) {
        translate(random(0,width/2),random(0,height/2));
      }
  }

  public void changeShape (int i) {
    shape = i;
  }

  public void changeRate (float rt) {
    rate = rate;
  }

  public void setDirection (int dir) {
    direction = dir;
  }

  public void setRandom (boolean d) {
    randomize = d;
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
    float magnitude = in.left.get(x) * abs(speed);
    // println(speed);

    switch (shape) {
      case 0: // screen dots 
      pushMatrix();
        ellipse(x, magnitude*size*y+y, size, size);
          if (rotation) {
            translate(random(0, width),random(0,height));
            rotate(PI*radians(speed+=.1f));
          }
      popMatrix();     
      break;
      case 1: // waveform
        ellipse(x, height/2 + magnitude*height/2, size,size);
      break;
      case 2:
      fill(150);
      stroke(150);
      line(x, height/2, x, (height/2)+sin(TWO_PI/0.180f)*abs(magnitude*size*1000));          
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
    currentShape = currentShape %3;
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
      if (currentRandom)
        mod.changeSize(currentSize*10);
        else 
        mod.changeSize(currentSize);
    }     

    if (key == 'a'){
    currentRate += 1.0f;
    currentRate = currentRate %10;
    consoleText = ("rate " + currentRate);
    }
    for (Module mod : mods) {
        mod.changeRate(currentRate);
    }     

    if (key == 'f'){
    currentSpeed += .1f;
    currentSpeed = currentSpeed %10;
    consoleText = ("speed " + currentSpeed);
    }
    for (Module mod : mods) {
    mod.changeSpeed(currentSpeed);
    } 

    if (key == 'd')
      if (!currentRandom) 
        currentRandom = true;
      else 
        currentRandom = false;
    consoleText = ("random " + currentRandom);
    for (Module mod : mods) {
    mod.setRandom(currentRandom);
    }   

    if (key == 'g')
      if (currentDir == -1) 
        currentDir = 1;
      else 
        currentDir = -1;
    consoleText = ("direction " + currentDir);
    for (Module mod : mods) {
    mod.setDirection(currentDir);
    }  

  }





  public void settings() {  size(1000, 500, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "wave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
