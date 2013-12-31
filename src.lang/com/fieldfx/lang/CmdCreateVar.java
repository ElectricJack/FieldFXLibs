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

import java.util.Queue;


public class CmdCreateVar extends Command
{
  // --------------------------------------------------------------------------------- //
  public CmdCreateVar ( CommandStore cs ) { 
    super( cs, 
      "int +variable = +@int"+
      " | float +variable = +@float"+
      " | bool +variable = +@bool"+
      " | string +variable = +@string"+
      " | vector2 +variable = < +float , +float >"+
      " | vector3 +variable = < +float , +float , +float >"+
      " | vector2 +variable = +@vector2"+
      " | vector3 +variable = +@vector3"
    );
  }
  public CmdCreateVar ( Command copy ) { 
    super( copy );
  }
  public Command clone ( ) {
    return new CmdCreateVar(this);
  }  
  // --------------------------------------------------------------------------------- //
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex ) {
    
    if( state.jumpEndIf || state.jumpElse || state.jumpLoop )
      return state.nextCommand();  
      
    switch( callIndex ) {
      case 0: return createInteger  ( state, popVarRef(params), popInt(params)     );
      case 1: return createFloat    ( state, popVarRef(params), popFloat(params)   );
      case 2: return createBoolean  ( state, popVarRef(params), popBool(params)    );
      case 3: return createString   ( state, popVarRef(params), popString(params)  );
      case 4: return createVector2  ( state, popVarRef(params), new Vector2(popFloat(params),popFloat(params)) );
      case 5: return createVector3  ( state, popVarRef(params), new Vector3(popFloat(params),popFloat(params),popFloat(params)) );
      case 6: return createVector2  ( state, popVarRef(params), popVector2(params) );
      case 7: return createVector3  ( state, popVarRef(params), popVector3(params) );
      
    }
    
    return -1;
  }
  
  // --------------------------------------------------------------------------------- //
  private int createInteger( ScriptState state, int varIndex, int value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createFloat( ScriptState state, int varIndex, float value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createBoolean( ScriptState state, int varIndex, boolean value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createString( ScriptState state, int varIndex, String value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createVector2( ScriptState state, int varIndex, Vector2 value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createVector3( ScriptState state, int varIndex, Vector3 value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
}
