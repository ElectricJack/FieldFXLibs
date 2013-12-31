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


package com.fieldfx.math;

import processing.core.PGraphics;

public class Space3 {
  public Vector3 pos   = new Vector3( );
  public Vector3 right = new Vector3( 1.0f,  0.0f, 0.0f );
  public Vector3 up    = new Vector3( 0.0f, -1.0f, 0.0f );
  public Vector3 ahead = new Vector3( 0.0f,  0.0f, 1.0f );
  
  // ----------------------------------------------------------------- //
  public Space3() {}
  public Space3( Space3 other ) {
    set( other );
  }
  // ----------------------------------------------------------------- //
  public Space3 set( Space3 other ) {
    
    pos.set   ( other.pos   );
    
    right.set ( other.right );
    up.set    ( other.up    );
    ahead.set ( other.ahead );
    
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 set( float x, float y, float z ) {
    pos.x = x;
    pos.y = y;
    pos.z = z;
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 set( Vector3 pos ) {
    set( pos.x, pos.y, pos.z );
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 setAhead( Vector3 ahead ) {
    
    this.ahead.set( ahead );
    this.ahead.nrmeq();
    
    this.right = this.ahead.cross( this.up );
    this.right.nrmeq();
    
    this.up = this.ahead.cross( this.right );
    this.up.nrmeq();
    
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 setRight( Vector3 right ) {
    this.right.set( right );
    this.right.nrmeq();
    
    this.up = this.ahead.cross( this.right );
    this.up.nrmeq();
    
    this.ahead = this.up.cross( this.right );
    this.ahead.nrmeq();
    
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 setUp( Vector3 up ) {
    this.up.set( up );
    this.up.nrmeq();
    
    this.right = this.ahead.cross( this.up );
    this.right.nrmeq();
    
    this.ahead = this.up.cross( this.right );
    this.ahead.nrmeq();
    
    return this;
  }
  // ----------------------------------------------------------------- //
  public Space3 get() { 
    return new Space3(this);
  }
  // ----------------------------------------------------------------- //
  public Vector3 transformIn( Vector3 v )                 { return transformIn(v,false); }
  public Vector3 transformIn( Vector3 v, boolean vector ) {
    if( !vector ) v.dec(pos);
    
    float rx = v.x*right.x + v.y*up.x + v.z*ahead.x;
    float ry = v.x*right.y + v.y*up.y + v.z*ahead.y;
    float rz = v.x*right.z + v.y*up.z + v.z*ahead.z;
    
    return v.set(rx,ry,rz);
  }
  // ----------------------------------------------------------------- //
  public Vector3 transformOut( Vector3 v )                 { return transformOut(v,false); }
  public Vector3 transformOut( Vector3 v, boolean vector ) {
    
    float rx = v.x*right.x + v.y*right.y + v.z*right.z;
    float ry = v.x*up.x    + v.y*up.y    + v.z*up.z;
    float rz = v.x*ahead.x + v.y*ahead.y + v.z*ahead.z;
    
    v.set(rx,ry,rz);
 
    if( !vector ) v.inc(pos);
    return v;
  }
  // ----------------------------------------------------------------- //
  public void beginDraw( PGraphics g ) {
    g.pushMatrix();
    g.applyMatrix(
        right.x, right.y, right.z, pos.x,
        up.x,    up.y,    up.z,    pos.y,
        ahead.x, ahead.y, ahead.z, pos.z,
        0.0f,    0.0f,    0.0f,    1.0f
      );
  }
  // ----------------------------------------------------------------- //
  public void endDraw( PGraphics g ) {
    g.popMatrix();
  }
}
