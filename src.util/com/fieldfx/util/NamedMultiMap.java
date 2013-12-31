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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class NamedMultiMap<T extends Nameable> extends AbstractCollection<T> {
  protected List<T>                  indexToValue = new ArrayList<T>();
  protected HashMap<String, List<T>> nameToValue  = new HashMap<String, List<T>>();

  // ------------------------------------------------------------------------------------------------------------- //  
  public int size() { return indexToValue.size(); }

  // ------------------------------------------------------------------------------------------------------------- //
  public List<T> getAll() {
    return indexToValue;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //  
  public boolean add( T value ) {
    if( nameToValue.containsKey( value.getName() ) ) {
      nameToValue.get( value.getName() ).add( value );
    } else {
      ArrayList<T> values = new ArrayList<T>();
                   values.add( value );
      nameToValue.put( value.getName(), values );
    }
    indexToValue.add( value );
    return true;
  }

  // ------------------------------------------------------------------------------------------------------------- //
  public void clear() {
    indexToValue.clear();
    nameToValue.clear();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //  
  public ArrayList<T> get( String name ) {
    if( nameToValue.containsKey( name ) ) {
      return (ArrayList<T>)nameToValue.get( name );
    }
    return null;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public T get( int index ) {
    if( index < 0 || index >= indexToValue.size() ) return null;
    return indexToValue.get( index );
  }

  // ------------------------------------------------------------------------------------------------------------- //
  @Override
  public Iterator<T> iterator() {
    return indexToValue.iterator();
  }
}
