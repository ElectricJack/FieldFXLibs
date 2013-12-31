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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Comparator;

import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;

import processing.core.PGraphics;

//import javax.media.opengl.GL;
//import javax.media.opengl.glu.*;

public class Poly2 implements Serializable {
  
  @SuppressWarnings("serial")
  public class PointList extends ArrayList<Point2> {}
  public PointList points = new PointList();
  
  
  public class Point2 extends Vector2 {
    boolean  visited  = false;
    Point2   neighbor = null;
    Point2   next     = null;
    Point2   prev     = null;
    
    public Point2( ) { super(); }
    public Point2( Vector2 other ) { super( other ); }
    public Point2( float x, float y ) { super( x, y ); }
    public Point2( Point2 other ) { super( other.x, other.y ); }
    
    public Point2 get() { return new Point2(this); }
    
    public Serializable clone()   { return new Point2(); }
    public String       getType() { return "Point2";     }
    public void serialize(Serializer s) {
      super.serialize(s);
      //s.serialize( "neighbor", neighbor );
      //s.serialize( "next",     next     );
      //s.serialize( "prev",     prev     );
    }     
  }
  public class Edge extends LineSeg2 {
    public Vector2 arcCenter = null;
    
    public Edge( Vector2 v0, Vector2 v1 ) { super(v0, v1); }
    
    public Edge get() {
      return new Edge( v0, v1 );
    }
    public Edge offset( float distance ) {
      Vector2 vOff = normal().muleq( distance );
      v0.inc( vOff );
      v1.inc( vOff );
      return this;
    }
    
  }

  public Poly2 clean() {

    PointList toRemove = new PointList();
    for( int i=0; i<points.size(); ++i ) {
      if( points.get(i).sub( points.get( (i + 1) % points.size() ) ).lenlen() < 0.001f ) {
        toRemove.add( points.get(i) );
      }
    }
    
    for( Point2 p : toRemove ) {
      points.remove( p );
    }
    return this;
  }
  public Serializable clone()   { return new Poly2(); }
  public String       getType() { return "Poly2";     }
  public void serialize(Serializer s) {
    s.serialize( "points", points );
  } 
  
  // -------------------------------------------------------------------------------- //
  public Poly2 add( Vector2 v )        { return this.add(v.x,v.y); }
  public Poly2 add( float x, float y ) { 
    Point2 p = new Point2(x, y);

    if ( points.isEmpty() ) {
      p.next = p;
      p.prev = p;
    } 
    else {
      p.next      = points.get( 0 );
      p.next.prev = p;
      p.prev      = points.get( points.size() - 1 );
      p.prev.next = p;
    }

    points.add( p );
    
    // Return ourselves for command chaining.
    return this;
  }

  // -------------------------------------------------------------------------------- //
  public Poly2 get() {
    Poly2  clone = new Poly2();
    clone.points.addAll( points );
    return clone;
  }

  // -------------------------------------------------------------------------------- //
  public List<Edge> edges ()                       {  return edges( new ArrayList<Edge>() ); }
  public List<Edge> edges ( List<Edge> edges ) {
    if( edges == null ) return null;
    
    for ( int i=0; i<points.size(); ++i )
      edges.add( edge( i ) );

    return edges;
  }
  // -------------------------------------------------------------------------------- // 
  public int edgeCount() { int edgeCount = points.size(); return edgeCount > 0 ? edgeCount : 0; }
  // -------------------------------------------------------------------------------- // 
  public  Edge edge( int index ) { return new Edge( points.get( index % points.size() ), points.get( (index + 1) % points.size() ) ); }


