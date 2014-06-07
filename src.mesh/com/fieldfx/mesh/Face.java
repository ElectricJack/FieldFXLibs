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

import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;

import com.fieldfx.util.NamedSerializable;



public class Face extends PolyLine implements Convertible, NamedSerializable {
  
  protected List< PolyLine > holes  = null;
  protected Normal           normal = new Normal();
  public    int              index  = 0;
  private   String           name   = "";
  
  public Face() {}
  public Face(Face copy) {
    super(copy);

    if(copy.holes != null) {
      holes = new ArrayList<PolyLine>();
      for(PolyLine hole : copy.holes) {
        holes.add( hole.get() );
      }
    }

    normal.set(copy.normal);
    index = copy.index;
    name  = copy.name;
  }

  public  Face              get      ( )                { return new Face(this); }
  // ------------------------------------------------------------------------------------- //
  public  String            getType  ( )                { return "face"; }
  public  int               getIndex ( )                { return index;  }
  public  Vector3           getNormal( )                { return normal; }
  public  List< PolyLine >  getHoles ( )                { return holes;  }
  
  public  void              setNormal( Vector3 normal ) { this.normal.set(normal); }

  public  void              setName  ( String name )    { this.name = name; }
  public  String            getName  ( )                { return name; }
  
  // ------------------------------------------------------------------------------------- //
  public void addHole( PolyLine hole ) {
    if( hole  == null ) return;
    if( holes == null ) holes = new ArrayList< PolyLine >();
    holes.add( hole );
  }
  
  // ------------------------------------------------------------------------------------- //
  //@Override
  public void convert( MeshBuilder builder ) {
    builder.beginPoly();
    for( Vertex v : verts ) {
      builder.normal( v.n.x, v.n.y, v.n.z );
      builder.vertex( v.x,  v.y,  v.z  );
    }
    builder.endPoly();
  }
  
  // ------------------------------------------------------------------------------------- //
  //@Override
  public void serialize(Serializer s) {
    super.serialize(s);
    index = s.serialize( "index", index );
    s.serialize( "normal", normal );

    if( s.isLoading() ) {
      holes = new ArrayList<PolyLine>();
      s.serialize( "holes",  holes );
      if( holes.size() == 0 ) {
        holes = null;
      }
    } else {
      if( holes != null ) {
        s.serialize( "holes",  holes  );
      }
    }
    
  }
  
  // ------------------------------------------------------------------------------------- //
  @Override
  public Serializable clone() {
    return new Face();
  }
}
