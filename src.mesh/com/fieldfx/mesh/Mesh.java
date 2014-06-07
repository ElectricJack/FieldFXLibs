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
package com.fieldfx.mesh;

import com.fieldfx.math.Vector3;
import com.fieldfx.util.NamedMultiMap;
import com.fieldfx.util.SerializableMultiMap;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;



import java.util.ArrayList;
import java.util.List;


public class Mesh implements Convertible, Serializable {
  
  private List                 < Vertex > verts   = new ArrayList            < Vertex >();
  private SerializableMultiMap < Face   > faces   = new SerializableMultiMap < Face   >();
  private int                             index   = 0;
  
  public 	String    getType  ( )              { return "mesh"; }
  public 	int       getIndex ( )              { return index; }
  public  void      setIndex ( int index )    { this.index = index; }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void add( Face face ) {  
    faces.add(face);
  }
  
  // Do these make sense to add?
  //public void beginPoly() {}
  //public void endPoly() {}
  
  // ------------------------------------------------------------------------------------------------------------- //
  public int vertexCount() {
    return verts.size();
  }
  // In-case we need some registration when creating verts
  public Vertex newVertex() {
    Vertex vertex = new Vertex();
    verts.add( vertex );
    return vertex; // Place-holder
  }
  
  public Vertex getVertex( int index ) {
    return verts.get(index);
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void clear( ) { faces.clear(); }
  
  
  // ------------------------------------------------------------------------------------------------------------- //
  // Serializable Methods:
  //@Override
  public void serialize(Serializer s) {
    index = s.serialize( "index", index );
    s.serialize( "verts", verts );
    s.serialize( "faces", faces );
  }
  // ------------------------------------------------------------------------------------------------------------- //  
  @Override
  public Serializable clone() {
    return new Mesh();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  // Convertible Methods:
  //@Override
  public void convert( MeshBuilder builder ) {
      for( PolyLine poly : faces ) {
        builder.beginPoly();
          poly.convert( builder );
        builder.endPoly();
      }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public ArrayList<Vertex> getVerts() {
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
    for( PolyLine poly : faces ) verts.addAll( poly.getVerts() );
    return verts;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public SerializableMultiMap<Face> getFaces() {
    return faces;
  }

  
  
  // ------------------------------------------------------------------------------------------------------------- //
  // The following should probably be moved into a "mesh" operator object that performs the
  // clean operation on a mesh. This way we can have lots of operations, without cluttering up basic
  // mesh functionality.
  // ------------------------------------------------------------------------------------------------------------- //
  public void clean( )
  {
    while( connectAdjacentPaths() );

    // Remove any adjacent duplicates from the list, until none are found]
    List<Face> cleanedPolyLines = new ArrayList<Face>();
    for( Face poly : faces ) {
      while( removeAdjacentDuplicates(poly) );
      if( poly.getVerts().size() > 1 ) cleanedPolyLines.add(poly);
    }
    
    faces.clear();
    faces.addAll( cleanedPolyLines );
  }
  private boolean connectAdjacentPaths()
  {
    SerializableMultiMap<Face>  joinedLines = new SerializableMultiMap<Face>();

    if( faces.size() < 1 ) return false;

    // Add our first line list    
    joinedLines.add( faces.get(0) );
    faces.remove(0);
    
    // Now check every one in the source list
    boolean merged = false;
    for( Face src : faces )
    {
      FindResults results = new FindResults();
      if( findAdjacentPath( src, joinedLines, results ) ) {
                 merged = true;
        Face dest   = joinedLines.get( results.index );
        combinePaths( dest, src, results );
      }
      else joinedLines.add( src );      
    }
    
    faces = joinedLines;
    
    return merged;
  }
  private boolean removeAdjacentDuplicates( Face testPoly )
  {
    float             threshold = 0.25f;
    boolean           found     = false;
    ArrayList<Vertex> newVerts  = new ArrayList<Vertex>();
    Vertex            compare   = testPoly.verts.get(0);
    
    newVerts.add( compare );
    
    for( int i=1; i<testPoly.verts.size(); ++i ) {
      if( compare.sub( testPoly.verts.get(i) ).len() > threshold ) {
        compare = testPoly.verts.get(i);
        newVerts.add( compare );
      }
      else found = true;
    }
    
    if( found )
      testPoly.verts = newVerts;
    
    return found;
  }
  private boolean findAdjacentPath( Face test, NamedMultiMap<Face> from, FindResults out )
  {
    float threshold = 0.01f;
    
    // Store off the first and last verts of the line we're adding
    Vector3 first = test.verts.get( 0 );
    Vector3 last  = test.verts.get( test.verts.size() - 1 );
    
    // Lets look through all the line lists and see if this
    //  line list should be inserted before or after any others in the list.
    boolean found  = false;
    out.index  = 0;
    out.before = false;
    out.flip   = false;       
    
    for( Face l : from )
    {
      if( l == test ) continue;
      
      Vector3 l0 = l.verts.get( 0 );
      Vector3 l1 = l.verts.get( l.verts.size() - 1 );
      
      if     ( l0.sub(last).len()  < threshold )  { found = true; out.before = true; break; }
      else if( l1.sub(first).len() < threshold )  { found = true; break; }
      else if( l0.sub(first).len() < threshold )  { found = true; out.flip = true; out.before = true; break; }
      else if( l1.sub(last).len()  < threshold )  { found = true; out.flip = true; break; }
      
      ++out.index;
    }    
    
    return found;
  }
  private void combinePaths( Face dest, Face src, FindResults how )
  {
    if( how.flip )
      src.flip();
      
    // Add them forwards
    int addAt = 0;
    for( Vertex v : src.verts )
    {
      if( how.before ) dest.verts.add( addAt++, v );
      else             dest.verts.add( v );
    }
  }
  private class FindResults
  {
      int     index  = 0;
      boolean before = false;
      boolean flip   = false;    
  }
  
 
}
