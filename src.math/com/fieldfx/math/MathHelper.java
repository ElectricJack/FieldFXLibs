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

public class MathHelper {
  public static final float PI = 3.1415926f;

  // ------------------------------------------------------------------------------------------------------------- //
  public static float sin( float theta ) { return (float)Math.sin( theta ); }
  public static float cos( float theta ) { return (float)Math.cos( theta ); }
  // ------------------------------------------------------------------------------------------------------------- //
  static public float lerp( float a, float b, float t ) {
    return a*(1.f - t) + b*t;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public float map( float value, float fromMin, float fromMax, float toMin, float toMax ) {
    return  toMin + (toMax - toMin) * ((value - fromMin) / (fromMax - fromMin));
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public Vector3 bezierPoint( Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3, float t ) {
    return new Vector3( bezierPoint( v0.x,v1.x,v2.x,v3.x, t )
                      , bezierPoint( v0.y,v1.y,v2.y,v3.y, t )
                      , bezierPoint( v0.z,v1.z,v2.z,v3.z, t ) );    
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public Vector2 bezierPoint( Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3, float t ) {
    return new Vector2( bezierPoint( v0.x,v1.x,v2.x,v3.x, t )
                      , bezierPoint( v0.y,v1.y,v2.y,v3.y, t ) );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public float bezierPoint( float v0, float v1, float v2, float v3, float t ) {
    float t2 = t*t;
    float t3 = t2*t;
    float c  = 3*(v1 - v0);
    float b  = 3*(v2 - v1) - c;
    float a  = v3 - v0 - c - b;
    return a*t3 + b*t2 + c*t + v0;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public float estimateBezierLength( Vector3 pa, Vector3 pb, Vector3 pc, Vector3 pd ) {
    float  dist  = pb.sub( pa ).len();
           dist += pc.sub( pb ).len();
           dist += pd.sub( pc ).len();
    return dist;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  static public float radians( float degrees ) {
    return degrees * PI / 180.f;
  }

}
