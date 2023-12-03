import themidibus.*;

import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

Minim minim;
//AudioPlayer in;
AudioInput input;
AudioInput in;
// MidiBus myBus;

int rows = 10;
int columns = 10;
int colorRange = 1;
float strokeRange = 255;
int direction = 0;  
boolean inputMode = false; // if true MIDI is on
boolean rot = false;
boolean fil = false;
boolean str = true;
boolean sin = false;

PVector punto = new PVector(0, 0);

int size = 1;

int x, y, r;
int n = 6;
PVector[] p = new PVector[n];
float freq, spd;


void setup()
{
  size(800, 500, P3D);
  //fullScreen(P3D);
  minim = new Minim(this);
  //in = minim.loadFile("song.mp3");
  //input = minim.getLineIn();
  //in.play();
  in = minim.getLineIn();

  // List all our MIDI devices
  // MidiBus.list();
   
  // Connect to one of the devices
  // myBus = new MidiBus(this, 0, 1);
  
  x = width/2;
  y = height/2;
        
  stroke(strokeRange);
  strokeWeight(size);
  r = 40;
  freq = 0;
  spd = .1;
}


void draw()
{
  background(1);
  stroke(strokeRange);
  strokeWeight(size);

  // 0 lines / screen of points
  // 1 point / waveform
  // 2 rect /
  // 4 circles
  // 5 no idea
  // 6 ellipse
  

  for (int h = 0; h < height; h+=rows) {
    for(int i = 0; i < in.bufferSize() - 1; i+=columns)
    {
      //line(i, 0 + in.left.get(i)*h+h, i+1,0 + in.left.get(i+1)*h+h);
      
      switch (direction) {
        
      case 0: // screen dots
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }

        if (rot){
          //translate(width/2, height/2);
          line(i, h, i+1, h*h);
          rotateZ(radians(i+in.left.get(i)));

        } else {
          
        if (!sin) {
          line(i, in.left.get(i)*h+h, i+1,0 + in.left.get(i+1)*h+h);
        } else {
          line(i, in.left.get(i)*h+h, sin(i)*100*size, sin(i)*100*size);
        }
        }
        break;
        
      case 1: // waveform
           //line(i, width/2 + in.left.get(i)*width/2, i+1, width/2 + in.left.get(i+1)*width/2);
         //ellipse(i, h, in.left.get(h)*size, in.left.get(h)*size);
         ellipse(i, width/2 + in.left.get(i)*width/2, size,size);
         
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }
        //point(i, in.left.get(i)*h/2+h);
        //point(i+1, in.left.get(i+1));
        break;
      case 2:
        if (!fil) {
          noFill();
        } else {
          fill(abs(((in.left.get(i)*1000) % 250)));
        }
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }
        if (rot){
          translate(width/2, height/2);
          rotate(radians(x+ in.left.get(h)*size));
        }
        rect(i, h+random(1, 10), y+ i+in.left.get(i)*size, in.left.get(i)*size);
        break;
      case 3:
        if (!fil) {
          noFill();
        } else {
          fill(abs(((in.left.get(i)*1000) % 250)));
        }
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }
        if (rot)rotate(radians(x+in.left.get(h)*100));
        triangle(i, h+in.left.get(h)*100, i,y+ h+in.left.get(h)*size, i-in.left.get(h)*size, h);
        break;
      case 4:
        if (!fil) {
          noFill();
        } else {
          fill(abs(((in.left.get(i)*1000) % 250)));
        }
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }
        if (rot)rotate(radians(in.left.get(h)*100));
        if (!sin) {
          ellipse(i, h, in.left.get(h)*size, in.left.get(h)*size);
        } else {
          ellipse(i+sin(i)*100*size, h, in.left.get(h)*size, in.left.get(h)*size);
        }
        break;

      case 5:
        if (!fil) {
          noFill();
        } else {
          fill(abs(((in.left.get(i)*1000) % 250)));
        }
        if (!str) {
          noStroke();
        } else {
          stroke(255);
        }
        if (rot)rotate(radians(in.left.get(h)*100));
        //line(i, height/2, i, (height/2)+sin(TWO_PI/0.180)*abs(in.left.get(i)*1000));    

        break;

      case 6:
        beginShape();
        float angle = i * 360 / 6 * (PI / 180);
        float x = i + h * cos(size);
        float y = h + i * sin(size);
        vertex(x + i, y + h);

        endShape(CLOSE);
        break;

      case 7:
        for (int ij=1; ij<n; ij++) {
          x= width/2;
          y = height/2;
          freq +=(360/ij)+spd;
          //freq = map(freq,0,360,0,1000);
          x = (int)(x +sin(freq)*r);
          y = (int)(y +cos(freq)*(r/2));
          //println(" freq " + freq + " sspd " + spd); 
          p[ij]= new PVector(x, y);
        }

        background(0);
        for (int iii=1; iii<n; iii++) {
          line(p[iii].x, p[iii].y, p[iii].x, p[iii].y+10);
          break;
        }
      }
    }
  }
}



