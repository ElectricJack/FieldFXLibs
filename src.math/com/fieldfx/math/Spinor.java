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

public class Spinor {
  public float real;
  public float complex;
  
  public static Spinor lerp( Spinor out, Spinor a, Spinor b, float t ) {
    //Return startVal.GetScale(1 - t).GetAdd(endVal.GetScale(t)).GetNormalized()
    return out.set(a).muleq(1-t).inc( b.mul(t) ).nrmeq();
  }
  public static Spinor slerp( Spinor out, Spinor a, Spinor b, float t ) {
    
    float cosom = a.real*b.real + a.complex * b.complex;
    
    float tr, tc;
    if( cosom < 0 ) {
      cosom *= -1;
      tc = -b.complex;
      tr = -b.real;
    } else {
      tc = b.complex;
      tr = b.real;      
    }
    
    float scale0;
    float scale1;
    if( (1-cosom) > 0.001 ) {
      float omega = (float)Math.acos(cosom);
      float sinom = (float)Math.sin(omega);
      //scale0 = sin( (1-t)*omega ) / sinom;
    } else {
      
    }
    
    /*
    Local tr:Float
    Local tc:Float
    Local omega:Float, cosom:Float, sinom:Float, scale0:Float, scale1:Float
    
    'calc cosine
    cosom = from.real * dest.real + from.complex * dest.complex
    
    'adjust signs
    If (cosom <0) Then
        cosom = -cosom
        tc = -dest.complex
        tr = -dest.real
    Else
        tc = dest.complex
        tr = dest.real
    End If
    
    ' coefficients
    If (1 - cosom)> 0.001 Then 'threshold, use linear interp if too close
        omega = ACos(cosom)
        sinom = Sin(omega)
        scale0 = Sin((1 - t) * omega) / sinom
        scale1 = Sin(t * omega) / sinom
    Else
        scale0 = 1 - t
        scale1 = t
    End If
    
    ' calc final
    Local res:Spinor = Spinor.Create(0, 0)
    res.complex = scale0 * from.complex + scale1 * tc
    res.real = scale0 * from.real + scale1 * tr
    Return res*/
    
    return out;
  }
  
  public        Spinor( Spinor other )              { set( other ); }
  public        Spinor( float ang )                 { set( ang ); }
  public        Spinor( float real, float complex ) { set( real, complex ); }
  
  
  public Spinor set   ( Spinor other )              { real = other.real; complex = other.complex; return this; }
  public Spinor set   ( float ang )                 { real = (float)Math.cos( ang ); complex = (float)Math.sin(ang); return this; }
  public Spinor set   ( float real, float complex ) { this.real = real; this.complex = complex; return this; }
  
  public Spinor get   ()                            { return new Spinor(this); }
  
  public Spinor add   ( Spinor s )                  { return get().inc(s); }
  public Spinor mul   ( float  t )                  { return get().muleq(t); }
  public Spinor nrm   ( )                           { return get().nrmeq(); }
  public Spinor inv   ( )                           { return get().inveq(); }
  
  
  public Spinor inc   ( Spinor s )                  { real += s.real; complex += s.complex; return this; }
  public Spinor muleq ( float t )                   { real *= t; complex *= t; return this; }
  
  public Spinor muleq ( Spinor s ) {
    float r = real * s.real - complex * s.complex;
    complex = real * s.complex + complex * s.real;
    real    = r;
    return this;
  }
  
  public Spinor nrmeq()  {
    float      len = len();
    real    /= len;
    complex /= len;
    
    return this;
  }
  
  public Spinor inveq()  { 
    set( real, -complex ).muleq( lenlen() );
    return this;
  }
  
  
  public float  ang()    { return (float)( Math.atan2( complex, real ) * 2.0 ); }
  public float  len()    { return (float)Math.sqrt( lenlen() ); }
  public float  lenlen() { return real*real + complex*complex;  }
  

}
