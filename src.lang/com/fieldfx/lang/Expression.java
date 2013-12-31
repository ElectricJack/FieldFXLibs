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

import com.fieldfx.math.Vector2;
import com.fieldfx.math.Vector3;

//import scriptcam.lang.ExpressionElement;

public class Expression extends ExpressionElement
{
  public ExpressionElement  left;
  public ExpressionElement  right;
  public String             operator;

  public boolean asBool() {
    if      ( operator.equals(">")  ) {
      if      ( left.isFloat() || right.isFloat() ) return left.asFloat() >  right.asFloat();
      else if ( left.isInt()   && right.isInt()   ) return left.asInt()   >  right.asInt();
      //else if ( left.isFloat() && right.isInt()   ) return left.isFloat() >  right.asInt();
      //else if ( left.isInt()   && right.isFloat() ) return left.asInt()   >  right.isFloat();
    }
    else if ( operator.equals("<")  ) {
      if      ( left.isFloat() || right.isFloat() ) return left.asFloat() <  right.asFloat();
      else if ( left.isInt()   && right.isInt()   ) return left.asInt()   <  right.asInt();
    }
    else if ( operator.equals(">=") ) {
      if      ( left.isFloat() || right.isFloat() ) return left.asFloat() >= right.asFloat();
      else if ( left.isInt()   && right.isInt()   ) return left.asInt()   >= right.asInt();
    }
    else if ( operator.equals("<=") ) {
      if      ( left.isFloat() || right.isFloat() ) return left.asFloat() <= right.asFloat();
      else if ( left.isInt()   && right.isInt()   ) return left.asInt()   <= right.asInt();
    }
    else if ( operator.equals("==") ) {
      if      ( left.isFloat()  || right.isFloat()  ) return (float)Math.abs( left.asFloat() - right.asFloat() ) < 0.001f;
      else if ( left.isInt()    && right.isInt()    ) return left.asInt()  == right.asInt();
      else if ( left.isBool()   && right.isBool()   ) return left.asBool() == right.asBool();
      else if ( left.isString() && right.isString() ) return left.asString().equals( right.asString() );
    }
    else if ( operator.equals("!=") ) {
      if      ( left.isFloat()  || right.isFloat()  ) return (float)Math.abs( left.asFloat() - right.asFloat() ) >= 0.001f;
      else if ( left.isInt()    && right.isInt()    ) return left.asInt()  != right.asInt();
      else if ( left.isBool()   && right.isBool()   ) return left.asBool() != right.asBool();
      else if ( left.isString() && right.isString() ) return !left.asString().equals( right.asString() );
    }
    else if ( operator.equals("&&") ) {
      if ( left.isBool()  && right.isBool() ) return left.asBool() && right.asBool();
    }
    else if ( operator.equals("||") ) {
      if ( left.isBool()  && right.isBool() ) return left.asBool() || right.asBool();
    }

    return false;
  }

  public int asInt() {
    try {
      if      ( operator.equals("+") ) return left.asInt() + right.asInt();
      else if ( operator.equals("-") ) return left.asInt() - right.asInt();
      else if ( operator.equals("*") ) return left.asInt() * right.asInt();
      else if ( operator.equals("/") ) return left.asInt() / right.asInt();
      else if ( operator.equals("%") ) return left.asInt() % right.asInt();
      else if ( operator.equals("^") ) return (int)Math.pow( left.asInt(), right.asInt() );
    } catch( Exception e ) {
      //e.printStackTrace();
      System.out.println("bad int op" + operator);
    }
    
    return 0;
  }
  
