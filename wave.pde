import themidibus.*;

import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

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

//clases
Circle circle;
Peixe peixe;
Joy joy;
Linea[] lineas;
int count;

boolean overlay = true;
String consoleText = "";
boolean isModule = false;
boolean isCircle = true;
boolean isPeixe = false;
boolean isJoy = false;


void setup() 
{
  // size(1000, 500, P3D);
  fullScreen(P3D);

  minim = new Minim(this);
  in = minim.getLineIn();

  circle = new Circle (); 
  peixe = new Peixe();
  peixe.setup();
  
  joy = new Joy();
  joy.setup();

  int largo = in.bufferSize() - 1;
  lineas = new Linea[largo];
  for(int i = 0; i < largo; i++) {
    lineas[i] = new Linea(i, height/2);
  }

  noStroke();
}

void draw()
{
  background(0);

  if (isModule){
      for (Linea linea : lineas) {
     linea.display();
     }
  }
  if (isCircle) circle.display();
  if (isPeixe) peixe.display();
  if (isJoy) joy.display();

  if (overlay) {
    fill(100);
    rect(0,height - 22, width, 20);
    fill(255);
    text(consoleText + "1,2,3 to change shapes", 0, height-10);
  }

}

//////////////////////////////////
class Joy {
  int w = 1800;
  int h = 600;
  int quadsFactor = 16;
  float fly = 0;
  float sensitivity = 0.2;
  int cols, rows;
  float[][] terrain;
  boolean hard = true;
  int rott = 0;
  float zoom = -155;
  float yaxis = height/5;
  float xaxis = width/5;

  FFT fft;
  int fftIndex = 0;  // Added variable to keep track of FFT index

  Joy() {
  }

  void setup() {
    cols = w / quadsFactor;
    rows = h / quadsFactor;
    terrain = new float[cols][rows];
    fft = new FFT(in.bufferSize(), in.sampleRate());
  }

  void display() {
    // Update FFT in the loop
    fft.forward(in.mix);
    stroke(255);
    noFill();

    if (key == 'x') xaxis+=1;
    if (key == 'c') xaxis-=1;
    if (key == 'w') yaxis+=1;
    if (key == 's') yaxis-=1;
    if (key == 'e') zoom+=1;
    if (key == 'd') zoom-=1;
    if (key == 'r') rott+=1;
    if (key == 'f') rott-=1;

    println(6969696);
    println(yaxis);
    println(xaxis);
    println(zoom);
    println(rott);

    pushMatrix();
    rotateX(PI / 3.5);
    // rotateZ(radians(270));
    
    translate(xaxis, yaxis, zoom);
    fly -=.01;
    float yoff = fly;
    for (int x = 0; x < cols - 1; x++) {
      float xoff = 0;
      beginShape(LINE_STRIP);
      for (int y = 0; y < rows; y++) {
        // Integrate FFT data into terrain generation
        float fftValue = fft.getBand(fftIndex) * sensitivity;
        terrain[x][y] = map(fftValue, 0, 1, -100, 100);
        fftIndex = (fftIndex + 1) % fft.specSize();  // Increment and wrap around
        xoff += 0.1;
        vertex(x * quadsFactor, y * quadsFactor, terrain[x][y]);
      }
      endShape();
      yoff += 0.1;
    }

    popMatrix();
  }
}



class Circle {
  float radio;
  float angulo = 0;
  int cantidadPuntos = 40;
  int size = 2;
  int speed = 1;

  Circle (){}

  void display() {
    pushMatrix();
    translate(width / 2, height / 2);
    float incrementoAngulo = TWO_PI / cantidadPuntos;
    float magnitude = in.left.get(100) * abs(10);
    angulo += 0.002;
    radio =  min(width, height) *  magnitude + height/3;
    
    for (float i = 0; i < cantidadPuntos; i += incrementoAngulo) {
      float x = radio * cos(i + angulo);
      float y = radio * sin(i + angulo);

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


  void display() {
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
  boolean hard = true;
  

  void setup() {
    cols = w/quadsFactor;
    rows = h /quadsFactor;
    terrain = new float[cols][rows];
  }

  Peixe (){}
  
  void display () {
    pushMatrix();
    fly -=.01;
    float yoff = fly;
    for ( int y = 0; y < rows; y++) {
      float xoff = 0;
      for (int x = 0; x < cols-1; x++) {
        if (hard) terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, abs(in.left.get(y) * sensitivity));
        if (!hard) terrain[x][y] = map(in.left.get(y)+ noise(xoff, yoff), 0, 1, -100, 100);
        xoff +=0.1;
      }
      yoff += 0.1;
    }

    if (key == 'n') hard = true;
    if (key == 'm') hard = false;

    rotateX(PI/3.5);
    translate(0, height/2);
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
void keyPressed()
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

  if (key == '4') {
    if (isJoy == true) {
      isJoy = false;
      } else {
      isJoy = true;
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





