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
boolean inputMode = false; //activates keyboard
int currentShape = 0; //activates keyboard
Module[] mods;
int count;

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
  println('h',height,'w',width,wideCount,highCount,count);
  mods = new Module[count];

  int index = 0;
  for (int yCor = 0; yCor < highCount; yCor++) {
    for(int xCor = 0; xCor < wideCount ; xCor++) // in.bufferSize() - 1
    {
      mods[index++] = new Module (xCor*rows,yCor*columns);
      // println(y,x);
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
}

class Module {
  int x;
  int y;
  // int w = 2;
  // int h = 2;
  int colorRange = 1;
  float strokeRange = 255;
  int shape = 0;  
  int size = 2;
  boolean rot = false;
  boolean fil = false;
  boolean str = true;
  boolean sin = false;
  float freq, spd;

  //constructor
  Module(int xPos,int yPos)//,int width,int height,int colorRange, float strokeRange, int shape, int size, boolean rot, boolean fil, boolean str, boolean sin, float freq, float spd)
  {
  x = xPos;
  y =  yPos;
  // w = size;
  // h = size;
  // colorRange = colorRange;
  // strokeRange = strokeRange;
  // shape = shape;
  // size = size;
  // rot = rot;
  // fil = fil;
  // str = str;
  // sin = sin;
  // freq = freq;
  // spd = spd;
  }

  public void update (){

  }

  public void changeShape (int i) {
    shape = i;
  }

   // Custom method for drawing the object
  public void display() {
    fill(255);
    // println(x,y);
   // line(x,y, x+1,0 + in.left.get(x+1)*y+y);
    // line(x, 0 + in.left.get(x)*y+y, x+1,0 + in.left.get(x+1)*y+y);
    switch (shape) {
      case 0: // screen dots      
        ellipse(x, in.left.get(x)*y+y, size, size);
        break;
      case 1: // waveform
        ellipse(x, width/2 + in.left.get(x)*width/2, size,size);
        break;
  }
  }
}

// INPUT
public void keyPressed()
{
   if (key == 'z') {// { inputmode  ? inputMode = false : inputMode = true; }
    if (!inputMode) 
      inputMode = true;
     else 
      inputMode = false;
      println("input mode " + inputMode);
  }

  if (key == '1')
    currentShape += 1;
    currentShape = currentShape %8;
    println("shape " + currentShape);
      for (Module mod : mods) {
    mod.changeShape(currentShape);
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