  // -------------------------------------------------------------------------------- // 
  /*
   Union of two complex polygons:
   http://davis.wpi.edu/~matt/courses/clipping/
   */
  public Poly2 unioneq( Poly2 other ) {
    
    Poly2 us = this.get();
    List<Integer> thisCuts    = this.cuteq( other );       
    if ( thisCuts.isEmpty() ) return this;
    List<Integer> otherCuts   = other.cuteq( us );
    
    for ( Point2 p : points       ) p.visited = false;
    for ( Point2 p : other.points ) p.visited = false;

    bindIntersections( points, thisCuts, other.points, otherCuts );
    
    // We want to start on 
    int startIndex = 0;
    for( int i=0; i<points.size(); ++i ) {
             startIndex = i;
      Point2 point      = points.get( i );
      if( !other.contains( point ) ) break;
    }
        

    PointList  newPoly    = new PointList();
    Point2     p          = points.get( startIndex );

    while ( !p.visited ) {

      p.visited = true;
      newPoly.add( p.get() );

      if ( p.neighbor != null ) { 
        p = p.neighbor; 
      }

      p = p.next;
    }

    points = newPoly;
    
    // Return ourselves for command chaining
    return this;
  }
  
  
  // -------------------------------------------------------------------------------- //
  public List<Edge> rawOffset( List<Edge> offsetEdges, float distance ) {
    if( offsetEdges == null ) return null;
    
    // Build a list of edge segments with each offset along it's normal
    // and generate the connection arcs between all the offset edges
    
    List< Edge > edges = edges();
        
    for( int i=0; i<edges.size(); ++i ) {
      
      Edge       e0           = edges.get( i ).get();
      Edge       e1           = edges.get( (i+1) % edges.size() ).get(); 
      Vector2    sharedVertex = e0.v1.get();
      
      e0.offset( distance );
      e1.offset( distance );
      
      offsetEdges.add( e0 );
      
      Edge             sharedEdge = new Edge( e0.v1, e1.v0 );
                       sharedEdge.arcCenter = sharedVertex;
      offsetEdges.add( sharedEdge );
    }    
    
    return offsetEdges;
  }
  // -------------------------------------------------------------------------------- //
  public List<Edge> rawOffset( float distance ) {
    return rawOffset( new ArrayList<Edge>(), distance );
  }
  
  // -------------------------------------------------------------------------------- //
  // private class PolyBuilder extends GLUtessellatorCallbackAdapter
  // {
  //   private List<Poly2> polies = new ArrayList<Poly2>();
  //   private Poly2       active = null;
  //   private GLU         glu    = null;
    
  //   public PolyBuilder( GLU glu ) { this.glu = glu; }
    
  //   public List<Poly2> get() { return polies; }
    
    
  //   public void begin(int type) {
  //     active = new Poly2();
  //   }
    
  //   public void end() {
  //     if( active == null ) return;
      
  //     active.add( active.points.get(0) );
  //     polies.add( active );
  //     active = null;
  //   }
    
  //   public void vertex( Object vertexData ) {
  //     if (vertexData instanceof Vector2 && active != null ) {
  //       active.add( (Vector2) vertexData );
  //     }
  //   }
    
  //   /*
  //    * combineCallback is used to create a new vertex when edges intersect.
  //    * coordinate location is trivial to calculate, but weight[4] may be used to
  //    * average color, normal, or texture coordinate data. In this program, color
  //    * is weighted.
  //    */
  //   public void combine(double[] coords, Object[] data, float[] weight, Object[] outData)
  //   {
  //     if( outData != null && coords != null )
  //       outData[0] = new Vector2((float)coords[0],(float)coords[1]);
  //   }
  //   public void error(int errnum)
  //   {
  //     String estring = glu.gluErrorString(errnum);
  //     System.err.println("Tessellation Error: " + estring);
  //     System.exit(0);
  //   }
  // }
  
  
  // -------------------------------------------------------------------------------- //
  // public List<Poly2> offset( GLU glu, List<Poly2> out, float distance, int arcPoints ) {
  //   if( out == null ) return null;
    
  //   // First calculate the raw offset polygon without
  //   //  andy clipped edges
  //   List<Edge> offsetPoly = rawOffset( distance );
    
    
  //   // Generate the arcs for the intersection code
  //   //  and store offset edges in a new poly2
  //   Poly2 offsetPoly2 = new Poly2();
  //   for( Edge e : offsetPoly ) {
      
  //     offsetPoly2.add( e.v0 );
      
  //     if( e.arcCenter != null &&
  //       e.v0.sub(e.v1).lenlen() > 0.001 ) {
        
  //       Vector3 rv0  = new Vector3( e.v0.sub( e.arcCenter ), 0);
  //       Vector3 rv1  = new Vector3( e.v1.sub( e.arcCenter ), 0);
  //       Vector3 rvUp = rv0.cross( rv1 );
        
  //       if( rvUp.z > 0 ) {
  //         rvUp.nrmeq();
  //         float   ang  = (float)Math.acos( rv0.nrm().dot( rv1.nrmeq() ));
          
