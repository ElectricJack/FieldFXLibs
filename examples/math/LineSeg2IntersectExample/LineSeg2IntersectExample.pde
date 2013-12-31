import   com.fieldfx.math.*;

OBounds2 bounds;

void setup()
{
  size(640,480,P3D);
  bounds = new OBounds2( new Vector2(10,10), new Vector2(width-10, height-10) );
}

void draw()
{
  background(0);

  Line2 lA = new LineSeg2( new Vector2( width/4.f, height/2.f)
                         , new Vector2( (float)mouseX, (float)mouseY ) );
  Line2 lB = new LineSeg2( new Vector2( 100.f, 100.f )
                         , new Vector2( (float)width-100, (float)height ) );


  // Draw the first line
  stroke(255,0,0);
  lA.draw( bounds, g );

  // Draw the second line
  stroke(0,255,0);
  lB.draw( bounds, g );

  // Draw the intersection of the two lines
  stroke(255);
  Vector2 v = lA.intersect( lB );
  if( v != null ) ellipse( v.x, v.y, 10,10 );
}

