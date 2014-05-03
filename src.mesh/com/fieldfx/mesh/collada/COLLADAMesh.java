/*
  FieldFX Processing Libraries
  Copyright (c) 2011-2013, Jack W. Kern
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/
package com.fieldfx.mesh.collada;

import com.fieldfx.mesh.Face;
import com.fieldfx.mesh.Mesh;
import com.fieldfx.mesh.Vertex;

import processing.data.XML;

public class COLLADAMesh extends Mesh {
  public  String materialName;
  public  String id;
  private int    poly_index = 0;
  
  // ------------------------------------------------------------------------ //
  public COLLADAMesh( XML mesh, String id ) {

    this.id = id;
    
    String positionArrayID = "";
    String normalArrayID   = "";
    XML verts = mesh.getChild( "vertices" );
    if( verts != null ) {
      for( XML child : verts.getChildren() ) {
        if( child.getName().equals("input") ) {
          if      ( COLLADAHelpers.isAttribOf( child, "semantic", "POSITION" ) ) positionArrayID = child.getString("source");
          else if ( COLLADAHelpers.isAttribOf( child, "semantic", "NORMAL"   ) ) normalArrayID   = child.getString("source");
        }
      }
    }

    for( XML child : mesh.getChildren() ) {
      if( child.getName().equals("source") ) {
        String childID = ("#" + child.getString("id"));
        if      ( childID.equals( positionArrayID ) ) loadPositions ( child );
        else if ( childID.equals( normalArrayID   ) ) loadNormals   ( child );
      }
      else if ( child.getName().equals("triangles") ) loadTriangles ( child );
      else if ( child.getName().equals("polylist")  ) loadPolylist  ( child );
      else if ( child.getName().equals("polygons")  ) loadPolygons  ( child );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadPositions( XML positions ) {
    if( positions == null ) return;
    float[] values = COLLADAHelpers.getFloatArray( positions.getChild("float_array") );
    
    for( int i=0; i<values.length; i += 3 ) {
      Vertex v = newVertex();
             v.x = values[i+0] * 25.4f;
             v.y = values[i+1] * 25.4f;
             v.z = values[i+2] * 25.4f;
    }
  }
  // ------------------------------------------------------------------------ //
  private void loadNormals( XML normals ) {
    if( normals == null ) return;
    float[] values = COLLADAHelpers.getFloatArray( normals.getChild("float_array") );
    
    for( int i=0; i<values.length; i += 3 ) {
      int n = i / 3;
      if( n >= vertexCount() ) continue;
      Vertex v = newVertex();
             v.n.x = values[i+0];
             v.n.y = values[i+1];
             v.n.z = values[i+2];
    }
  }
  // ------------------------------------------------------------------------ //
  private void loadTriangles( XML triangles ) {
    int   count = triangles.getInt("count");
    int[] data  = COLLADAHelpers.getIntArray( triangles.getChild("p") );
    
    if( count != data.length/3 )
      System.out.println( "specified triangle count and point indicies are out of proportion!" );
    
    for( int i=0; i<count; ++i ) {
      int n = i*3;
      int a = data[n+0];
      int b = data[n+1];
      int c = data[n+2];
      
      Face p = new Face();
           p.add( getVertex(a) );
           p.add( getVertex(b) );
           p.add( getVertex(c) );
      add( p );
    }
  }
  
  // ------------------------------------------------------------------------ //
  private void loadPolylist( XML polygons ) {
    int   count           = polygons.getInt("count");
    int[] pointIndicies   = COLLADAHelpers.getIntArray( polygons.getChild("p") );
    int[] polyPointCounts = COLLADAHelpers.getIntArray( polygons.getChild("vcount") );
    
    if( count != polyPointCounts.length )
      System.out.println( "specified poly count and poly point counts are out of proportion!" );
      
    // For each polygon
    int pointIndex = 0;
    for( int polyIndex=0; polyIndex<polyPointCounts.length; ++polyIndex ) {
      
      // Get the number of points in the polygon, and create our polygon object
      int  points = polyPointCounts[polyIndex];
      Face poly   = new Face();
      
      // Add the corresponding number of points into the polygon
      for( int i=0; i<points && pointIndex<pointIndicies.length; ++i, ++pointIndex ) {
        Vertex vert = this.getVertex( pointIndicies[pointIndex] );
        poly.add( vert );
      }
      
      // Finally, add the new polygon
      this.add( poly );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadPolygons( XML polygons ) {
    for( XML child : polygons.getChildren() ) {
      if( child.getName().equals("ph") ) {

        Face poly = null;
        for( XML phChild : child.getChildren() ) {
          if      ( phChild.getName().equals("p") ) poly = loadPolygon( phChild );
          else if ( phChild.getName().equals("h") && poly != null ) poly.addHole( loadPolygon( phChild ) );
        }
        
        // Finally, add the new polygon
        this.add( poly );
      }
    }
  }
  
  // ------------------------------------------------------------------------ //
  private Face loadPolygon( XML polygon ) {
    if( !COLLADAHelpers.isNameOf( polygon, "p" ) 
     && !COLLADAHelpers.isNameOf( polygon, "h" ) ) return null;
    
    Face  poly          = new Face();
    	  poly.index    = poly_index++;
    int[] pointIndicies = COLLADAHelpers.getIntArray( polygon );
    
    for( int i=0; i<pointIndicies.length; ++i )
    	poly.add( this.getVertex( pointIndicies[i] ) );
    
    return poly;
  }
}