  public float asFloat() {
    try {
      if      ( operator.equals("+") ) return left.asFloat() + right.asFloat();
      else if ( operator.equals("-") ) return left.asFloat() - right.asFloat();
      else if ( operator.equals("*") ) return left.asFloat() * right.asFloat();
      else if ( operator.equals("/") ) return left.asFloat() / right.asFloat();
      else if ( operator.equals("%") ) return left.asFloat() % right.asFloat();
      else if ( operator.equals("^") ) return (float)Math.pow( left.asFloat(), right.asFloat() );
      /*else if ( operator.equals("#") ) {
        if( left.isVector3() && right.isVector3() ) {
          return left.asVector3().dot( right.asVector3() );
        } else if( left.isVector2() && right.isVector2() ) {
          return left.asVector2().dot( right.asVector2() );
        }
      } */     
    } catch( Exception e ) {
      System.out.println("bad float op" + operator);
    }

    return 0.f;
  }
  
  public String asString() {
    if ( operator.equals("+") ) {
      return left.asString() + right.asString();  
    }
    return "";
  }
  
  public Vector2 asVector2() {
    return null; //@TODO convert expression to vector2
  }
  public Vector3 asVector3() {
    if      ( operator.equals("+") ) return left.asVector3().add( right.asVector3() );
    else if ( operator.equals("-") ) return left.asVector3().sub( right.asVector3() );
    else if ( operator.equals("*") ) {
      if( left.isVector3() ) {
        return left.asVector3().mul( right.asFloat() );
      }
    }
    else if ( operator.equals("/") ) {
      //System.out.println("l: "+left.getType()+" r: "+right.asFloat());
      if( left.isVector3() ) {
        return left.asVector3().div( right.asFloat() );
      }
    }
    
    //else if ( operator.equals("%") ) return left.asVector3() % right.asVector3();
    //else if ( operator.equals("^") ) return (float)Math.pow( left.asVector3(), right.asVector3() );

    return new Vector3();
  }

  public boolean isFloat() {
    if( isString() ) return false;
    if( isBool()   ) return false;

    if      ( operator.equals("+") ) return left.isFloat() || right.isFloat();
    else if ( operator.equals("-") ) return left.isFloat() || right.isFloat();
    else if ( operator.equals("*") ) return left.isFloat() || right.isFloat();
    else if ( operator.equals("/") ) return left.isFloat() || right.isFloat();
    else if ( operator.equals("%") ) return left.isFloat() || right.isFloat();
    else if ( operator.equals("^") ) return left.isFloat() || right.isFloat();
    //else if ( operator.equals("#") ) return (left.isVector2() && right.isVector2()) || (left.isVector3() && right.isVector3());

    return false;
  }
  public boolean isInt() {
    if( isString() ) return false;
    if( isBool()   ) return false;
    if( isFloat()  ) return false;

    if      ( operator.equals("+") ) return true;
    else if ( operator.equals("-") ) return true;
    else if ( operator.equals("*") ) return true;
    else if ( operator.equals("/") ) return true;
    else if ( operator.equals("%") ) return true;
    else if ( operator.equals("^") ) return true;

    return false;
  }
  public boolean isBool() {
    if      ( operator.equals("<")  ) return true;
    else if ( operator.equals(">")  ) return true;
    else if ( operator.equals("<=") ) return true;
    else if ( operator.equals(">=") ) return true;
    else if ( operator.equals("==") ) return true;
    else if ( operator.equals("!=") ) return true;
    else if ( operator.equals("&&") ) return true;
    else if ( operator.equals("||") ) return true;
    return false;
  }
  public boolean isString() {
    return left.isString() || right.isString();
  }
  public boolean isVector2() {
    if( left.isVector2() && right.isVector2() ) {
      if      ( operator.equals("+") ) return true;
      else if ( operator.equals("-") ) return true;
    }
    if( left.isVector2() && right.isFloat() ) {
      if      ( operator.equals("*") ) return true;
      else if ( operator.equals("/") ) return true;
    }
    return false;
  }
  public boolean isVector3() {
    //return left.isVector2() && right.isVector2();
    if( left.isVector3() && right.isVector3() ) {
      if      ( operator.equals("+") ) return true;
      else if ( operator.equals("-") ) return true;
    }
    if( left.isVector3() && right.isFloat() ) {
      if      ( operator.equals("*") ) return true;
      else if ( operator.equals("/") ) return true;
    }

    return false;
  }
}