// INPUT
void keyPressed()
{
  if (key == 'z') {
    if (!inputMode) {
      inputMode = true;
      println("input mode " + inputMode);
    } else {
      inputMode = false;
      println("input mode " + inputMode);
    }
  }
  
  if (inputMode) {
    if (key == 'r') {
      if (!rot) {
        rot = true;
        println("rot " + rot);
      } else {
        rot = false;
        println("rot " + rot);
      }
    }
  
    if (key == 'f') {
      if (!fil) {
        fil = true;
        println("fil " + fil);
      } else {
        fil = false;
        println("fil " + fil);
      }
    }
  
    if (key == 's') {
      if (!str) {
        str = true;
        println("str " + str);
      } else {
        str = false;
        println("str " + str);
      }
    }
  
    if (key == 'a') {
      if (sin) {
        sin = false;
        println("sin " + sin);
      } else {
        sin = true;
        println("sin " + sin);
      }
    }
  
    if (key == 'x') {
      punto = new PVector(random(0, 20), random(0, 20));
      println("punto " + punto);
    }
  
  
      //direction
      if (key == '1')
        direction+=1;
        direction = direction %8;
        println("dir " + direction);
      
      //size
      if (key == 'o')
        size -= 2;
        println("size " + size);
  
      if (key == 'p')
        size += 2;
        println("size " + size);
  
      if (key == 'k')
        strokeRange -= 2;
         println("strokeRange " + strokeRange);
  
      if (key == 'l')
        strokeRange += 2;
        println("strokeRange " + strokeRange);
  
      if (key == 'x') {
        r+=4;
        println("r " + r);
      }
  
      if (key == 'c') {
        r-=4;
        println("r " + r);
      }
  
      if (key == 'n') {
        freq+=1;
        println("freq " + freq);
      }
  
      if (key == 'm') {
        freq-=1;
        println("freq " + freq);
      }
  }
}



void controllerChange(int channel, int number, int value) {
  // Here we print the controller number.
  println(number);

  // If we turn knob with id 104, draw a line
  // The vertical position of the line depends on how much we turn the knob

  if (number == 7) {
    float y = map(value, 0, 80000, 0, 80000);
    size = (int)y;
    println(size);
    //columns = (int)y;
  }

  if (number == 74) {
    float tr = map(value, 0, 8000000, 0, 8000000);
    size = (int)tr;
    println(size);
    //rows = (int)y;
  }

  if (number == 19) {
    float y = map(value, 0, 3, 0, 3);
    direction = (int)y %6;
    print ("dir" + direction);
  }

  if (number == 71) {
    float y = map(value, 0, 127, 0, 127);
    colorRange = (int)y*2;
    strokeRange = (int)255-y*2;
    print("c" + colorRange + "strk" + strokeRange);
  }

  if (number == 76) {
    float y = map(value, 0, 255, 0, height);
    noStroke();
    colorMode(RGB, y*2);
    stroke(y*2);
  }
}

/*
  for(int i = 0; i < player.bufferSize() - 1; i++)
 {
 line(i, 50 + player.left.get(i)*50, i+1, 50 + player.left.get(i+1)*50);
 //line(i, 150 + player.right.get(i)*50, i+1, 150 + player.right.get(i+1)*50);
 }
 */