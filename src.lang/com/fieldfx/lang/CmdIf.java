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

import java.util.Queue;

class CmdIf extends Command
{
  //If ...
  //Else
  //End IF
  //---------------------------------------------------------------------------------
  public         CmdIf ( CommandStore cs ) { super( cs, "if +@bool | else | endif" ); }
  public         CmdIf ( Command copy    ) { super( copy ); }
  public Command clone ( )                 { return new CmdIf(this); }  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex ) {
    if( state.jumpLoop )
      return state.nextCommand();  

    switch( callIndex ) {
      case 0: return BeginIf( state, params );
      case 1: return Else( state );
      case 2: return EndIf( state );
    }
    
    return state.nextCommand();
  }  
  // ---------------------------------------------------------------------------------
  protected int BeginIf( ScriptState state, Queue<ExpressionElement> params ) {
    
    ++state.ifNest;
    
    // If we're searching for the next else or endif then just skip this if statement
    if( state.jumpEndIf || state.jumpElse )
      return state.nextCommand();  

    state.jumpNest  = state.ifNest;
    state.jumpEndIf = false;
      
    if( popBool( params ) ) {
      state.jumpElse  = false;
    }
    // If this wasn't true, let's specify that' we're jumping to the next
    //  else or end if at the same level    
    else {
      state.jumpElse  = true;
    }
    
    
    // Continue parsing  
    return state.nextCommand();
  }
  // ---------------------------------------------------------------------------------
  protected int Else( ScriptState state ) {

    if( state.ifNest == state.jumpNest ) {
      state.jumpElse = !state.jumpElse;
    }
    
    return state.nextCommand();  
  }
  // ---------------------------------------------------------------------------------
  protected int EndIf( ScriptState state ) {

    if( state.ifNest == state.jumpNest ) {
      state.jumpElse  = false;
      state.jumpEndIf = false;
      state.jumpNest  = 0;
    }
    --state.ifNest;      

    
    return state.nextCommand();
  }  
}
