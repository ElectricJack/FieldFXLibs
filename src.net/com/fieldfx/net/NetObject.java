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
package com.fieldfx.net;


import java.util.ArrayList;
import java.util.List;

import com.fieldfx.math.Vector3;
import com.fieldfx.util.*;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;

public abstract class NetObject implements Serializable {
  public static final int invalid_id = -1;
  public static final int max_relations = 5;
  
  protected int               id        = invalid_id;
  protected byte              life      = (byte)128;
  protected List<NetRelation> relations = new ArrayList<NetRelation>();
  public    Vector3           pos       = new Vector3();
  
  // -------------------------------------------------------------------------------- //
  public Vector3 vecFrom( NetObject other ) {
    return pos.sub( other.pos );
  }
  // -------------------------------------------------------------------------------- //
  public Vector3 vecTo( NetObject other ) {
    return other.pos.sub( pos );
  }
  // -------------------------------------------------------------------------------- //
  public float   distSqrTo( NetObject other ) {
    return vecTo(other).lenlen();
  }
  
  // -------------------------------------------------------------------------------- //
  //public abstract void update();
  //public abstract void draw();
  
  // -------------------------------------------------------------------------------- //
  public void tick( NetMesh parent ) {
    if( life != -1)
      --life;
    
    List<NetRelation> toRemove = new ArrayList<NetRelation>();
    for( NetRelation r : relations ) {
        r.tick();
        if( r.isDead() )
        toRemove.add(r);
    }
    
    for( NetRelation r : toRemove ) {
      if( r.get(parent) != null )
        parent.killRelation( this, r.get(parent) );
      relations.remove(r);
    }
  }
  
  // -------------------------------------------------------------------------------- //
  public void updateRelation( NetMesh parent, NetObject other ) {
    
    for( NetRelation r : relations ) {
      if( r.refersTo( other.id ) ) {
        r.renew();
        return;
      }
    }
    
    if( relations.size() < max_relations ) {
      relations.add( new NetRelation( other ) );
    } else {
      float dist_other = pos.sub( other.pos ).lenlen();
        
      NetRelation worse = null;
      for( NetRelation r : relations ) {
        if( r.get(parent) == null ) continue;
        if( dist_other < pos.sub( r.get(parent).pos ).lenlen() ) {
          worse = r;
          break;
        }
      }
      
      if( worse != null ) {
        relations.remove(worse);
        relations.add( new NetRelation(other) );
      }
    }
    
  }
  
  // -------------------------------------------------------------------------------- //
  public String                getType   ( ) { return "NetObject"; }
  public abstract Serializable clone     ( );
  public abstract void         serialize ( Serializer s );

  public void notifyAdd() {}

}
