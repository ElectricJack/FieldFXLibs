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
package com.fieldfx.lang;

import java.util.ArrayList;
import java.util.TreeMap;

import com.fieldfx.math.Vector2;
import com.fieldfx.math.Vector3;


public class VariableBank
{ 
  public static final int VARTYPE_INTEGER         =  1;
  public static final int VARTYPE_FLOAT           =  2;
  public static final int VARTYPE_VECTOR2         =  4;
  public static final int VARTYPE_VECTOR3         =  8;
  public static final int VARTYPE_BOOLEAN         =  16;
  public static final int VARTYPE_STRING          =  32;
  public static final int VARTYPE_VARIABLEREF     =  64;
  public static final int VARTYPE_EXPRESSION_FLAG = 128;
  
  private ArrayList<ExpressionElement>      indexToVars;
  private TreeMap<String,ExpressionElement> strToVars;

  public VariableBank() {
    indexToVars = new ArrayList<ExpressionElement>();
    strToVars   = new TreeMap<String,ExpressionElement>();
  }


  public void register( Vector2   var, String name )  { addVariable( name, VARTYPE_VECTOR2, var ); }
  public void register( Vector3   var, String name )  { addVariable( name, VARTYPE_VECTOR3, var ); }
  public void register( Float     var, String name )  { addVariable( name, VARTYPE_FLOAT,   var ); }
  public void register( Integer   var, String name )  { addVariable( name, VARTYPE_INTEGER, var ); }
  public void register( Boolean   var, String name )  { addVariable( name, VARTYPE_BOOLEAN, var ); }

  public ExpressionElement registerAndCreate( String identifier ) {
    //System.out.println(identifier);
    if( identifier == null   ) return null;
    if( exists( identifier ) ) return get( identifier );
  
    char c = identifier.charAt(0);
    int type = VARTYPE_FLOAT;
    if      ( identifier.contains("\"")  ) type = VARTYPE_STRING;
    else if ( identifier.contains(".")   ) type = VARTYPE_FLOAT;
    else if ( c == '<' ) {
      if( identifier.indexOf(',') == identifier.lastIndexOf(',') ) {
        type = VARTYPE_VECTOR2;
      } else {
        type = VARTYPE_VECTOR3;
      }
    }
    else if ( identifier.equals("true") || identifier.equals("false") ) {
      type = VARTYPE_BOOLEAN;
    }
    else if ( c >= '1' && c <= '9' ) {
      type = VARTYPE_INTEGER;
    }
    
    int index = registerAndCreate( identifier, type );
    
    return get( index );
  }
  
  public int registerAndCreate( String name, int type ) {
    if( exists(name) ) return -1;

    Object  val      = null;
    name = name.trim();

    // Check if this field can be an expression, and get the actual type index
    //boolean expression  = ( type & VARTYPE_EXPRESSION_FLAG ) > 0;
    type &= ( VARTYPE_EXPRESSION_FLAG - 1 );

    switch( type ) {
      case VARTYPE_INTEGER: {
          float value = 0.f;
          try { 
            value = Float.parseFloat( name );
          } catch( Exception e ) {

          }
          val = new Integer((int)value);
        }
        break;
      case VARTYPE_FLOAT: {
          float value = 0.f;
          try {
            value = Float.parseFloat( name );
          } catch( Exception e ) {

          }
          val = new Float(value);
        }
        break;
      case VARTYPE_STRING: {
          String value = "";
          if( name.contains("\"") ) {
            int begin = name.indexOf("\"");
            int end   = name.lastIndexOf("\"");
            if( begin+1 < end ) {
              value = name.substring( begin+1, end );
            }
          }
          val = new String( value );
        }
        break;

      case VARTYPE_VECTOR2: {
          Vector2 value = new Vector2();
          int c1 = name.indexOf(',');

          String valX = name.substring(1,c1).trim();
          String valY = name.substring(c1+1,name.length()-1).trim();

          float x = 0, y = 0;
          try { x = Float.parseFloat(valX); } catch(Exception e) {}
          try { y = Float.parseFloat(valY); } catch(Exception e) {}
          val = value.set(x,y);
        }
        break;
      case VARTYPE_VECTOR3: {
          Vector3 value = new Vector3();

          int c1 = name.indexOf(',');
          int c2 = name.lastIndexOf(',');

          String valX = name.substring(1,c1).trim();
          String valY = name.substring(c1+1,c2).trim();
          String valZ = name.substring(c2+1,name.length()-1).trim();

          float x = 0, y = 0, z = 0;
          try { x = Float.parseFloat(valX); } catch(Exception e) {}
          try { y = Float.parseFloat(valY); } catch(Exception e) {}
          try { z = Float.parseFloat(valZ); } catch(Exception e) {}

          val = value.set(x,y,z);
        }
        break;
      case VARTYPE_BOOLEAN: {
          if (name.equals("true") ) {
            val = new Boolean(true);
          } else {
            val = new Boolean(false);
          }
        }
        break;
    }
    
    addVariable( name, type, val );
    
    // Return the index of the variable
    return indexToVars.size() - 1;
  }

