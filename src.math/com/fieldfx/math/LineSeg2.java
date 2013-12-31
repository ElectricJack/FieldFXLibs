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

import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;
import com.fieldfx.math.Line2;
import com.fieldfx.math.Vector2;

import processing.core.PGraphics;

public class LineSeg2 extends Line2 implements Serializable
{
  // No frontin' ( public data )
  public Vector2 v1 = new Vector2();
  
  // ------------------------------------------------------------------------------------------------------------ //
  public String       getType   ( )              { return "LineSeg2"; }
  public Serializable clone     ( )              { return new LineSeg2(); }
  public void         serialize ( Serializer s ) {
    super.serialize(s);
    s.serialize( "v1", v1 );
  }
  
  
  // --------------------------------------------------------- //
  public LineSeg2() {}
  public LineSeg2(float x0, float y0, float x1, float y1 ) { this( new Vector2(x0,y0), new Vector2(x1,y1) ); }
  public LineSeg2( Vector2 v0, Vector2 v1 ) {
    super( v0, v1 );
    this.v1.set( v1 );
  }
  // --------------------------------------------------------- //  
  public LineSeg2 flip() {
    Vector2 v3 = v0;
            v0 = v1;
            v1 = v3;
    return this;
  }
  // --------------------------------------------------------- //
  public LineSeg2 set( Vector2 v0, Vector2 v1 ) { 
  	super.set( v0, v1 );
  	if( this.v1 != null )
  	    this.v1.set( v1 );
  	return this;
  }
  // --------------------------------------------------------- //
  public LineSeg2 set( int index,  Vector2 v  ) {
    super.set( index == 0 ? v : v0
             , index == 1 ? v : v1 );
    return this;
  }
  public float length() {
    return v1.sub(v0).len();
  }
  
  // --------------------------------------------------------- //
  public Vector2 getPoint( int index ) {
    return index == 0 ? v0.get() : v1.get();
  }
  
  // --------------------------------------------------------- //
  public boolean contains( Vector2 point ) {
    // First let's calculate the time along the line for this point
    //  as the inital rejection test because it's cheaper
    Float t = time( point );
    if( t == null ) return false;
    
    // Finally only return true, if the time lies on the segment AND
    //  the point is collinear to the line.
    if( t < 0.f || t > 1.f ) return false;

    return collinear( point );
  }
  
  // --------------------------------------------------------- //
  public Float time( Vector2 point ) {
    float dy = v1.y - v0.y;
    float dx = v1.x - v0.x;    
    float adx = Math.abs(dx);
    float ady = Math.abs(dy);
    if( adx > ady && adx > 0.001f ) {
      return (point.x - v0.x) / dx;
    } else if( ady > 0.001f ){
      return (point.y - v0.y) / dy;
    }
    return null;
  }

  
  public Vector2 normal()  { Vector2 n = tangent(); return n.set(n.y, -n.x); }
  public Vector2 tangent() { return v1.sub(v0).nrmeq(); }
  
  // --------------------------------------------------------- //
  public Vector2 intersect( LineSeg2 segment )              { return intersect( new Vector2(), segment ); }
  public Vector2 intersect( Vector2 out, LineSeg2 segment ) {
    if( out == null || segment == null ) return null;
    
    // If any of the end-points are equal, then this is not an
    //  intersection of the segments.
    float eps = 0.001f;
    if( v0.sub( segment.v0 ).lenlen() < eps
     || v0.sub( segment.v1 ).lenlen() < eps
     || v1.sub( segment.v0 ).lenlen() < eps
     || v1.sub( segment.v1 ).lenlen() < eps ) {
      return null;
    }

    // http://en.wikipedia.org/wiki/Line-line_intersection
    float x1 = v0.x,         y1 = v0.y;
    float x2 = v1.x,         y2 = v1.y;
    float x3 = segment.v0.x, y3 = segment.v0.y;
    float x4 = segment.v1.x, y4 = segment.v1.y;
    
    float d = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
    if( Math.abs(d) < 0.001f ) {
      return null;
    }
    
    float s1 = (x1 * y2 - y1 * x2);
    float s2 = (x3 * y4 - y3 * x4);
    float nx = s1*(x3 - x4) - (x1 - x2)*s2;
    float ny = s1*(y3 - y4) - (y1 - y2)*s2;
    
    out.set( nx / d, ny / d );
    
    // If the result is contained in the first line segment
    //  and the second line segment
    if( contains(out) &&  segment.contains(out) ) {
      // Then we have a winner!
      return out;
    }

    // Boo hoo.. no intersect
    return null;
  }
  // --------------------------------------------------------- //
  public boolean collinear( float x, float y ) {
    super.set( v0, v1 );
    return super.collinear( x, y );
  }
  // --------------------------------------------------------- //
  public boolean collinear( Vector2 point ) {
    super.set( v0, v1 );
    return super.collinear( point );
  }
  // --------------------------------------------------------- //
  public Vector2 project( Vector2 point ) {
    return super.project( point );
  }
}
