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

public class Matrix22 implements Serializable  {
  public float xx, xy;
  public float yx, yy;
  
  // ------------------------------------------------------------------ //  
  public Matrix22( Matrix22 m )                             { set(m); }
  public Matrix22( float xx, float xy, float yx, float yy ) { set( xx, xy, yx, yy ); }
  public Matrix22()                                         { set(); }

  // ------------------------------------------------------------------ //
  public Matrix22 get()             { return new Matrix22(this); }
  
  // ------------------------------------------------------------------ //
  public Vector2  get( int column ) { 
    if( column < 0 || column >= 2 ) return null;
    if( column == 0 ) return new Vector2(xx,yx);
    else              return new Vector2(xy,yy);
  }
  
  // ------------------------------------------------------------------ //
  public float[]  getArr()              { return new float[] { xx, xy, yx, yy }; }
  // ------------------------------------------------------------------ //  
  public void     set()                                             { xx =    1; xy =    0; yx =    0; yy  =   1; }
  public void     set( Matrix22 m )                                 { xx = m.xx; xy = m.xy; yx = m.yx; yy = m.yy; }
  public void     set( float mxx, float mxy, float myx, float myy ) { xx =  mxx; xy =  mxy; yx =  myx; yy =  myy; }
  // ------------------------------------------------------------------ //  
  public Vector2  mul( Vector2 v ) { 
    return null;
  }
  // ------------------------------------------------------------------ //  
  public Matrix22 mul( Matrix22 m ) {
    return null;
  }
  // ------------------------------------------------------------------ //  
  public void muleq( Matrix22 m ) {
    
  }
  // ------------------------------------------------------------------ //  
  public float determinant() { 
    return 0;
  }
  // ------------------------------------------------------------------ //
  public void transpose() {
    
  }
  // ------------------------------------------------------------------ //  
  public void invert() {
    
  }
  // ------------------------------------------------------------------ //  
  public String       getType() { return "Matrix22";     }
  public Serializable clone()   { return new Matrix22(); }
  // ------------------------------------------------------------------ //  
  @Override
  public void serialize(Serializer s) {
    xx = s.serialize("",xx);
    xy = s.serialize("",xy);
    yx = s.serialize("",yx);
    yy = s.serialize("",yy);
  }
  
}