  public boolean exists ( String name ) {
    return strToVars.containsKey(name);
  }
  public int size( ) {
    return indexToVars.size();
  }
  
  public String typeToString( int type ) {
    String strType = "";
    if( (type & VARTYPE_INTEGER) > 0 ) strType += "int ";
    if( (type & VARTYPE_FLOAT)   > 0 ) strType += "float ";
    if( (type & VARTYPE_VECTOR2) > 0 ) strType += "vector2 ";
    if( (type & VARTYPE_VECTOR3) > 0 ) strType += "vector3 ";
    if( (type & VARTYPE_BOOLEAN) > 0 ) strType += "bool ";
    if( (type & VARTYPE_STRING)  > 0 ) strType += "string ";
    if( (type & VARTYPE_VARIABLEREF) > 0 ) {
      strType += "variable ";
    }
    if( (type & VARTYPE_EXPRESSION_FLAG) > 0 ) strType = "exp";
    return strType;
  }

  public int getVariableIndex( String name ) {
    ExpressionElement var = get( name );
    if( var != null ) return var.getIndex();
    return -1;
  }
  
  public ExpressionElement get( int index ) {
    if( index >= 0 && index < indexToVars.size() ) return (ExpressionElement)indexToVars.get(index);
    return null;    
  }

  public ExpressionElement get( String name ) {
    name = name.trim();
    if( strToVars.containsKey(name) ) return (ExpressionElement)strToVars.get(name);
    return null;    
  }

  public Variable getVariable( int index ) {
    ExpressionElement expr = get( index );
    
    if( expr != null && expr instanceof Variable )
      return (Variable)expr;

    return null;    
  }  
  
  public Variable getVariable( String name ) {
    ExpressionElement expr = get( name );
    
    if( expr != null && expr instanceof Variable )
      return (Variable)expr;

    return null;  
  }

  public void setVariable( String name,  int     value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); }  
  public void setVariable( String name,  float   value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); } 
  public void setVariable( String name,  boolean value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); }  
  public void setVariable( String name,  String  value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); }  
  public void setVariable( String name,  Vector2 value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); }  
  public void setVariable( String name,  Vector3 value )  { Variable var = getVariable(name ); if( var == null ) return; var.set(value); }  

  public void setVariable( int    index, int     value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); }
  public void setVariable( int    index, float   value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); }
  public void setVariable( int    index, boolean value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); } 
  public void setVariable( int    index, String  value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); } 
  public void setVariable( int    index, Vector2 value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); }  
  public void setVariable( int    index, Vector3 value )  { Variable var = getVariable(index); if( var == null ) return; var.set(value); }  

  protected int addExpression( Expression expr ) {
    int index = indexToVars.size();
    
    expr.setIndex( index );
    indexToVars.add( expr );
    
    return index;
  }
  
  private boolean addVariable( String name, int type, Object value ) {
    if( exists(name) ) 
      return false;
      
    int index = indexToVars.size();

    Variable var = new Variable( name, index, type, value );    
    indexToVars.add( var );
    strToVars.put( name, var );
    
    return true;
  }
  

}
