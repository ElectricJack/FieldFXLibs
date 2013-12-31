import processing.opengl.*;


import com.fieldfx.math.*;
import java.util.Map.Entry;

Poly2 a, b;

//ArrayList<Vector2> points;
List<Vector2> insidePoints = new ArrayList<Vector2>();

void setup() {
  size         ( 400, 400 );
  smooth       ( );
  
  setupTestA();
  //setupTestB();
  //setupTestC();
  
  //testInside   ( );
  //testCut      ( );
  //testUnion    ( );
  //noLoop();
}

void draw() {

  background(255);
  
  strokeWeight(1);
  stroke(0,0,0,32);
  line( 100,   0,   100, height );
  line( 200,   0,   200, height );
  line(   0, 100, width,    100 );  
  line(   0, 200, width,    200 );
  
  testDynamic();
  testInside();
  
  strokeWeight(2);
  if( b != null ) { stroke(255,0,0,128); fill(255,0,0,64); b.draw(g); }
  if( a != null ) { stroke(0,0,255,128); fill(0,0,255,64); a.draw(g); }

  stroke(0,0,0); fill(0,0,0,64);
  for( Vector2 point : insidePoints ) {
    ellipse( point.x, point.y, 5, 5 );
  }
}

void testDynamic () {
  Vector2 start = new Vector2( 10, 200 );
  Vector2 end   = new Vector2( mouseX, mouseY );
  /*
  int index = (int)map( mouseX, 0, width, 0, 4 );
  Vector2 end = null;
  if      ( index == 0 ) end = new Vector2( 349, 203 );
  else if ( index == 1 ) end = new Vector2( 247, 71 ); 
  else if ( index == 2 ) end = new Vector2( 207, 37 );
  else if ( index == 3 ) end = new Vector2( 222, 137 );
  */

  Vector2 tang = end.sub(start).nrm();
  tang.set(-tang.y, tang.x);
  
  //a = new Poly2();
  //a.add( 100, 100 );
  //a.add( 100, 300 );
  //a.add( 300, 300 );
  //a.add( 300, 100 );
  setupTestC();
  b = new Poly2();
  b.add( start.x + tang.x * -20, start.y + tang.y * -20 );
  b.add( start.x + tang.x *  20, start.y + tang.y *  20 );
  b.add( end.x   + tang.x *  20, end.y   + tang.y *  20 );
  b.add( end.x   + tang.x * -20, end.y   + tang.y * -20 );


  
  //testCut    ();
  testUnion    ();
  //testInside ();
}

void testCut()    { a.cuteq( b );   }
void testUnion()  { a.unioneq( b ); }
void testInside() {
  insidePoints.clear();
  for( int i=0; i<100; ++i ) {
    Vector2 point = new Vector2( random( 0, width ), random( 0, height ) );
    if( a.contains( point ) ) {
      //println( point );
      insidePoints.add( point );
    }
  }
}

void setupTestA() {
  a = new Poly2();
  
  a.add(  20, 20 );
  a.add( 250, 30 );
  a.add( 250, 200 );
  a.add(  20, 380 );

  
  b = new Poly2();
  b.add( 20,  60 );
  b.add( 390, 10 );
  b.add( 390, 390 );
}


void setupTestB() {
  a = new Poly2();
  a.add(  20,  20 );
  a.add(  20, 380 );
  a.add( 300, 380 );
  a.add( 300,  20 );
  
  b = new Poly2();
  b.add(  40, 200 );
  b.add(  40, 100 );
  b.add( 390, 100 );
  b.add( 390, 350 );
  
}

void setupTestC() {
  a = new Poly2();
  a.add(  20,  20 );
  a.add(  20, 380 );
  a.add( 300, 380 );
  a.add( 300, 200 );
  a.add( 100, 200 );
  a.add( 100, 150 );
  a.add( 300, 150 );
  a.add( 300,  20 );
}
