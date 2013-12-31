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
package com.fieldfx.mesh.collada;

import processing.data.XML;

public class COLLADAHelpers {

  //------------------------------------------------------------------------ //
  static boolean  isNameOf        ( XML elm, String name )                 { return elm.getName().equals(name);  }
  static boolean  isAttribOf      ( XML elm, String attrib, String value ) { return elm.getString(attrib).equals(value); }
  static boolean  isAttribOf      ( XML elm, String attrib, int    value ) { return elm.getInt(attrib) == value; }
  static String[] getContentArray ( XML elm )                              { return elm.getContent().split(" "); }
  //------------------------------------------------------------------------ //
  static int[] getIntArray ( XML elm ) {
   String[] values = getContentArray(elm);
   int[]    ints   = new int[values.length];
   for( int i=0; i<values.length; ++i )
     ints[i] = Integer.parseInt( values[i] );
   return ints;
  }
  //------------------------------------------------------------------------ //
  static float[] getFloatArray ( XML elm ) {
   String[] values = getContentArray(elm);
   float[]  floats = new float[values.length];
   for( int i=0; i<values.length; ++i )
     floats[i] = Float.parseFloat( values[i] );
   return floats;
  }
}
