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
FFT fft;
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
Circle circle;
int count;
boolean overlay = true;
String consoleText = "";



public void setup() 
{
  
  // fullScreen(P3D);
  minim = new Minim(this);
  in = minim.getLineIn();

  noStroke();
  generate();

  

}

public void generate(){
    int highCount = height / columns;
    int wideCount =  width / rows;
    count = wideCount * highCount;
    println(count);

    mods = new Module[count];

    int index = 0;
    for (int yCor = 0; yCor < highCount; yCor++) {
      for(int xCor = 0; xCor < wideCount ; xCor++) 
      {
        mods[index++] = new Module (xCor*rows,yCor*columns);
      }
    }

    circle = new Circle (); 
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

  circle.display();

  if (overlay) {
    fill(100);
    rect(0,height - 22, width, 20);
    fill(255);
    text(consoleText + "Z-show this -1-changes shape - A- audioMagnification - S - size	- R	-rotates -D-randomize - F- faster rotate - G- rotate direction ", 0, height-10);
  }

}


class Circle {
  float radio;
  float angulo = 0;
  int cantidadPuntos = 40;
  int size = 2;
  int speed = 1;
  float magnitude;

  Circle (){}

  public void display() {
    pushMatrix();
    translate(width / 2, height / 2);
    float incrementoAngulo = TWO_PI / cantidadPuntos;
    magnitude = in.left.get(200);
    // angulo += 0.002 * magnitude;
    angulo += 0.002f;
    // float magnitude = in.left.get(x) * abs(speed);
    // fft = new FFT( in.bufferSize(), in.sampleRate());
    // fft.forward( in.mix );

    //   for(int i = 0; i < fft.specSize(); i++) {
    //   // draw the line for frequency band i, scaling it up a bit so we can see it
    //   line(i, height, i, height - fft.getBand(i) * 8 );
    // }

    radio =  min(width, height) *  magnitude + height/3;
    
    for (float i = 0; i < cantidadPuntos; i += incrementoAngulo + PApplet.parseInt(magnitude)) {
    // for (float i = 0; i < fft.specSize(); i += incrementoAngulo + int(magnitude)) {
      float x = radio * cos(i + angulo); //* fft.getBand(int(i)*2);
      float y = radio * sin(i + angulo); //* fft.getBand(int(i)*2);

      // Dibuja cada punto
      fill(255);
      noStroke();
      ellipse(x, y, size, size);
    }
    popMatrix();
}

}

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
        translate(500, 250);
        rotate(PI*(speed+=1));
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

    switch (shape) {
      case 0: // screen dots 
        pushMatrix();
          ellipse(x, magnitude * size * y + y, size, size);
            if (rotation) {
              translate (random (0, width),random(0, height));
              rotate (PI * radians (speed += .1f));
            }
        popMatrix();     
      break;
      case 1: // waveform
        ellipse(x, height/2 + magnitude*height/2, size,size);
      break;
      case 2:
        fill(255);
        stroke(255);
        line(x, height/2, x, (height/2) + sin(TWO_PI/0.180f) * abs(magnitude * size * 1000));          
        line(x, height/2, x, (height/2) - sin(TWO_PI/0.180f) * abs(magnitude * size * 1000));          
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
    String[] appletArgs = new String[] { "wave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
