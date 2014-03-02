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

import processing.core.PApplet;
import processing.data.XML;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;


public class XMLSerializer implements Serializer {
  
  private PApplet                       parent    = null;
  private XML                           xmlReader = null;
  private PrintWriter                   xmlWriter = null;
  private int                           depth     = 0;
  private HashMap<String, Serializable> types     = new HashMap<String, Serializable>();
  
  // ------------------------------------------------------------ //
  public XMLSerializer( PApplet parent ) {
    this.parent = parent;
  }
  
  // ------------------------------------------------------------ //
  public boolean load ( String fileName, Serializable object ) {
    //xmlReader = new XML( parent, fileName );
    try {
      xmlReader = parent.loadXML(fileName);
      if( xmlReader != null ) {
        serialize( "root", object, true );
      }
    } catch(Exception e) {
      return false;
    }

    xmlReader = null;    
    return true;
  }
  
  // ------------------------------------------------------------ //
  public boolean save ( String fileName, Serializable object ) {
    try {
      xmlWriter = parent.createWriter( fileName );
      if( xmlWriter != null ) {
        serialize( "root", object );
        xmlWriter.flush();
        xmlWriter.close();
        xmlWriter = null;
      }
    } catch( Exception e ) {
      return false;
    }
    return true;
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
    if( isLoading() ) {
      XML child = getChild( "float", name );
      if( child != null ) value = child.getFloat( "value", defaultValue );
    } else {
      xmlWriter.println( getXmlTag( "float", name, "" + value ) );
    }
    return value;
  }
  
  // ------------------------------------------------------------ //
  public int serialize ( String name, int value ) {
    return serialize( name, value, 0 );
  }
  public int serialize ( String name, int value, int defaultValue ) {
    if( isLoading() ) {
      XML child = getChild( "int", name );
      if( child != null ) value = child.getInt( "value", defaultValue );
    } else {
      xmlWriter.println( getXmlTag( "int", name, "" + value ) );
    }
    return value;
  }
  
  public byte serialize ( String name, byte value ) {
    return (byte)serialize( name, (byte)value, 0 );
  }
	  
  
  // ------------------------------------------------------------ //
  public boolean serialize ( String name, boolean value ) {
    if( isLoading() ) {
      
      XML child = getChild( "boolean", name );
      if( child != null ) {
        String _value = child.getString( "value" );
        _value = _value.trim();
        _value = _value.toLowerCase();
        
        if( _value != null && _value.equals( "true" ) ) value = true;
        else value = false;
      }
    } else {
      xmlWriter.println( getXmlTag( "boolean", name, value ? "true" : "false" ) );
    }
    return value;
  }
  
  // ------------------------------------------------------------ //
  public String serialize ( String name, String value ) {
    if( isLoading() ) {
      
      XML child = getChild( "string", name );
      if( child != null ) value = child.getString( "value" );
    } else {
      xmlWriter.println( getXmlTag( "string", name, value ) );
    }
    return value;
  }
  
  // ------------------------------------------------------------ //
  // public Vector2 serialize( String name, Vector2 value ) {
  // if( isLoading() ) {
  // XML child  = getChild( "Vector2", name );
  // if       ( child != null ) {
  // String   _value  =  child.getString( "value" );
  // _value  = _value.substring( 1, _value.length()-1 );
  // String[] _values = _value.split(",");
  
  // value.set( Float.parseFloat( _values[0].trim() )
  // , Float.parseFloat( _values[1].trim() ) );
  // }
  // } else {
  // xmlWriter.println( getXmlTag( "Vector2", name, value.toString() ) );
  // }
  // return value;
  // }
  
  // ------------------------------------------------------------ //
  public Serializable serialize ( String name, Serializable object ) {
    return serialize( name, object, false );
  }
  
  protected Serializable serialize ( String name, Serializable object, boolean root ) {
    
    if( isLoading() ) {
      // First if this is the root node, then xmlReader is already set to the correct node, but let's just 
      //  do some sanity checking anyways to make sure everything goes smoothly.
      boolean actuallyRoot = root && xmlReader != null && xmlReader.getName().equals( "root" );
      XML child = null;
      if( actuallyRoot  ) {
        child = xmlReader; 
      } else if( object != null ) {
        child = getChild( object.getType(), name );
      } else {
        child = xmlReader.getChild( name );
      }
      
      // Now we should have the correct child element, but let's make sure something didn't go wrong.
      if( child != null ) {
        
        // Now we need to store the active node, and set it to the child node we found
        XML parent = xmlReader;
        xmlReader = child;
        
        String typeName = xmlReader.getString("type");
        if( object == null ) {
          Serializable template = types.get( typeName );
          object = template.clone();
        }

        // Let the object Serialize itself
        object.serialize( this );
        
        // Finally we need to set back the active node so we can continue
        xmlReader = parent;
      }
    } else {
      if( object != null ) {
        xmlWriter.println( getXmlBeginTag( object.getType(), name ) );
        
        // Serialize the type so it can be instantiated later if need be
        //this.serialize("typeName",object.getType());

        // Let the object Serialize itself
        object.serialize( this );
        
        xmlWriter.println( getXmlEndTag( name ) );
      }
    }

    return object;
  }
  
  // ------------------------------------------------------------ //
  public <T extends Serializable> void serialize ( String name, List<T> values ) {
    if( isLoading() ) {
      values.clear();
      XML child = getChild( "ArrayList", name );
      if( child != null ) {
        // Now we need to store the active node, and set it to the child node we found
        XML parent = xmlReader;
        xmlReader = child;
        
        for( XML listChild : child.getChildren() ) {

          if( listChild.getName().equals("#text") ) continue;

          String typeName = listChild.getString( "type" );
          Serializable template = types.get( typeName );
          T object = (T)template.clone();

          serialize( listChild.getName(), object );
          values.add( object );
        }
        
        // Finally we need to set back the active node so we can continue
        xmlReader = parent;
      }
    } else {
      xmlWriter.println( getXmlBeginTag( "ArrayList", name ) );
      int index = 0;
      for( Serializable object : values )
        serialize( "child" + ( index++ ), object );
      xmlWriter.println( getXmlEndTag( name ) );
    }
  }
  
  // ------------------------------------------------------------ //
  public boolean isLoading () {
    return xmlReader != null;
  }
  
  // ------------------------------------------------------------ //
  private XML getChild ( String type, String name ) {
    // We must have an active node to read from
    if( xmlReader == null ) return null;
    
    // First try to get the child by name, and bail if we can't find it.
    XML child = xmlReader.getChild( name );
    if( child == null ) return null;
    
    // Next check the type to make sure it's what we expect, if it 'aint, then bail.
    String childType = child.getString( "type" );
    if( !childType.equals( type ) ) return null;
    
    // Finally, everything looks good so return our element
    return child;
  }
  
  // ------------------------------------------------------------ //
  private String getXmlTag ( String type, String name, String value ) {
    return getIndent() + "<" + name + " type=\"" + type + "\" value=\"" + value + "\" />";
  }
  
  private String getXmlBeginTag ( String type, String name ) {
    String beginTag = getIndent() + "<" + name + " type=\"" + type + "\">";
    ++depth;
    return beginTag;
  }
  
  private String getXmlEndTag ( String name ) {
    --depth;
    return getIndent() + "</" + name + ">";
  }
  
  private String getIndent () {
    String out = "";
    for( int indent = 0; indent < depth; ++indent )
      out += "  ";
    return out;
  }
}
