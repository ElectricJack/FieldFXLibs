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
package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fieldfx.math.Matrix22;
import com.fieldfx.math.Vector2;

public class Matrix22Test {

  private Matrix22           m   = null;
  private static final float eps = 0.0001f;
  
  // ---------------------------------------------------------------- //
  @Before
  public void runBeforeEveryTest() { m = new Matrix22(); }
  @After 
  public void runAfterEveryTest()  { m = null; }
  
  // ---------------------------------------------------------------- //
  @Test
  public void testGet() {
    Matrix22 m2 = m.get();
    assertNotSame( m, m2 );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testGetInt() {
    assertEquals( 1.0f, m.get(0).x, eps );
    assertEquals( 0.0f, m.get(0).y, eps );
    assertEquals( 0.0f, m.get(1).x, eps );
    assertEquals( 1.0f, m.get(1).y, eps );
    
    m.xy = 5.0f;
    assertEquals( 5.0f, m.get(1).x, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testGetArr() {
    assertArrayEquals( new float[] {1.f, 0.0f, 0.0f, 1.0f}, m.getArr(), eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMulVector2() {
    Vector2 v = new Vector2( 5.0f, 1.0f );
    Vector2 v2 = m.mul(v);
    assertNotSame( null, v2 );
    assertNotSame( v,    v2 );
    if( v2 != null ) {
      assertEquals( v.x, v2.x, eps );
      assertEquals( v.y, v2.y, eps );
    }
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMuleq() {
    Matrix22 m2 = new Matrix22(1,2,3,4);
    m.muleq(m2);
    assertEquals( m2.xx, m.xx, eps );
    assertEquals( m2.xy, m.xy, eps );
    assertEquals( m2.yx, m.yx, eps );
    assertEquals( m2.yy, m.yy, eps );
    m.muleq(m2);
    assertEquals(  7.0f, m.xx, eps );
    assertEquals( 10.0f, m.xy, eps );
    assertEquals( 15.0f, m.yx, eps );
    assertEquals( 22.0f, m.yy, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDeterminant() {
    assertEquals( 1.0f, m.determinant(), eps );
    m.set( 1, -4
         , 0,  3 );
    assertEquals( 3.0f, m.determinant(), eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testTranspose() {
    m.transpose();
    assertEquals( 1.0f, m.xx, eps );
    assertEquals( 0.0f, m.xy, eps );
    assertEquals( 0.0f, m.yx, eps );
    assertEquals( 1.0f, m.yy, eps );
    m.xy = 5.0f;
    m.transpose();
    assertEquals( 5.0f, m.yx, eps );
    assertEquals( 0.0f, m.xy, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testInvert() {
    
  }

}