  //         float   angInc = ang / arcPoints;
  //         float   a = angInc * 0.5f; 
  //         for( int i=0; i<arcPoints; ++i, a += angInc ) {
  //           Vector2 vSlerped = rv0.rot(rvUp, a).getXY();
  //           offsetPoly2.add( vSlerped.inc( e.arcCenter ) );
  //         }
          
  //       } else {
  //         offsetPoly2.add( e.arcCenter );
  //         offsetPoly2.add( e.arcCenter );
  //       }
  //     }
      
  //     offsetPoly2.add( e.v1 );
  //   }
  //   offsetPoly2.clean();

  //   boolean enableTesselation = true;
  //   if( enableTesselation ) {
  //     PolyBuilder     events = new PolyBuilder( glu );
  //     GLUtessellator  tess   = glu.gluNewTess();
      
  //     try {
        
  //       glu.gluTessProperty( tess, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO );
  //       //glu.gluTessProperty( tess, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE );
  //       glu.gluTessProperty( tess, GLU.GLU_TESS_BOUNDARY_ONLY, GL.GL_TRUE );
        
  //       glu.gluTessCallback( tess, GLU.GLU_TESS_VERTEX, events );// glVertex3dv);
  //       glu.gluTessCallback( tess, GLU.GLU_TESS_BEGIN,  events );// beginCallback);
  //       glu.gluTessCallback( tess, GLU.GLU_TESS_END,    events );// endCallback);
  //       glu.gluTessCallback( tess, GLU.GLU_TESS_COMBINE,events );// endCallback);
        

  //       glu.gluTessBeginPolygon(tess, null);
  //       glu.gluTessBeginContour(tess);
        
  //       for( Vector2 v : offsetPoly2.points ) {
  //         double[] va = new double[]{ (double)v.x, (double)v.y, (double)0 };
  //         glu.gluTessVertex( tess, va, 0, v );
  //       }
        
  //       glu.gluTessEndContour(tess);
  //       glu.gluTessEndPolygon(tess);
        
  //     } catch( Exception e ) {
  //       System.out.println( "Tesselation Error." );
  //     }

  //     glu.gluDeleteTess(tess);
      
  //     return events.get();
      
  //   } else {
      
  //     out.add( offsetPoly2 );
  //     return out;
  //   }
  // }
  // -------------------------------------------------------------------------------- //
  // public List<Poly2> offset( GLU glu, float distance, int arcPoints ) {
  //   return offset( glu, new ArrayList<Poly2>(), distance, arcPoints );
  // }

