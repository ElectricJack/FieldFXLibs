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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fieldfx.math.LineSeg2;
import com.fieldfx.math.Poly2;
import com.fieldfx.math.Vector2;

public class Poly2Test {
  private static final float eps  = 0.0001f;
  private              Poly2 poly = null;
  
  @Before
  public void runBeforeEveryTest() { 
    poly = new Poly2();
    poly.add( new Vector2(-1,1) );
    poly.add(  1,  1f );
    poly.add(  1, -1 );
    poly.add( -1, -1 );
  }
  @After 
  public void runAfterEveryTest()  { poly = null; }
  @Test
  public void testEdges() {
    List<Poly2.Edge> edges = poly.edges();
    assertEquals( edges.size(), 4 );
  }
  @Test
  public void testContains() {
    poly = new Poly2();
    poly.add( new Vector2(-2,1) );
    poly.add(   2,  1.8f );
    poly.add(   2, -0.1f );
    poly.add(  -2, -1.0f );
    
    assertEquals( poly.contains(0,0),        true  ); // Inside
    assertEquals( poly.contains(3,1.5f),     false ); // Outside
    assertEquals( poly.contains(0.2f,-0.2f), true  ); // Fraction
    assertEquals( poly.contains(-2,1),       true ); // Corner
    assertEquals( poly.contains(0,0.5f),     true  ); // Edge
    assertEquals( poly.contains(0,10),       false ); // Above
    assertEquals( poly.contains(3,0.2f),    false ); // Right
    assertEquals( poly.contains(4,-0.2f),    false ); // Right
    assertEquals( poly.contains(-10,0.5f),   false ); // Left
    assertEquals( poly.contains(0,-10),      false ); // Below
  }
  @Test
  public void testCuts() {
    Poly2 p2 = new Poly2();
          p2.add( 0, 0 );
          p2.add( 0, 2 );
          p2.add( 2, 2 );
          p2.add( 2, 0 );
          
    List<Integer> cuts = poly.cuteq( p2 );
    
    assertEquals( cuts.size(), 2 );
  }  
  @Test
  public void testCutsOnEdge() {
    Poly2 p2 = new Poly2();
          p2.add( 0, 0 );
          p2.add( 0, 1 );
          p2.add( 1, 1 );
          p2.add( 1, 0 );
          
    List<Integer> cuts = poly.cuteq( p2 );
    
    assertEquals( cuts.size(), 2 );
    assertEquals( cuts.toString(), "[1, 3]" );
  }  
  @Test
  public void testUnion() {
    Poly2 p2 = new Poly2();
          p2.add( 0, 0 );
          p2.add( 0, 2 );
          p2.add( 2, 2 );
          p2.add( 2, 0 );
    
    poly.unioneq( p2 );
    
    assertEquals( poly.points.size(), 8 );
    assertEquals( poly.points.toString(), "[[-1.0, 1.0], [0.0, 1.0], [0.0, 2.0], [2.0, 2.0], [2.0, 0.0], [1.0, 0.0], [1.0, -1.0], [-1.0, -1.0]]" );
    
  }
  @Test
  public void testUnionOverlap() {
    Poly2 p2 = new Poly2();
          p2.add( 0, 0 );
          p2.add( 0, 1 );
          p2.add( 1, 1 );
          p2.add( 1, 0 );
    
    poly.unioneq( p2 );
    
    assertEquals( poly.points.size(), 6 );
    assertEquals( poly.points.toString(), "[[-1.0, 1.0], [0.0, 1.0], [1.0, 1.0], [1.0, 0.0], [1.0, -1.0], [-1.0, -1.0]]" );
  }

  @Test
  public void testRawOffset() {
                                   poly.add( 0, 0 );
    List<Poly2.Edge> offsetEdges = poly.rawOffset( 1.0f );
  }
  
  @Test
  public void testOffset() {
                         //poly.add( 0, 0 );
    //List<Poly2> polies = poly.offset( 1.0f, 1 );
  }
}
