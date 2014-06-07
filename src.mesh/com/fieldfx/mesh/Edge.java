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

import java.util.ArrayList;

import com.fieldfx.math.LineSeg3;
import com.fieldfx.math.Vector3;

//import com.fieldfx.util.SelectableBase;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;


public class Edge implements Serializable {

  public    Vertex  first    = null;
  public    Vertex  second   = null;
  public    Edge    previous = null;
  public    Edge    next     = null;
  public    Face    inside   = null;
  public    Face    outside  = null;
  public    float   bulge    = 0;
  protected int     index    = 0;
  
  public Edge() {}
  public Edge(Edge copy) {
    if( copy.first    != null ) first    = copy.first.get();
    if( copy.second   != null ) second   = copy.second.get();
    if( copy.previous != null ) previous = copy.previous.get();
    if( copy.next     != null ) next     = copy.next.get();
    if( copy.inside   != null ) inside   = copy.inside.get();
    if( copy.outside  != null ) outside  = copy.outside.get();
    bulge = copy.bulge;
    index = copy.index;
  }

  public Edge   get()      { return new Edge(this); }
  // ---------------------------------------------------------------- //
  public String getType()  { return "edge"; }
  public int    getIndex() { return index; }
  // ---------------------------------------------------------------- //
  public ArrayList<Vertex> getVerts() {
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
    verts.add(first);
    verts.add(second);
    return verts;
  }
  // ---------------------------------------------------------------- //
  public LineSeg3 getAsLineSeg3() {
    return new LineSeg3( first, second );
  }
  // ---------------------------------------------------------------- //  
  @Override
  public void serialize( Serializer s ) {
            //@TODO figure out best way to serialize references if they exist
            // s.serialize( "first",    first    );
            // s.serialize( "second",   second   );
            // s.serialize( "previous", previous );
            // s.serialize( "next",     next     );
            // s.serialize( "inside",   inside   );
            // s.serialize( "outside",  outside  );
    
    bulge = s.serialize( "bulge",    bulge    );
    index = s.serialize( "index",    index    );
  }
  // ---------------------------------------------------------------- //
  @Override
  public Serializable clone() {
    return new Edge();
  }
}
