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
package com.fieldfx.net;

import java.util.List;

public abstract class NetRule {
  protected float radrad = 10.0f;
  protected float factor = 1.0f;
  
  public void updateRelation( NetObject a, NetObject b, NetMesh parent ) {
    // If the squared length between the two objects is less than the squared radius of this relation
    //  then we want to update the relation so it continues to exist.
    if( a.pos.sub( b.pos ).lenlen() < radrad ) {
      parent.updateRelation(a,b);
    }
  }
  
  public abstract void apply( NetObject a, List<NetObject> relations );
}
