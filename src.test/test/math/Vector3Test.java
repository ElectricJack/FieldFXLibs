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

import com.fieldfx.math.Vector3;

public class Vector3Test {

  private Vector3 v = null;
  private static final float eps = 0.0001f;
  
  // ---------------------------------------------------------------- //
  @Before
  public void runBeforeEveryTest() { v = new Vector3(); }
  @After 
  public void runAfterEveryTest() { v = null; }
  
  // ---------------------------------------------------------------- //  
  @Test
  public void testIncVector3() {
    v.inc( 0.5f, -2.0f, 1.0f );
    assertEquals( 0.5f, v.x, eps );
    assertEquals(-2.0f, v.y, eps );
    assertEquals( 1.0f, v.z, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDecVector3() {
    v.dec( 0.5f, -2.0f, 1.0f );
    assertEquals(-0.5f, v.x, eps );
    assertEquals( 2.0f, v.y, eps );
    assertEquals(-1.0f, v.z, eps );
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMuleqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDiveqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testModeqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMideq() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testInveq() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testCrosseqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testPrjeqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testRefeqVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testLenlen() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testLen() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testDotVector3() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testNrmeq() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMax() {
    fail("Not yet implemented");
  }
  // ---------------------------------------------------------------- //
  @Test
  public void testMin() {
    fail("Not yet implemented");
  }
}
