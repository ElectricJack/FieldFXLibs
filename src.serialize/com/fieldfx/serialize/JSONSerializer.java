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
package com.fieldfx.serialize;

import java.util.HashMap;
import java.util.List;


public class JSONSerializer {
  private HashMap<String, Serializable> types = new HashMap<String, Serializable>();
  
  // ------------------------------------------------------------ //
  public void load ( String fileName, Serializable object ) {
    
  }
  // ------------------------------------------------------------ //
  public void save ( String fileName, Serializable object ) {
    
  }
  
  // ------------------------------------------------------------ //  
  public void registerType ( Serializable type ) {
    //types.put( type.getType(), type );
  }
  
  // ------------------------------------------------------------ //
  public float serialize ( String name, float value ) {
    return serialize( name, value, 0.f );
  }
  
  public float serialize ( String name, float value, float defaultValue ) {
    if( isLoading() ) {

    } else {

    }
    return value;
  }
  
  // ------------------------------------------------------------ //
  public int serialize ( String name, int value ) {
    return serialize( name, value, 0 );
  }
  
  public int serialize ( String name, int value, int defaultValue ) {
    return value;
  }
  
  // ------------------------------------------------------------ //
  public boolean serialize ( String name, boolean value ) {
    return value;
  }
  
  // ------------------------------------------------------------ //
  public String serialize ( String name, String value ) {
    return value;
  }
  

  
  // ------------------------------------------------------------ //
  public void serialize ( String name, Serializable object ) {
    serialize( name, object, false );
  }
  
  protected void serialize ( String name, Serializable object, boolean root ) {
    if( isLoading() ) {

    } else {

    }
  }
  
  // ------------------------------------------------------------ //
  public <T extends Serializable> void serialize ( String name, List<T> values ) {
    if( isLoading() ) {

    } else {

    }
  }
  
  // ------------------------------------------------------------ //
  public boolean isLoading () {
    return false;
  }
}
