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

import org.junit.Test;

import com.fieldfx.math.Line2;
import com.fieldfx.math.LineSeg2;
import com.fieldfx.math.Vector2;

public class LineSeg2Test {
  private static final float   eps = 0.0001f;
  
  @Test
  public void testProject() {
    
    Vector2  v = new Vector2  ( 0,2 );
    LineSeg2 l = new LineSeg2 ( 0,0, 2,2 );
    Vector2  p = l.project    ( v );
    
    assertEquals( p.x, 1.0f, eps );
    assertEquals( p.y, 1.0f, eps );
  }

  @Test
  public void testFlip() {
    LineSeg2 l = new LineSeg2 ( 0,0, 2,3 );
    
    assertEquals( l.v0.x, 0.0f, eps );
    assertEquals( l.v1.x, 2.0f, eps );
    
    l.flip();
    
    assertEquals( l.v1.x, 0.0f, eps );
    assertEquals( l.v0.x, 2.0f, eps );    
  }

  @Test
  public void testContains() {
    LineSeg2 l  = new LineSeg2 ( 0,0, 2,4 );
    Vector2  v0 = new Vector2  ( 1,2 );
    Vector2  v1 = new Vector2  ( 0,0 );
    Vector2  v2 = new Vector2  ( 2,4 );
    Vector2  v3 = new Vector2  ( 2,2 );
    Vector2  v4 = new Vector2  ( -1,-2 );
    
    assertEquals( l.contains(v0), true );
    assertEquals( l.contains(v1), true );
    assertEquals( l.contains(v2), true ); 
    assertEquals( l.contains(v3), false ); 
    assertEquals( l.contains(v4), false );
  }

  @Test
  public void testTime() {
    LineSeg2 l = new LineSeg2 ( 0,0, 2,4 );
    Vector2  v = new Vector2  ( 1,2 );
    assertEquals( l.time(v), 0.5, eps );
  }

  @Test
  public void testIntersectFail() {
    // These line segments do not intersect, but their continuous lines do.
    LineSeg2 l1 = new LineSeg2( 0,0, 2,0 );
    LineSeg2 l2 = new LineSeg2( 1,1, 2,2 );
    Vector2  p  = l1.intersect(l2);
    
    assertEquals( p, null );
  }
  
  @Test
  public void testIntersectHoriz() {
    //fail("Not yet implemented");
    LineSeg2 l1 = new LineSeg2( 0,0, 2,0 );
    LineSeg2 l2 = new LineSeg2( -1,-1, 2,2 );
    Vector2  p  = l1.intersect(l2);
    
    assertEquals( p.x, 0.0f, eps );
    assertEquals( p.y, 0.0f, eps );
  }
  
  @Test
  public void testIntersectVert() {
    //fail("Not yet implemented");
    LineSeg2 l1 = new LineSeg2( 0,0, 0,2 );
    LineSeg2 l2 = new LineSeg2( -1,-1, 2,2 );
    Vector2  p  = l1.intersect(l2);
    
    assertEquals( p.x, 0.0f, eps );
    assertEquals( p.y, 0.0f, eps );
  }
  
  @Test
  public void testNormal() {
    Vector2  v = new Vector2  ( 1,-1 );
             v.nrmeq();
    LineSeg2 l = new LineSeg2 ( 0,0, 2,2 );
    Vector2  n = l.normal();
    
    assertEquals( n.sub(v).lenlen(), 0, eps );   
  }
  
  @Test
  public void testTangent() {
    Vector2  v = new Vector2  ( 1,1 );
             v.nrmeq();
    LineSeg2 l = new LineSeg2 ( 0,0, 2,2 );
    Vector2  t = l.tangent();
    
    assertEquals( t.sub(v).lenlen(), 0, eps );
  }
}
