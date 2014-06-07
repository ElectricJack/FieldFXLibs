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
import java.util.ArrayList;

import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PApplet;

public class JSONSerializer implements Serializer {
  private HashMap<String, Serializable>  types       = new HashMap<String, Serializable>();
  private PApplet                        parent      = null;
  private JSONObject                     root        = null;
  private List<JSONObject>               parentStack = new ArrayList<JSONObject>();
  private boolean                        loading     = false;

  // ------------------------------------------------------------ //
  public JSONSerializer(PApplet parent) {
    this.parent = parent;
  }
  // ------------------------------------------------------------ //
  protected void pushParent(JSONObject parent) {
    parentStack.add( parent );
  }
  // ------------------------------------------------------------ //
  protected void popParent() {
    parentStack.remove( parentStack.size() - 1 );
  }
  // ------------------------------------------------------------ //
  protected JSONObject getActiveParent() {
    if( parentStack.size() > 0 ) {
      return parentStack.get( parentStack.size() - 1 );  
    }
    return null;
  }
  // ------------------------------------------------------------ //
  public boolean isLoading() {
    return loading;
  }
  // ------------------------------------------------------------ //
  public void load ( String filePath, Serializable object ) {
    root = parent.loadJSONObject( filePath );
    pushParent(root);
    load( object );
    parentStack.clear();
  }
  // ------------------------------------------------------------ //
  public void loadString( String data, Serializable object ) {
    loadJSONObject(JSONObject.parse( data ), object);
  }
  // ------------------------------------------------------------ //
  public void loadJSONObject( JSONObject jsonObject, Serializable object ) {
    root = jsonObject;
    pushParent(root);
    load( object );
    parentStack.clear();
  }
  // ------------------------------------------------------------ //
  private void load( Serializable object ) {
    loading = true;
    serialize( "root", object, true );
  }
  // ------------------------------------------------------------ //
  public void save ( String filePath, Serializable object ) {
    root = new JSONObject();
    pushParent(root);
    save( object );
    parentStack.clear();
    parent.saveJSONObject( root, filePath );
  }
  // ------------------------------------------------------------ //
  public String saveString( Serializable object ) {
    return saveJSONObject(object).toString();
  }
  // ------------------------------------------------------------ //
  public JSONObject saveJSONObject( Serializable object ) {
    root = new JSONObject();
    pushParent(root);
    save( object );
    parentStack.clear();
    return root;
  }
  // ------------------------------------------------------------ //
  private void save( Serializable object ) {
    loading = false;
    serialize( "root", object, true );
  }
  
  // ------------------------------------------------------------ //  
  public void registerType ( Serializable type ) {
    types.put( type.getType(), type );
  }
  
  // ------------------------------------------------------------ //
  public float serialize ( String name, float value ) {
    return serialize( name, value, 0.f );
  }
  public float serialize ( String name, float value, float defaultValue ) {
    //System.out.println("serialize float " + name);
    if( isLoading() ) {
      // Get the child object with the same name and type
      Object child = getChild( name );
      if( child != null ) {
        if( child instanceof Float ) {
          return (Float)child;
        } else if( child instanceof Double ) {
          return ((Double)child).floatValue();
        } else if( child instanceof Integer ) {
          return ((Integer)child).floatValue();
        }
      }
    } else {
      getActiveParent().setFloat( name, value );
      return value;
    }
    return defaultValue;
  }
  
  // ------------------------------------------------------------ //
  public int serialize ( String name, int value ) {
    return serialize( name, value, 0 );
  }
  public int serialize ( String name, int value, int defaultValue ) {
    //System.out.println("serialize int " + name);
    if( isLoading() ) {
      // Get the child object with the same name and type
      Object child = getChild( name );
      if( child != null && child instanceof Integer ) {
        return (Integer)child;
      }
    } else {
      getActiveParent().setInt( name, value );
      return value;
    }
    return defaultValue;
  }
  
  // ------------------------------------------------------------ //
  public boolean serialize ( String name, boolean value ) {
    return serialize(name, value, false);
  }
  public boolean serialize ( String name, boolean value, boolean defaultValue ) {
    //System.out.println("serialize bool " + name);
    if( isLoading() ) {
      // Get the child object with the same name and type
      Object child = getChild( name );
      if( child != null && child instanceof Boolean ) {
        return (Boolean)child;
      }
    } else {
      getActiveParent().setBoolean( name, value );
      return value;
    }
    return defaultValue;
  }

  // ------------------------------------------------------------ //
  public String serialize ( String name, String value ) {
    return serialize(name, value, "");
  }
  public String serialize ( String name, String value, String defaultValue ) {
    //System.out.println("serialize value " + name);
    if( isLoading() ) {
      // Get the child object with the same name and type
      Object child = getChild( name );
      if( child != null && child instanceof String ) {
        return (String)child;
      }
    } else {
      getActiveParent().setString( name, value );
      return value;
    }
    return defaultValue;
  }

  // ------------------------------------------------------------ //
  public byte serialize ( String name, byte value ) {
    return (byte)serialize( name, (byte)value, 0 );
  }

  // ------------------------------------------------------------ //
  public Serializable serialize ( String name, Serializable object ) {
    return serialize( name, object, false );
  }
  protected Serializable serialize ( String name, Serializable object, boolean root ) {
    //System.out.println("serialize Object " + name);
    if( isLoading() ) {
      try {
        // Get the child object with the same name and type
        Object child = getChild( name );
        if( child != null && child instanceof JSONObject ) {
          pushParent((JSONObject)child);
            String typeName = serialize("type", "");
            if( object == null ) {
              Serializable template = types.get( typeName );
              object = template.clone();
            }
            object.serialize(this);
          popParent();
        }
      } catch(Exception e) {
        e.printStackTrace();
      }
    } else {
      // Create a new child container
      // and serialize the object into the new container
      JSONObject child = new JSONObject();
      pushParent(child);
        serialize("type", object.getType());
        object.serialize(this);
      popParent();

      // Add the new child container to the parent
      JSONObject parent = getActiveParent();
      parent.setJSONObject( name, child );
    }

    return object;
  }
  
  // ------------------------------------------------------------ //
  public <T extends Serializable> void serialize ( String name, List<T> values ) {
    //System.out.println("serialize List " + name);
    // @TODO - Saving and loading arrays...
    if( isLoading() ) {

      Object child = getChild( name );
      if( child != null && child instanceof JSONArray ) {
        values.clear();
        JSONArray childArray = (JSONArray)child;
        for( int i=0; i<childArray.size(); ++i ) {
          JSONObject   childObj  = childArray.getJSONObject(i);
          String       indexName = Integer.toString(i);
          String       typeName  = childObj.getString("type");
          Serializable template  = types.get( typeName );
          T            object    = (T)template.clone();

          pushParent(childObj);
            serialize( indexName, object );
          popParent();

          values.add( object );
        }
      }
    } else {
      JSONArray childArray  = new JSONArray();
      int       index       = 0;
      for(T value : values) {
        JSONObject child     = new JSONObject();
        String     indexName = Integer.toString(index);

        child.setString("type", value.getType());
        pushParent(child);
          serialize(indexName, value);
        popParent();

        childArray.setJSONObject(index, child);
        ++index;
      }

      JSONObject parent = getActiveParent();
      parent.setJSONArray( name, childArray );
    }
  }

  // ------------------------------------------------------------ //
  private Object getChild ( String name ) {
    JSONObject parent = getActiveParent();
    if( parent != null && parent.keys().contains( name ) ) {
      return parent.remove(name);
    }
    return null;
  }

}
