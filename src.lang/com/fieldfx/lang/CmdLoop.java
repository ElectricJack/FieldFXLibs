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

public class CmdLoop extends Command
{
  // Loop I From X To Y
  // Loop X Times
  // End Loop
  //---------------------------------------------------------------------------------
  public         CmdLoop ( CommandStore cs ) { super( cs, "loop +int times | loop +variable from +int to +int | end loop"); }
  public         CmdLoop ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdLoop(this); }  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpEndIf || state.jumpElse )
      return state.nextCommand();
      
    switch( callIndex ) {
      case 0: return BeginLoopTimes( state, popInt(params) );
      case 1: return BeginLoopFrom( state, popVarRef(params), popInt(params), popInt(params) );
      case 2: return EndLoop( state );
    }

    return state.nextCommand();
  }
  
  // ---------------------------------------------------------------------------------
  protected int BeginLoopTimes( ScriptState state, int nTimes ) {

    if( state.jumpLoop )
      return state.nextCommand();

    if( state.loopNest == state.loopInfo.length - 1 )
      return -1; // Too many nested loops
    

    if( nTimes > 0 ) {
      state.loopInfo[ state.loopNest ].varIndex = -1;
      state.loopInfo[ state.loopNest ].index    = 0;
      state.loopInfo[ state.loopNest ].start    = 0;
      state.loopInfo[ state.loopNest ].end      = nTimes - 1;    
      state.loopInfo[ state.loopNest ].line = state.nextCommand();
    } else {
      state.jumpNest = state.loopNest;
      state.jumpLoop = true;
    }
    
    state.loopNest++;
    
    return state.nextCommand();
  }
  // ---------------------------------------------------------------------------------
  protected int BeginLoopFrom( ScriptState state, int varIndex, int start, int end ) {

    if( state.jumpLoop )
      return state.nextCommand();

    if( state.loopNest == state.loopInfo.length - 1 )
      return -1; // Too many nested loops

    if( start != end ) {
      state.loopInfo[ state.loopNest ].varIndex = varIndex;
      state.loopInfo[ state.loopNest ].start    = start;
      state.loopInfo[ state.loopNest ].index    = start;
      state.loopInfo[ state.loopNest ].end      = end;
      state.vars.setVariable( varIndex, start );
      state.loopInfo[ state.loopNest ].line = state.nextCommand(); 
    } else {
      state.jumpNest = state.loopNest;
      state.jumpLoop = true;
    }
    
    state.loopNest++;
    
    return state.nextCommand();
  } 
  // ---------------------------------------------------------------------------------
  protected int EndLoop( ScriptState state ) {
    
    if( state.jumpLoop ) {
      if( state.loopNest == state.jumpNest ) {
        state.jumpLoop  = false;
        state.jumpNest  = 0;
      }
      return state.nextCommand();
    }

    int loopInfoIndex = state.loopNest - 1;
    ScriptLoopState loopState = state.loopInfo[ loopInfoIndex ];

    // Check if we're done with the loop
    if( loopState.index == loopState.end ) {
      --state.loopNest;
      return state.nextCommand();
    }
  
    // Increment the counter in the correct direction
    if( loopState.end < loopState.start )
      --loopState.index;
    else
      ++loopState.index;
  
    // Update the variable if it exists
    if( loopState.varIndex != -1 )
      state.vars.setVariable( loopState.varIndex, loopState.index );
  
    return loopState.line;
  }
}