  // -------------------------------------------------------------------------------- //
  public boolean contains( float x, float y ) { return contains( new Vector2(x,y) ); }
  public boolean contains( Vector2 point ) {

    // Create a "ray" at least until we have a real ray data structure
    Vector2  outside   = point.get();
             outside.x = -999999.0f;
    LineSeg2 ray       = new LineSeg2( point, outside );

    int        intersects = 0;
    List<Edge> edges      = edges();
    for ( Edge edge : edges ) {
      if ( edge.intersect( ray ) != null ) ++intersects;
    }
    
    if( (intersects % 2) == 1 || hasEqualVert( point ) ) return true;
    return false;
  }
  // -------------------------------------------------------------------------------- //
  public boolean containsVertsFrom( Poly2 other, ArrayList<Integer> contained ) {

    int     pointIndex   = 0;
    boolean thisContains = false;
    for ( Point2 point : other.points ) {
      if ( this.contains( point ) ) {
        thisContains = true;
        contained.add( pointIndex );
      }
      ++pointIndex;
    }

    return thisContains;
  }
  // -------------------------------------------------------------------------------- //
  public boolean hasEqualVert( Vector2 point ) {
    for( Point2 p : points )
      if( p.sub(point).lenlen() < 0.01f ) return true;
    return false;
  }
  // -------------------------------------------------------------------------------- //
  public int findFirstContainingEdge( Vector2 point ) {
    List<Edge> edges = edges(); // This polygon's edges
    int        index = 0;
    for ( Edge edge : edges ) {
      if ( edge.contains( point ) )
        return index;
      ++index;
    }
    return -1;
  }
  // -------------------------------------------------------------------------------- //
  public List<Integer> cuteq( Poly2 other ) {

    // Some local classes to make dealing with the generics somewhat clearer
    @SuppressWarnings("serial")
    class EdgeIntersectionMap extends TreeMap<Integer, PointList> {
      public void add( int edgeIndex, Point2 intersection ) {
        if ( containsKey( edgeIndex ) ) {
          PointList     intersections = get( edgeIndex );
          intersections.add( intersection );
        } 
        else {
          PointList     intersections = new PointList();
          intersections.add( intersection );
          put( edgeIndex, intersections );
        }
      }
    }

    class TimeComparator implements Comparator<Float> {
      public int compare(Float o1, Float o2) {
        if ( o1 < o2 ) return -1;
        if ( o1 > o2 ) return 1;
        return 0;
      }
    }

    List<Edge>           edges         = edges();               // This polygon's edges
    List<Edge>           otherEdges    = other.edges();         // The other polygon's edges
    EdgeIntersectionMap  intersections = new EdgeIntersectionMap();

    // Attempt to intersect each of our edges edge with every
    //  edge from the other polygon, and store the results into the hashmap
    //   assigning intersections points to the edge they lie on.
    int edgeIndex = 0;
    for ( Edge edge : edges ) {
      for ( Edge otherEdge : otherEdges ) {  
        Vector2 result = edge.intersect( otherEdge );
        // Make sure the result exists and is not equal to any of the vertices in our poly
        if ( result != null && !hasEqualVert(result) ) {  
          intersections.add( edgeIndex, new Point2(result) );
        }
      }
      ++edgeIndex;
    }
    
    // Create the storage for our new points
    PointList newPoints = new PointList();

    // Add the intersection points to the proper location inside the poly and
    //  update the intersection indices as required
    int                lastIndex        = 0;
    ArrayList<Integer> intersectIndices = new ArrayList<Integer>(); // The indices of our new intersection points
    for ( Entry<Integer,PointList> entry : intersections.entrySet() ) {

      int       index  = entry.getKey();
      PointList points = entry.getValue();
      LineSeg2  edge   = edge( index );

      // Calculate the time value for the point on the edge
      //  and insert them into the tree map so they will be sorted
      TreeMap<Float, Point2> timeSorted = new TreeMap<Float, Point2>( new TimeComparator() );
      for ( Point2 point : points ) {
        Float time = edge.time(point);
        if( time != null )
          timeSorted.put( time, point );
      }

      // Add any existing points up to the edge index (leading point index)
      for ( int i = lastIndex; i <= index && i < this.points.size(); ++i )
        newPoints.add( this.points.get(i) );

      // Update the last index
      lastIndex = index + 1;

      // Add all of the intersections for this polygon edge, sorted
      for ( Point2 point : timeSorted.values() ) {
        newPoints.add( point );
        intersectIndices.add( newPoints.size()-1 );
      }
    }

    // Finally add any remaining points
    for ( int i=lastIndex; i < this.points.size(); ++i )
      newPoints.add( this.points.get(i) );

    // Link up the points
    int count = newPoints.size();
    for ( int i=0; i<count; ++i ) {
      Point2 p0 = newPoints.get(i);
      Point2 p1 = newPoints.get((i+1)%count);
      p0.next = p1;
      p1.prev = p0;
    }

    // Assign the new points array
    points = newPoints;

    // Return the intersection point indices
    return intersectIndices;
  }
  // -------------------------------------------------------------------------------- //
  public void draw( PGraphics g ) {
    g.beginShape();
    for ( Vector2 p : points )
      g.vertex( p.x, p.y );
    g.endShape( PGraphics.CLOSE );
  }
  // -------------------------------------------------------------------------------- // 

  // -------------------------------------------------------------------------------- // 
  private void bindIntersections( PointList a, List<Integer> thisCuts, PointList b, List<Integer> otherCuts ) {
    for( Point2 p : a ) p.neighbor = null;
    for( Point2 p : b ) p.neighbor = null;
    
    for ( Integer index_a : thisCuts ) {
      Point2 pa = a.get( index_a );
      for ( Integer index_b : otherCuts ) {
        Point2 pb = b.get( index_b );
        if ( pb.sub(pa).lenlen() < 0.001f ) {
          pa.neighbor = pb;
          pb.neighbor = pa;
        }
      }
    }
  }
}
