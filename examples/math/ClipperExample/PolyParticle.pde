class PolyParticle {
  Poly2         p           = new Poly2();
  Vector2       pos         = new Vector2();
  Vector2       vel         = new Vector2();
  float         ang         = 0;
  float         angVel      = 0;
  float         z           = 0;
  int           col         = 0;
  List<Poly2>   offsetPolys = new ArrayList<Poly2>();
  List<AnimArc> arcs        = new ArrayList<AnimArc>();
  
  public void grav( PolyParticle other ) {
    vel.inc( other.pos.sub( pos ).nrmeq().muleq( other.p.points.size() * 0.005 / p.points.size() ) );
  }
  
  public PolyParticle() {
    noiseSeed( millis( ));
    int nVerts = (int)random(1,5);
    nVerts *= (int)(nVerts * random(2,5));
    nVerts += random(2,15);
    float r = random(10,10*nVerts);
    if( r > 200 )
      for( int i=0; i<nVerts/4; ++i )
        arcs.add( new AnimArc() );
    
    for( int i=0; i < nVerts; ++i ) {
      float d = noise( map(i,0,nVerts-1,0,8), 2 )*r+5;
      float x = d*cos( map(i,0,nVerts-1,0,2*PI) );
      float y = d*sin( map(i,0,nVerts-1,0,2*PI) );
      p.add( x, y );
    }
    
    pos.set( random(100,width-100), random(50,height-50) );
    float a = random(0,2*PI);
    float s = random(30,400) / (r*0.95);
    vel.set( cos(a)*s, sin(a)*s );
    
    ang    = random(0, 2*PI);
    angVel = random(-0.03f, 0.03f);
    
    z = random(0,100);
    col = color( random(0,255),random(0,255),255 );
    
    int nOffsets = 10;
    for( int o=0; o<nOffsets; ++o ) {
      float off = map(o,0,nOffsets-1,0.5,3.0);
      off *= off * off;
      off *= 20;
      List<Poly2> offsets = p.offset( glu, off, 15 );
      if( offsets != null ) {
        offsetPolys.addAll( offsets );
      }
    }
  }
  
  public void draw() {
    float margin = 150.f;
    if( pos.x < -margin       ) pos.x += width  + margin*2;
    if( pos.y < -margin       ) pos.y += height + margin*2;
    if( pos.x > width+margin  ) pos.x -= width  + margin*2;
    if( pos.y > height+margin ) pos.y -= height + margin*2;
    
    pos.inc( vel );
    ang += angVel;
   

    
    pushMatrix();
      translate( pos.x, pos.y, z );
      rotate( ang );
    
      strokeWeight(2.5);
      float fade = 1.0;
      int i=0;
      for( Poly2 p : offsetPolys ) {
        ++i;
        
        stroke(col,128.0*fade);
        fade *= 0.855;
        pushMatrix();
          rotate( 100.5 * sin(frameCount*0.1f) / (float)p.points.size() );
          scale(0.35f + sin( frameCount*0.15 + i*0.5 )*0.05f );
          p.draw( g );
        popMatrix();
      }
      
    popMatrix();
  }
  
  public void drawSolid() {
    fill(0,128.0f);
    stroke(255);
    strokeWeight(1.0);

    pushMatrix();
      translate( pos.x, pos.y, z );
      rotate( ang );
      
      pushMatrix();
        scale(0.28f + sin( frameCount*0.15 )*0.05f );
        p.draw(g);

      popMatrix();
    popMatrix();
  }
}

