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

import java.util.ArrayList;

import com.fieldfx.mesh.MeshBuilder;


public class COLLADANode {
  public String                 name     = null;
  public float[]                matrix    = null;
  public ArrayList<COLLADANode> children  = null;
  public ArrayList<String>      instances = null;
  public COLLADAReader          parent;
  
  public COLLADANode( COLLADAReader parent ) { this.parent = parent; }
  
  public void add( COLLADANode child ) {
    if( children == null )
      children = new ArrayList<COLLADANode>();
    children.add( child );
  }
  
  public void instance( String id ) {
    if( instances == null )
      instances = new ArrayList<String>();
    instances.add( id );
  }
  
  public void convert( MeshBuilder builder ) {
    if( matrix != null ) {
      builder.pushMatrix();
      builder.applyMatrix( matrix );
    }
    
    if( instances != null ) {
      for( String id : instances ) {
        COLLADAMesh mesh = parent.getMesh( id );
        if( mesh != null )  mesh.convert( builder );
      }
    }
    
    if( children != null )
      for( COLLADANode node : children )
        node.convert( builder );
    
    if( matrix != null ) {
      builder.popMatrix();
    }
  }

}
