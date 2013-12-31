
import com.fieldfx.util.*;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.XMLSerializer;
import com.fieldfx.mesh.MeshBuilderBase;
import com.fieldfx.mesh.Mesh;
import com.fieldfx.mesh.collada.COLLADAReader;


Mesh example_map = null;

// ------------------------------------------------------------------------------- //
void setup() {
  size( 100,100,P3D );
  example_map = loadColladaMesh( "example.dae" );
  XMLSerializer s = new XMLSerializer( this );
  s.save( sketchPath( "example.xml" ), (Serializable)example_map );
  exit();
}


// ------------------------------------------------------------------------------- //
Mesh loadColladaMesh( String fileName ) {
  MeshBuilderBase builder = new MeshBuilderBase( this );
  COLLADAReader   collada = new COLLADAReader( this, dataPath( fileName ) );
                  collada.convert( builder );
  return                           builder.getMesh();
}

