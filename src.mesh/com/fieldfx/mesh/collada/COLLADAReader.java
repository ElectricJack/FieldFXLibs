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

import processing.core.PApplet;
import processing.data.XML;

import com.fieldfx.mesh.MeshBuilder;
import com.fieldfx.mesh.Convertible;


public class COLLADAReader implements Convertible {

  public boolean                loaded = false;
  public ArrayList<COLLADAMesh> meshes = new ArrayList<COLLADAMesh>();
  public ArrayList<COLLADANode> roots  = new ArrayList<COLLADANode>();
  
  // ------------------------------------------------------------------------ //
  public COLLADAReader( PApplet app, String filePath ) {
    XML xml  = app.loadXML( filePath );
    
    for( XML child : xml.getChildren() ) {
      if      ( COLLADAHelpers.isNameOf(child, "asset"                 ) ) loadAssetData   ( child );
      else if ( COLLADAHelpers.isNameOf(child, "library_visual_scenes" ) ) loadVisualScene ( child );
      else if ( COLLADAHelpers.isNameOf(child, "library_geometries"    ) ) loadGeometries  ( child );
    }
        
    loaded = true;
  }
  // ------------------------------------------------------------------------ //
  public void convert( MeshBuilder builder ) {
    for( COLLADANode n : roots ) {
      n.convert( builder );
    }
  }
  // ------------------------------------------------------------------------ //
  protected COLLADAMesh getMesh( String id ) {
    for( COLLADAMesh m : meshes )
      if( ("#" + m.id).equals( id ) )
        return m;
        
    return null;
  }
  
  // ------------------------------------------------------------------------ //
  private void loadAssetData( XML asset ) {
  }
  
  // ------------------------------------------------------------------------ //
  private void loadGeometries( XML geometries ) {
    for( XML geometry : geometries.getChildren() ) {
      String id = geometry.getString("id");
      for( XML mesh : geometry.getChildren() )
        meshes.add( new COLLADAMesh( mesh, id ) );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadVisualScene( XML scenes ) {
    for( XML scene : scenes.getChildren() ) {
      XML node = scene.getChild("node");
      if( node == null ) continue;
      roots.add( buildNodeTree( node ) );
    }
  }

  // ------------------------------------------------------------------------ //
  private COLLADANode buildNodeTree( XML node ) {
    COLLADANode n      = new COLLADANode( this );   

    for( XML child : node.getChildren() ) {
      if      ( COLLADAHelpers.isNameOf( child, "matrix"            ) ) n.matrix = COLLADAHelpers.getFloatArray( child );
      else if ( COLLADAHelpers.isNameOf( child, "node"              ) ) n.add( buildNodeTree( child ) );
      else if ( COLLADAHelpers.isNameOf( child, "instance_geometry" ) ) {
        
        String geomID = child.getString("url");
        n.instance( geomID );
      }
    }
 
    return n;
  }

}
