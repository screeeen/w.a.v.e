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

// int rows = 100;
// int columns = 100;
int currentShape = 0;
int currentSize = 2;
int currentDir = 1;
float currentRate = 1;
float currentSpeed = 2;
boolean currentRotation = false; 
boolean currentRandom= false; 
Circle circle;
Peixe peixe;
Linea[] lineas;
int count;
boolean overlay = true;
String consoleText = "";
boolean isModule = false;
boolean isCircle = true;
boolean isPeixe = false;


public void setup() 
{
  // size(1000, 500, P3D);
  

  minim = new Minim(this);
  in = minim.getLineIn();

  circle = new Circle (); 
  peixe = new Peixe();

  peixe.setup();

  int largo = in.bufferSize() - 1;

  lineas = new Linea[largo];

  for(int i = 0; i < largo; i++) {
    lineas[i] = new Linea(i, height/2);
  }

  noStroke();
}

public void draw()
{
  background(0);

  if (isModule){
      for (Linea linea : lineas) {
     linea.display();
     }
  }
  if (isCircle) circle.display();
  if (isPeixe) peixe.display();

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

  Circle (){}

  public void display() {
    pushMatrix();
    translate(width / 2, height / 2);
    float incrementoAngulo = TWO_PI / cantidadPuntos;
    float magnitude = in.left.get(100) * abs(10);
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
    
    for (float i = 0; i < cantidadPuntos; i += incrementoAngulo) {
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

class Linea {
  int wideCount = width / 10; //height / columns;
  int speed = 2;
  int size = 2;
  int x,y;

  Linea(int xPos, int yPos) {
    x = xPos;
    y = yPos;
  }


  public void display() {
    pushMatrix();
    float magnitude = in.left.get(x) * abs(speed);
    ellipse(x*5, y + magnitude * y, size, size);
    popMatrix();
    }
}


class Peixe {
  int w = 1800;
  int h = 600;
  int quadsFactor = 16;
  float fly = 0;
  int sensitivity = 500;
  int cols,rows;
  float [][] terrain ;
  float roti = 3.5f;
  float zoom = 0;

  public void setup() {
    cols = w/quadsFactor;
    rows = h /quadsFactor;
    terrain = new float[cols][rows];
  }


  Peixe (){}
  
  public void display () {
    pushMatrix();
    fly -=.01f;
    float yoff = fly;
    for ( int y = 0; y < rows; y++) {
      float xoff = 0;
      for (int x = 0; x < cols-1; x++) {
        terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, abs(in.left.get(y) * sensitivity));
        //terrain[x][y] = map(-in.left.get(y)*yoff,0,1,-100,100);
        xoff +=0.1f;
      }
      yoff += 0.1f;
    }

    if (key == 'm') roti++;
    if (key == 'n') roti--;
    if (key == 'b') zoom++;
    if (key == 'v') zoom--;
  

    rotateX(PI/roti);
    translate(0, height/2, zoom);
    stroke(255);
    noFill();

    for (int y = 0; y < rows - 1; y++) {
      beginShape(TRIANGLE_STRIP);
      for ( int x = 0; x < cols; x++) {

      vertex(x * quadsFactor, y * quadsFactor, terrain[x][y]);
      vertex(x * quadsFactor, (y + 1) * quadsFactor, terrain[x][y + 1]);
      }
    endShape();
    }
    popMatrix();
  }
}

// INPUT
public void keyPressed()
{
  //  if (key == 'z') {
  //     if (overlay)
  //       overlay = false; 
  //     else 
  //       overlay = true;
  // }

  if (key == '1') {
    if (isModule == true) {
      isModule = false;
      } else {
      isModule = true;
      }
    }

  if (key == '2') {
    if (isCircle == true) {
      isCircle = false;
      } else {
      isCircle = true;
      }
    }

  if (key == '3') {
    if (isPeixe == true) {
      isPeixe = false;
      } else {
      isPeixe = true;
      }
    }

    // if (key == 'r')
    //   if (!currentRotation) 
    //     currentRotation = true;
    //   else 
    //     currentRotation = false;
    //   consoleText = ("rot " + currentRotation);
    // for (Module mod : mods) {
    // mod.changeRotation(currentRotation);
    // }   

    // if (key == 's'){
    //   currentSize += 1;
    //   currentSize = currentSize %10;
    //   consoleText = ("size " + currentSize);
    //   }
    // for (Module mod : mods) {
    //   if (currentRandom)
    //     mod.changeSize(currentSize*10);
    //     else 
    //     mod.changeSize(currentSize);
    // }     

    // if (key == 'a'){
    //   currentRate += 1.0;
    //   currentRate = currentRate %10;
    //   consoleText = ("rate " + currentRate);
    //   }
    //   for (Module mod : mods) {
    //       mod.changeRate(currentRate);
    // }     

    // if (key == 'f'){
    //   currentSpeed += .1;
    //   currentSpeed = currentSpeed %10;
    //   consoleText = ("speed " + currentSpeed);
    //   }
    //   for (Module mod : mods) {
    //   mod.changeSpeed(currentSpeed);
    // } 

    // if (key == 'd')
    //   if (!currentRandom) 
    //     currentRandom = true;
    //   else 
    //     currentRandom = false;
    // for (Module mod : mods) {
    // mod.setRandom(currentRandom);
    // }   

    // if (key == 'g')
    //   if (currentDir == -1) 
    //     currentDir = 1;
    //   else 
    //     currentDir = -1;
    //   consoleText = ("direction " + currentDir);
    //   for (Module mod : mods) {
    //   mod.setDirection(currentDir);
    // }  
  }





  public void settings() {  fullScreen(P3D,1); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "wave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
