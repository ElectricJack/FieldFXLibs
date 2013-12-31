 /*
  Serialization Example
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
 
import com.fieldfx.serialize.*;
 
 int type = 1;

// ------------------------------------------------------------ // 
void setup() {
  
  // First create the serializer object
  Serializer store = null;
  
  if      ( type == 1 ) store = new XMLSerializer( this );
  else if ( type == 2 ) store = new BinarySerializer();
  
  // Next register your serializable types so they
  //  can be instantiated on load. (Only required for list-serialization support)
  store.registerType( new Data() );
  store.registerType( new PVectorData() );
  
  // Finally let's test saving out some data, and loading it back in.
  Object obj = testSaving( store );
  println("------------------------------------------------------------");
  testLoading( obj, store );

  // Fin.
  exit();  
}

// ------------------------------------------------------------ // 
void testLoading( Object obj, Serializer s ) {
  // First we create our data object, then use the serializer to load it, and finally
  //  we print it to the debug console to verify success!
  Data data = new Data();
  
  if( type == 1 ) {
    XMLSerializer store = (XMLSerializer)s;
                  store.load( "data.xml", data );
  }
  else if( type == 2 ) {
    
    byte[]           bytes = (byte[])obj;
    BinarySerializer store = (BinarySerializer)s;
                     store.load( bytes, data );
  }
  
  data.print();
}

// ------------------------------------------------------------ // 
Object testSaving( Serializer s ) {
  // Notice it is possible to easily save a tree structure because "Data" types can
  //  store "Data" objects as thier own children.
  Data  data =             new Data( 1, 2.0f, "root",    false, 10.0f,  20.0f, 30.0f );
        data.children.add( new Data( 2, 1.0f, "child 1", true,   1.2f,   3.4f,  5.6f ) );
        data.children.add( new Data( 3, 1.1f, "child 2", false, 40.0f, -10.1f, 62.0f ) );
        data.print();

  if( type == 1 ) {
    XMLSerializer store = (XMLSerializer)s;
                  store.save( "data.xml", data );
  }                
  else if( type == 2 ) {
    BinarySerializer store = (BinarySerializer)s;
    byte[]           bytes = store.save( data );
    return           bytes;
  }

  return null;
}


