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


import java.util.LinkedList;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.fieldfx.math.Vector2;
import com.fieldfx.math.Vector3;


public class ScriptInstance
{
  public static final int             DEFAULT_TERMINATION_COUNT = 1000000;
  
  private    ScriptDoc<String>        codeState      = null;
  private    ScriptDoc<char[]>        compiledState  = null;
  protected  CommandStore             compiler       = new CommandStore();
  protected  VariableBank             vars           = new VariableBank();
  private    ScriptState              state          = new ScriptState(); 
  private    Queue<ExpressionElement> params         = new LinkedList<ExpressionElement>();
    
  public ScriptInstance( ScriptDoc<String> codeState ) {
    this.codeState      = codeState;
    this.compiledState  = new ListDoc<char[]>();
    
    new CmdCreateVar    ( compiler );
    new CmdIf           ( compiler );
    new CmdLoop         ( compiler );
    new CmdMathHelpers  ( compiler );
  }
  
  public void setVariable( String name, float    value ) { 
    vars.registerAndCreate( name, VariableBank.VARTYPE_FLOAT);
    vars.setVariable( name, value );
  }
  public void setVariable( String name, int      value ) {
    vars.registerAndCreate( name, VariableBank.VARTYPE_INTEGER); 
    vars.setVariable( name, value );
  }
  public void setVariable( String name, boolean  value ) {
    vars.registerAndCreate( name, VariableBank.VARTYPE_BOOLEAN); 
    vars.setVariable( name, value );
  }
  public void setVariable( String name, String   value ) {
    vars.registerAndCreate( name, VariableBank.VARTYPE_STRING); 
    vars.setVariable( name, value );
  }
  public void setVariable( String name, Vector2  value ) {
    vars.registerAndCreate( name, VariableBank.VARTYPE_STRING); 
    vars.setVariable( name, value );
  }
  public void setVariable( String name, Vector3  value ) {
    vars.registerAndCreate( name, VariableBank.VARTYPE_STRING); 
    vars.setVariable( name, value );
  }

  public void compile() {
    this.compiledState.clear();
    for( int i=0; i<codeState.size(); ++i ) {
      String codeLine = codeState.get(i);
      codeLine = codeLine.replaceAll("->"," -> ");
      codeLine = codeLine.replaceAll("<"," < ");
      codeLine = codeLine.replaceAll(">"," > ");
      codeLine = codeLine.replaceAll(","," , ");
      this.compiledState.add( compiler.compileLine( codeLine, vars ) );
    }
    //System.out.println("Compile complete...\n\n");
  }

  public void reset( Object userState )
  {
    state.reset( userState );
    state.vars = vars;    
  }
  
  public boolean step()
  {
    char[] code = (char[])compiledState.get( state.codeLine );

    params.clear();
    if( code != null && code.length > 0 ) {
      compiler.executeLine( code, state, vars, params ); 
    } else {
      ++state.codeLine;
    }
      
    if( state.codeLine == -1 )
      return false;
    
    return true;
  }
  
  public boolean run( Object userState ) {
    return run( userState, DEFAULT_TERMINATION_COUNT );
  }
  
  public boolean run( Object userState, int terminationCount ) {
    reset( userState );
    
    int codeLines = codeState.size();
    int counter   = 0;
    //System.out.println("codeLines: " + codeLines);
    
    while( state.codeLine >= 0         && 
           state.codeLine <  codeLines && 
           counter        <  terminationCount )
    {
      if( !step() ) return false;
      ++counter;
    }
    
    return true;
  }
  

}
