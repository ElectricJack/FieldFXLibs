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

public class Space2 {
   private Vector2 pos   = new Vector2( );
   private Vector2 right = new Vector2( 1,  0 );
   private Vector2 up    = new Vector2( 0, -1 );
   
   // ----------------------------------------------------------------- //
   public Space2() {}
   public Space2( Space2 other ) {
     set( other );
   }
   // ----------------------------------------------------------------- //
   public Space2 set( Space2 other ) {
     pos.set   ( other.pos   );
     right.set ( other.right );
     up.set    ( other.up    );
     return this;
   }
   // ----------------------------------------------------------------- //
   public Space2 set( float x, float y ) {
     pos.x = x;
     pos.y = y;
     return this;
   }
   // ----------------------------------------------------------------- //
   public Space2 set( Vector2 pos ) {
     set( pos.x, pos.y );
     return this;
   }
   // ----------------------------------------------------------------- //
   public Space2 setAng( float a ) {
     right.set(1,0);
     up.set(0,-1);
     rot(a);
     
     return this;
   }
   // ----------------------------------------------------------------- //
   public Space2 rot( float a ) {
     up.roteq(a);
     right.roteq(a);
     return this;
   }
   // ----------------------------------------------------------------- //
   public Space2 get() { 
     return new Space2(this);
   }
   // ----------------------------------------------------------------- //
   public Vector2 transformIn( Vector2 v )                 { return transformIn(v,false); }
   public Vector2 transformIn( Vector2 v, boolean vector ) {
     if( !vector ) v.dec(pos);

     float rx = v.x*right.x + v.y*up.x;
     float ry = v.x*right.y + v.y*up.y;
     
     return v.set(rx,ry);
   }
   // ----------------------------------------------------------------- //
   public Vector2 transformOut( Vector2 v )                 { return transformOut(v,false); }
   public Vector2 transformOut( Vector2 v, boolean vector ) {
     
     float rx = v.x*right.x + v.y*right.y;
     float ry = v.x*up.x    + v.y*up.y;
     v.set(rx,ry);
  
     if( !vector ) v.inc(pos);
     return v;
   }
   // ----------------------------------------------------------------- //
   public void beginDraw( PGraphics g ) {
     g.pushMatrix();
     g.applyMatrix(
         right.x, right.y, 0.0f,   pos.x,
         up.x,    up.y,    0.0f,   pos.y,
         0.0f,    0.0f,    1.0f,   0.0f,
         0.0f,    0.0f,    0.0f,   1.0f
       );
   }
   // ----------------------------------------------------------------- //
   public void endDraw( PGraphics g ) {
     g.popMatrix();
   }
}
