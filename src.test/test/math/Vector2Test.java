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

import com.fieldfx.math.Vector2;

public class Vector2Test {
  private static final float   eps = 0.0001f;
  private              Vector2 v   = new Vector2();
  // ---------------------------------------------------------------- //
  @Before
  public void runBeforeEveryTest() { v = new Vector2(); }
  @After 
  public void runAfterEveryTest()  { v = null; }
  // ---------------------------------------------------------------- //
  @Test
  public void testLenlen() {
    v.set(10,10);
    assertEquals( 200.0f, v.lenlen(), eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testLen() {
    v.set(10,10);
    assertEquals( Math.sqrt(200.0f), v.len(), eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDotVector2() {
    assertEquals( -40.f, v.set(10,1).dot( new Vector2(-5,10) ), eps );
    assertEquals( 200.f, v.set(10,10).dot( v ), eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testNrmeq() {
    assertEquals( 1.0f, v.set(4,4).nrmeq().len(), eps );
    assertEquals( 1.f/Math.sqrt(2.0), v.set(4,4).nrmeq().x, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testIncVector2() {
    v.set(4,4).inc(-0.5f,2.0f);
    assertEquals( 3.5f, v.x, eps );
    assertEquals( 6.0f, v.y, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDecVector2() {
    v.set(4,4).dec(-0.5f,2.0f);
    assertEquals( 4.5f, v.x, eps );
    assertEquals( 2.0f, v.y, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMuleqVector2() {
    v.set(10,10).muleq(-0.5f,2.0f);
    assertEquals( -5.0f, v.x, eps );
    assertEquals( 20.0f, v.y, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDiveqVector2() {
    v.set(10,10).diveq(-0.5f,2.0f);
    assertEquals(-20.0f, v.x, eps );
    assertEquals(  5.0f, v.y, eps );

  }
  // ---------------------------------------------------------------- //
  @Test
  public void testModeqVector2() {
    v.set(2,6);
    v.modeq(2);
    assertEquals( v.sub(0,0).lenlen(), 0, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMideq() {
    v.set(4,8);
    v.mideq();
    assertEquals( v.sub(2,4).lenlen(), 0, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testInveq() {
    v.set(1,4);
    v.inveq();
    assertEquals( v.sub( 1, 0.25f ).lenlen(), 0, eps );
  }

  @Test
  public void testPrjeqVector2() {
    v.set(1,1);
    Vector2 p = new Vector2(-2,5);
            p.prjeq(v);
            
    assertEquals( p.sub(3.f,3.f).lenlen(), 0, eps );
  }

  @Test
  public void testRefeqVector2() {
    v.set(1,1);
    Vector2 p = new Vector2(-2,5);
            p.refeq(v);
            
    assertEquals( p.sub(5.f,-2.f).lenlen(), 0, eps );
  }

  @Test
  public void testRoteq() {
    v.set(1,1);
    v.roteq(3.14159f);
    
    assertEquals( v.sub(-1.f,-1.f).lenlen(), 0, eps );
  }

  @Test
  public void testMin() {
    v.set(1,1);
    v = v.min( new Vector2(-1,2) );
    
    assertEquals( v.sub(-1.f,1.f).lenlen(), 0, eps );
  }

  @Test
  public void testMax() {
    v.set(1,1);
    v = v.max( new Vector2(-1,2) );
    
    assertEquals( v.sub(1.f,2.f).lenlen(), 0, eps );
  }

  @Test
  public void testLerp() {
    v = v.lerp( new Vector2(1,1), new Vector2(5,9), 0.5f );
    
    assertEquals( v.sub(3.f,5.f).lenlen(), 0, eps );
  }

}
