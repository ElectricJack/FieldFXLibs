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
import com.fieldfx.math.Vector2;

public class Line2Test {
  private static final float   eps = 0.0001f;
  
  @Test
  public void testProject() {
    
    Vector2 v = new Vector2 ( 0,2 );
    Line2   l = new Line2   ( 0,0, 2,2 );
    Vector2 p = l.project(v);
    
    assertEquals( p.x, 1.0f, eps );
    assertEquals( p.y, 1.0f, eps );
  }

  @Test
  public void testInside() {
    
    Vector2 v1 = new Vector2 ( 0,2 );
    Vector2 v2 = new Vector2 ( 1,1 );
    Vector2 v3 = new Vector2 ( 2,0 );
    
    Line2   l = new Line2   ( 0,0, 2,2 );
    
    assertEquals( l.inside(v1), false );
    assertEquals( l.inside(v2), true  );
    assertEquals( l.inside(v3), true  );
  }

  @Test
  public void testCollinear() {
    Vector2 v1 = new Vector2 ( 0,0 );
    Vector2 v2 = new Vector2 ( 1,1 );
    Vector2 v3 = new Vector2 ( 3,3 );
    Vector2 v4 = new Vector2 ( 3,2 );
    Vector2 v5 = new Vector2 ( -2,2 );
    
    Line2   l = new Line2   ( 0,0, 2,2 );
    
    assertEquals( l.collinear( v1 ), true );
    assertEquals( l.collinear( v2 ), true );
    assertEquals( l.collinear( v3 ), true );
    assertEquals( l.collinear( v4 ), false );
    assertEquals( l.collinear( v5 ), false );
  }
  
  @Test
  public void testIntersectHoriz() {
    Line2   l1 = new Line2( 0,0, 2,0 );
    Line2   l2 = new Line2( 1,1, 2,2 );
    Vector2 p  = l1.intersect(l2);
    
    assertEquals( p.x, 0.0f, eps );
    assertEquals( p.y, 0.0f, eps );
  }
  
  @Test
  public void testIntersectVertz() {
    Line2   l1 = new Line2( 2,0, 2,2 );
    Line2   l2 = new Line2( 0,0, 1,1 );
    Vector2 p  = l1.intersect(l2);
    
    assertEquals( p.x, 2.0f, eps );
    assertEquals( p.y, 2.0f, eps );
  }
}
