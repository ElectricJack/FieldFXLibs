
import com.fieldfx.math.*;
import java.util.List;

List<PolyParticle> particles = new ArrayList<PolyParticle>();
Poly2              clipper   = new Poly2();

void setup() {
  size(800, 600, P3D);
  smooth();
  randomSeed( millis() );
  hint(DISABLE_DEPTH_TEST);
  
  for( int i=0; i<40; ++i ) {
    particles.add( new PolyParticle() );
  }
}

void draw() {
  background(0);
  noFill();
  
  for( PolyParticle p : particles ) {
    for( PolyParticle p2 : particles ) {
      if( p2 == p ) continue;
      p.grav(p2);
    }
  }
  
  for( int i=0; i<particles.size(); ++i ) {
    particles.get(i).draw();
  }
  
  for( int i=0; i<particles.size(); ++i ) {
    particles.get(i).drawSolid();
  }
}


