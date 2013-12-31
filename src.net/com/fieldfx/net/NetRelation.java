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

import processing.core.PApplet;

import com.fieldfx.math.Vector3;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;


// Relations are created by rules, and exist until rules stop renewing them. 
// They are used to specify bonds between net objects, to facilitate performing calculations based on 
// differences between net objects.
public class NetRelation implements Serializable {
  
  
  private   int        otherID  = 0;
  private   NetObject  other    = null; // Set when updated by serialization
  private   byte       life     = 0;

  // -------------------------------------------------------------------------------- //
  public NetRelation()                  { }
  public NetRelation( NetObject other ) { this.other = other; this.otherID = other.id; }

  // -------------------------------------------------------------------------------- //
  public String       getType   ( )         { return "NetRelation"; }
  public Serializable clone     ( )         { return new NetRelation(); }
  public void         serialize ( Serializer s )  {
    if( s.isLoading() ) {
      otherID = s.serialize( "other", otherID );
    } else {
      s.serialize("other", other.id );
    }
  }
  
  // -------------------------------------------------------------------------------- //
  public boolean equals( NetRelation other ) {
    return this.otherID == other.otherID;
  }
  // -------------------------------------------------------------------------------- //
  public boolean refersTo(int id) {
    return this.otherID == id;
  }
  // -------------------------------------------------------------------------------- //
  public float lenlen( Vector3 to ) {
    return other.pos.sub(to).lenlen();
  }
  
  // -------------------------------------------------------------------------------- //
  public void draw( PApplet parent, Vector3 to ) {
    if( other == null || to == null ) return;
    parent.line( other.pos.x, other.pos.y, other.pos.z, to.x, to.y, to.z );
  }
  
  // -------------------------------------------------------------------------------- //
  // This binds the object reference to the correct NetObject
  //  and should be called after serializing in the relation
  public void bind( NetMesh parent ) {
    // First try binding with local nodes, then remote nodes.
    other = parent.get(true).getById(otherID);
    if( other == null ) {
      other = parent.get(false).getById(otherID);
    }
  }
  // -------------------------------------------------------------------------------- //
  public NetObject get( NetMesh parent ) {
    if( otherID != 0 && other == null ) bind( parent );
      return other;
  }
  // -------------------------------------------------------------------------------- //
  public void renew() {
    life = 5;
  }
  // -------------------------------------------------------------------------------- //
  public void tick() {
    if( life != -1)
      --life;
  }
  // -------------------------------------------------------------------------------- //
  public boolean isDead() {
    return life <= 0;
  }

}
