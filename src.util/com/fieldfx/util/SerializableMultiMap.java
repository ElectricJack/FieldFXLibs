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
  
package com.fieldfx.util;

import java.util.ArrayList;
import java.util.List;

import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;

public class SerializableMultiMap<T extends NamedSerializable> extends NamedMultiMap<T> implements Serializable {
  
  // ------------------------------------------------------------------------------------------------------------- //
  @Override
  public Serializable clone() {
    return new SerializableMultiMap<T>();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  @Override
  public void serialize( Serializer s ) {
    // First serialize all the objects by index
    s.serialize("indexToValue", indexToValue );
    
    // Next serialize the name to object list map
    if( s.isLoading() ) {
      // Need to clear the map, so we can load
      //  the new values
      nameToValue.clear();
      for( T value : indexToValue ) {
        addToMap(value);
      }
    }
  }
  
  
  //------------------------------------------------------------------------------------------------------------- //
  @Override
  public String getType() {
    return "SerializableMultiMap";
  }
  
}
