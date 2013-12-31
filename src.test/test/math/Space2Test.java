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

import com.fieldfx.math.Poly2;
import com.fieldfx.math.Space2;
import com.fieldfx.math.Vector2;

public class Space2Test {
  private static final float   eps = 0.0001f;
  
  private Vector2 v = new Vector2( 1, 0 ); 
  private Space2  s = null;
  
  @Before
  public void runBeforeEveryTest() { 
    s = new Space2();
    s.setAng( 3.14159f * 0.5f );
    s.set(1.f, 1.f);
  }
  @After 
  public void runAfterEveryTest()  { s = null; }
  @Test
  public void testSetAng() {
                        s.setAng( 3.14159f );
    Vector2       vIn = s.transformIn(v);
    assertEquals( vIn.sub( new Vector2(0.f, -1.f) ).lenlen(), 0, eps );
  }

  @Test
  public void testRot() {
                        s.rot( 3.14159f * 0.5f );
    Vector2       vIn = s.transformIn(v);
    assertEquals( vIn.sub( new Vector2(0.f, -1.f) ).lenlen(), 0, eps );
  }

  @Test
  public void testTransformInPoint() {
    Vector2       vIn = s.transformIn(v);
    assertEquals( vIn.sub( new Vector2(-1.f, 0.0f) ).lenlen(), 0, eps );
  }

  @Test
  public void testTransformInVector() {
    Vector2       vIn = s.transformIn(v,true);
    assertEquals( vIn.sub( new Vector2(0.f, 1.0f) ).lenlen(), 0, eps );
  }

  @Test
  public void testTransformOutPoint() {
    Vector2       vOut = s.transformOut(v);
    assertEquals( vOut.sub( new Vector2(1.f, 2.0f) ).lenlen(), 0, eps );
  }

  @Test
  public void testTransformOutVector() {
    Vector2       vOut = s.transformOut(v,true);
    assertEquals( vOut.sub( new Vector2(0.f, 1.0f) ).lenlen(), 0, eps );
  }

}
