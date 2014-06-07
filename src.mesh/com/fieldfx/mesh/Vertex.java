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
import java.util.List;

import com.fieldfx.math.Vector3;
import com.fieldfx.math.Vector2;
//import com.fieldfx.util.Selectable;

import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;


public class Vertex extends Vector3 implements Serializable {
  
  public  Vector2 uv    = new Vector2();
  public  Normal  n     = new Normal(0.f, 0.f, 1.f);
  private int     index = 0;

  public Vertex( float x, float y, float z ) { super(x,y,z); }
  public Vertex( Vector3 v )                 { super(v); }
  public Vertex( )                           { }
  public Vertex(Vertex copy) {
    this.set(copy);
    uv.set(copy.uv);
    n.set(copy.n);
    index = copy.index;
  }
  
  public  int         getIndex  ( ) { return index; }
  public String       getType   ( ) { return "Vertex"; }
  public Serializable clone     ( ) { return new Vertex(); }
  public Vertex       get       ( ) { return new Vertex(this); }

  public void serialize( Serializer s ) {
    super.serialize(s);
    s.serialize( "uv", uv );
    s.serialize( "n",  n  );
    index = s.serialize("index", index);
  }

}
