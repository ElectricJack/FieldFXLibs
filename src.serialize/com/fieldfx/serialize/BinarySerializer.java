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


public class BinarySerializer implements Serializer {
  class BinaryWriter {
    List<Byte> bytes = new ArrayList<Byte>();
    
    byte[] get() {
      byte[] out = new byte[ bytes.size() ];
      int    i   = 0;
      for( Byte b : bytes ) {
        out[i++] = b;
      }
      return out;
    }
    
    void write( byte b ) {
      bytes.add( b );
    }
    void write( int v ) {
      write( (byte)( v        & 0xFF) );
      write( (byte)((v >>  8) & 0xFF) );
      write( (byte)((v >> 16) & 0xFF) );
      write( (byte)((v >> 24) & 0xFF) );
    }
    void write( float v ) {
      int    val = Float.floatToIntBits( v );
      write( val );
    }
    void write( String v ) {
      byte[] stringBytes = v.getBytes();
      write( (int)(stringBytes.length) );
      for( byte b : stringBytes )
        bytes.add( b );
    }
    void write( boolean v ) {
      write( (byte)( v? 1 : 0 ) );
    }
  }
  class BinaryReader {
    int    at = 0;
    byte[] data;
    
    BinaryReader( byte[] data ) {
      this.data = data;
    }
    byte getByte() {
      return data[at++]; // CRASH HARD IF BROKEN
    }
    float getFloat() {
      return Float.intBitsToFloat( getInt() );
    }
    boolean getBool() {
      byte b =  getByte();
      return b == 1 ? true : false;
    }
    String getString() {
      int len = getInt();
      String out = "";
      for( int i=0; i < len; ++i ) {
        out += (char)getByte();
      }
      return out;
    }
    int getInt() {
      int    out  = 0;
             out |= ((int)getByte() + 256) % 256;
             out |= ((int)getByte() + 256) % 256 << 8;
             out |= ((int)getByte() + 256) % 256 << 16;
             out |= ((int)getByte() + 256) % 256 << 24;
      return out;
    }
  }
  
  private Byte                          curID      = 0;
  private HashMap<String, Byte>         typeToID   = new HashMap<String, Byte>();
  private HashMap<Byte,   Serializable> idToObj    = new HashMap<Byte,   Serializable>();
  private HashMap<String, Serializable> typeToObj  = new HashMap<String, Serializable>();
  private BinaryWriter                  binWriter  = null;
  private BinaryReader                  binReader  = null;
  
  // ------------------------------------------------------------ //  
  public void registerType ( Serializable type ) {
    System.out.println( "Registering " + type.getType() + " with id " + curID );
    ++curID;
    typeToID.put  ( type.getType(), curID );
    idToObj.put   ( curID,          type  );
    typeToObj.put ( type.getType(), type  );
  }
  
  
  // ------------------------------------------------------------ //
  public void load( byte[] data, Serializable object ) {
    binReader = new BinaryReader( data );
    serialize( "root", object, true );
    binReader = null;
  }
  // ------------------------------------------------------------ //
  public byte[] save( Serializable object ) {
    binWriter = new BinaryWriter();
    serialize( "root", object, true );
    byte[] bytes = binWriter.get();
    //System.out.println( "Bytes: " + bytes.length );
    binWriter = null;
    return bytes;
  }
  
  // ------------------------------------------------------------ //
  public boolean isLoading () {
    return binWriter == null;
  }
  
  // ------------------------------------------------------------ //
  public Serializable serialize ( String name, Serializable object ) {
    return serialize( name, object, false );
  }
  public Serializable serialize ( String name, Serializable object, boolean root ) {
    object.serialize( this );
    return object;
  }

  // ------------------------------------------------------------ //
  public float serialize ( String name, float value ) {
    return serialize( name, value, 0.f );
  }
  public float serialize ( String name, float value, float defaultValue ) {
    if( isLoading() ) value = binReader.getFloat( );
    else              binWriter.write( value );
    return            value;
  }
  
  // ------------------------------------------------------------ //
  public int serialize ( String name, int value ) {
    return serialize( name, value, 0 );
  }
  public int serialize ( String name, int value, int defaultValue ) {
    if( isLoading() ) value = binReader.getInt();
    else              binWriter.write( value );
    return            value;
  }
  
  // ------------------------------------------------------------ //
  public byte serialize ( String name, byte value ) {
    return serialize( name, value, 0 );
  }
  public byte serialize ( String name, byte value, int defaultValue ) {
    if( isLoading() ) value = binReader.getByte();
    else              binWriter.write( value );
    return            value;
  }
  
  // ------------------------------------------------------------ //
  public boolean serialize ( String name, boolean value ) {
    return serialize( name, value, false );
  }
  public boolean serialize ( String name, boolean value, boolean defaultValue ) {
    if( isLoading() ) value = binReader.getBool();
    else              binWriter.write( value );
    return value;
  }
  // ------------------------------------------------------------ //
  public String serialize ( String name, String value ) {
    return serialize( name, value, "" );
  }
  public String serialize ( String name, String value, String defaultValue ) {
    if( isLoading() ) value = binReader.getString();
    else              binWriter.write( value );
    return value;
  }
  
  // ------------------------------------------------------------ //
  public Serializable cloneByType( String name ) {
    Serializable   template = typeToObj.get( name );
    return template.clone();
  }
  
  // ------------------------------------------------------------ //
  public Serializable cloneByID( int typeId ) {
    Serializable   template = idToObj.get( (byte)typeId );
    return template.clone();
  }

  // ------------------------------------------------------------ //
  @SuppressWarnings("unchecked")
  public <T extends Serializable> void serialize ( String name, List<T> values ) {
    if( isLoading() ) {
      int count = binReader.getInt();
      for( int i=0; i<count; ++i ) {
        int typeId  = binReader.getInt();
        //System.out.println( idToObj );
        // System.out.println( typeId );
        T              obj = (T)cloneByID( typeId );
        values.add   ( obj );
                       obj.serialize( this );
      }
    } else {
      int count = values.size();
      binWriter.write( count );
      for( T val : values ) {
        int              typeId = typeToID.get( val.getType() );
        binWriter.write( typeId );
        val.serialize( this );
      }
    }
  }


  public byte getTypeIdFromType(String type) {
    return typeToID.get( type );
  }
    

}
