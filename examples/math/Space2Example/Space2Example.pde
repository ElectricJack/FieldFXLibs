/*
  Space2 Example
  Copyright (c) 2011, Jack W. Kern
  
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

import com.fieldfx.math.*;


Space2   testSpace  = new Space2();
Vector2  localMouse = new Vector2();

void setup() {
  size(800,600,P3D);
  testSpace.set( width/2, height/2 );
}

void draw() {
  background(0);
  
  // Rotate the space with time
  testSpace.setAng( frameCount*0.01 );

  // Change the position of the space if a mouse button is pressed  
  if( mousePressed )
    testSpace.set( mouseX, mouseY );
  
  localMouse.set( mouseX, mouseY );
  testSpace.transformIn( localMouse );
  
  testSpace.beginDraw(g);
    noStroke();
    fill(255);
    rect( localMouse.x, localMouse.y, 10, 10 );
    rect( 100, 100, 10, 10 );
  testSpace.endDraw(g);
  
  testSpace.transformOut( localMouse );
  
  noFill();
  stroke(255);
  ellipse( localMouse.x, localMouse.y, 50, 50 );
}

