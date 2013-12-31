
import peasy.*;

import com.fieldfx.util.*;
import com.fieldfx.mesh.*;
import com.fieldfx.mesh.collada.*;
import com.fieldfx.math.*;

DrawWriter drawer      = null;
Mesh       example_map = null;
PeasyCam   cam         = null;


// ------------------------------------------------------------------------------- //
void setup() {
  size( 800, 600, P3D );
  example_map = loadColladaMesh( "example.dae" );
  drawer      = new DrawWriter( this.g );
  cam         = new PeasyCam( this, 1000 );
}

// ------------------------------------------------------------------------------- //
void draw() {
  background(0);

  fill   ( 100 );
  stroke ( 255 );
  scale  ( 0.1f );
  
  example_map.convert( drawer );
}


// ------------------------------------------------------------------------------- //
Mesh loadColladaMesh( String fileName ) {
  MeshBuilderBase builder = new MeshBuilderBase( this );
  COLLADAReader   collada = new COLLADAReader( this, dataPath( fileName ) );
                  collada.convert( builder );
  return                           builder.getMesh();
}

