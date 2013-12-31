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
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class CommandStore {

  private static final int MAX_LINE_CODES = 256;
  
  protected TreeMap<String,Command> nameToCommand  = new TreeMap<String,Command>();
  protected ArrayList<Command>      commandList    = new ArrayList<Command>();
 
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  protected void addCommand( Command cmd, String commandSig ) {
    StringTokenizer st = new StringTokenizer( commandSig.toLowerCase() );
    
    // If there isn't a command signature then we can't add the command
    if( st.countTokens() == 0 ) {
      return;
    }
    
    // Add commands incrementing the index while    
    int index = 0;
    while( addCommandRecurse( nameToCommand, cmd, st, index ) ) {
      ++index;
    }

    //System.out.println( "cl: " + commandList.size() );
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  protected void executeLine( char[]                   compiledLine
                            , ScriptState              state
                            , VariableBank             vars
                            , Queue<ExpressionElement> params )
  {
    if( compiledLine == null || compiledLine.length == 0 ) {
      return;
    }
    
    executeLineRecurse( compiledLine, 0, state, vars, params );
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  protected void executeLineRecurse( char[]                   compiledLine
                                   , int                      byteIndex
                                   , ScriptState              state
                                   , VariableBank             vars
                                   , Queue<ExpressionElement> params )
  {
    int cmdCode = (int)compiledLine[byteIndex++];
    if( cmdCode >= 0 && cmdCode < commandList.size() ) {
      
      Command cmd = (Command)commandList.get( cmdCode );
      
      if( cmd.takesParams() ) {
        // We need to push back all the params in reverse order so they pop off in thier correct order.
        int startParam = byteIndex;
        int endParam   = startParam + cmd.data.params;
            byteIndex += cmd.data.params;
        
        for( int paramIndex = startParam; paramIndex < endParam; ++paramIndex ) {
          ExpressionElement v = vars.get( (int)compiledLine[ paramIndex ] );
          if( v == null ) {
            continue;
          }
          params.offer( v );
        }
      }
      

      if( byteIndex == compiledLine.length - 1 ) {
        state.codeLine = cmd.call( state, params, compiledLine[byteIndex] );
      } else {
        executeLineRecurse( compiledLine, byteIndex, state, vars, params );
      }
    }
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  public char[] compileLine( String line, VariableBank vars ) {
    char[] bytes   = new char[ MAX_LINE_CODES ];
    int    newsize = compileLineRecurse( nameToCommand, vars, bytes, 0, new StringTokenizer( line ) );
    
    if( newsize == -1 ) {
      return null;
    }
    
    char[] output = new char[newsize];
    for( int i=0; i<output.length; ++i )
      output[i] = bytes[i];

    return output;
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  private String mergeTokens( StringTokenizer st
                            , String          token
                            , String          startElement
                            , String          endElement )
  {
    int begin = token.indexOf(startElement);
    int end   = token.indexOf(endElement, begin+1);
    if( begin == -1 ) {
      return token;
    }

    while( end == -1 && st.hasMoreTokens() ) {
      String nextToken = st.nextToken();
      token += " " + nextToken;
      
      end = nextToken.indexOf(endElement);
    }
    
    return token;
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  private int compileLineRecurse( TreeMap<String,Command> nameToCommand
                                , VariableBank            vars
                                , char[]                  bytes
                                , int                     byteIndex
                                , StringTokenizer         st )
  {
    if( st.countTokens() == 0 ) {
      return byteIndex;
    }

    String token = st.nextToken();
    if( nameToCommand.containsKey(token) ) {
      Command cmd = (Command)nameToCommand.get( token );
      
      // Add the code for the byte
      bytes[byteIndex++] = (char)cmd.code;
      
      // If this command takes parameters
      if( cmd.takesParams() ) {
        for( int i=0; i<cmd.data.params; ++i ) {
          // Make sure the required parameters are available
          if( !st.hasMoreTokens() ) {
            return -1;
          }
        
          // get the name of the variable
                  token       = st.nextToken();
          int     varIndex    = -1;
          boolean expression  = ( cmd.data.paramSignature[i] &   VariableBank.VARTYPE_EXPRESSION_FLAG ) > 0;
          int     paramType   =   cmd.data.paramSignature[i] & ( VariableBank.VARTYPE_EXPRESSION_FLAG - 1 );

          // Make sure strings are concatinated together
          if( token.indexOf("\"") >= 0 ) {
            token = mergeTokens( st, token, "\"", "\"" );
            paramType = VariableBank.VARTYPE_STRING;
            //System.out.println("Parsed string: " + token);
          } else if( token.indexOf("#") >= 0 ) {
            int color = 0;
            String a = token.substring( 1, 3 );
            String b = token.substring( 3, 5 );
            String c = token.substring( 5, 7 );
            color += Integer.valueOf( a, 16 ) << 16;
            color += Integer.valueOf( b, 16 ) << 8;
            color += Integer.valueOf( c, 16 );
            token = Integer.toString(color);
            //System.out.println(token);
            paramType = VariableBank.VARTYPE_INTEGER;
          } else if( expression ) {
            token = token;
            while( st.hasMoreTokens() ) {
              token += st.nextToken();
            }

            //System.out.println("Parsing expression: " + token);
            ExpressionElement exp = buildExpressionTree( token, vars, paramType );
            if( exp != null ) {
              bytes[byteIndex++] = (char)exp.getIndex();
              //Expressions consume all remaining tokens, continuing is futile
              break;
            }
          }

          // If we can find an existing variable then get the index, otherwise register and create it
          if( vars.exists( token ) ) {
            varIndex = vars.getVariableIndex( token );
            int varType = vars.getVariable(varIndex).getType();
            //System.out.println("Found variable: " + token + " with type " + varType + " attempt matching " + paramType );
            //if( (paramType & varType) == 0 ) {
              //System.out.println("Invalid variable type: " + token);
            //}
          } else {
            varIndex = vars.registerAndCreate( token, paramType );
            //System.out.println("Creating variable: " + token + " with type " + vars.typeToString(paramType) + " and index " + varIndex);
          }
          
          // This will limit us to 255 variables, so we'll need two bytes in the future
          bytes[ byteIndex++ ] = (char)varIndex;
        }
      }
      
      // If we're finished parsing
      if( st.countTokens() == 0 ) {
        bytes[ byteIndex++ ] = (char)cmd.data.index;
        return byteIndex;
      } else {
        return compileLineRecurse( cmd.nameToCommand, vars, bytes, byteIndex, st );
      }
    } 

    return -1;
  }
  
  

  
  final int EXPR_VALUE  = 1;
  final int EXPR_LPAREN = 2;
  final int EXPR_RPAREN = 3;
  final int EXPR_OP     = 4;
  
  //protected static final String [] boolExprOps  = { ">","<","=","!","&","|" };
  //protected static final String [] mathExprOps  = { "(",")","+","-","*","/","%","^","#" };
  protected static final String [] exprOps      = { "(",")","+","-","*","/","%","^",">","<","!"}; 
  protected static final String [] exprOps2      = { ">=","<=","==","!=","&&","||"};    
  
  private ExpressionElement buildExpressionTreeRecurse( ArrayList<String> expression, VariableBank vars, int resultType )
  {
    int count = expression.size();
    
    if( count == 1 ) {
      vars.registerAndCreate( expression.get(0), resultType );
      ExpressionElement v = vars.get( expression.get(0) );
      
      if( v != null ) return v;
      else            return null; //[undefined variable]
    }
    else if( count == 2 ) { return null; } //[incomplete expression]
    else if( count == 3 ) {
      Expression           exp = new Expression();
                           exp.operator  = expression.get(1);
                           exp.left      = vars.registerAndCreate( expression.get(0) );
                           exp.right     = vars.registerAndCreate( expression.get(2) );
      vars.addExpression ( exp );
      return               exp;

    } else {

      int type = getExprTokenType( expression.get(0) );
      if( type == EXPR_LPAREN ) {
        int index = findMatchingParen( expression );
        // If we couldn't find it then return
        if( index == count ) return null; //[incomplete expression]
        
        // If the matching parenthesis is the last token, then the expression is just inside
        if( index == count - 1 ) {
          
          expression.remove( count - 1 );
          expression.remove( 0 );
          
          return buildExpressionTreeRecurse( expression, vars, resultType );
        } else {
          
          int opIndex    = index   + 1;
          int rightIndex = opIndex + 1;

          // Get the left hand portion of the expression
          ArrayList<String> exprLeft  = new ArrayList<String>();
          for( int i = 1; i < index; ++i )
            exprLeft.add( expression.get(i) );

          // Set the remainder as the right hand side
          ArrayList<String> exprRight = new ArrayList<String>();        
          for( int i = rightIndex; i < expression.size(); ++i )
            exprRight.add( expression.get(i) );            
          
          // Create our expression and set the operator
          Expression exp = new Expression();
                    
          exp.left      = buildExpressionTreeRecurse( exprLeft,  vars, resultType );
          exp.operator  = expression.get(opIndex);
          exp.right     = buildExpressionTreeRecurse( exprRight, vars, resultType );
          
          vars.addExpression( exp );
          
          return exp;
        }
      }
      else if( type == EXPR_VALUE )
      {
        Expression exp = new Expression();

        exp.left     = vars.registerAndCreate( expression.get(0) );
        exp.operator = expression.get(1);
        
        if( exp.left == null ) return null; //[undefined variable]
        
        // Set the remainder as the right hand side
        ArrayList<String> exprRight = new ArrayList<String>();        
        for( int i = 2; i < expression.size(); ++i )
            exprRight.add( expression.get(i) );               

        exp.right = buildExpressionTreeRecurse( exprRight, vars, resultType );
        
        vars.addExpression( exp );
        
        return exp;   
      }      
    }
    return null;
  }

  private ExpressionElement buildExpressionTree( String expression, VariableBank vars, int resultType ) {
    ArrayList<String> expTokens2 = splitExpr( exprOps2, expression );
    ArrayList<String> expTokens;
    if( expTokens2 == null ) {
      expTokens = splitExpr( exprOps, expression );
    } else {
      expTokens = new ArrayList<String>();
      for( String exp : expTokens2 ) {
        if( exprIsOp(exp) ) {
          expTokens.add(exp);
          continue;
        }
        splitExpr( exprOps, exp, expTokens );
      }
    }
    
    // Probably not an expression; so, we return null now.
    if( expTokens == null ) { return null; }

    return buildExpressionTreeRecurse( expTokens, vars, resultType );
  }
  
  private int getExprTokenType( String strToken ) {
    //System.out.println("op1? " + strToken);
    int len = strToken.length();
    if( len == 1 || len == 2 ) {
           if( strToken.charAt(0) == '(' ) return EXPR_LPAREN;
      else if( strToken.charAt(0) == ')' ) return EXPR_RPAREN;
      else if( exprIsOp(strToken)        ) return EXPR_OP;
    }
    return EXPR_VALUE;
  }
  
  private int findMatchingParen( ArrayList<String> tokens )
  {
    int paren = 1;
    int count = tokens.size();
    
    for( int i=1; i<count; ++i )
    {
      String token = tokens.get(i);
      if( token == null || token.length() != 1 )
        continue;
      
           if( token.charAt(0) == '(' ) ++paren;
      else if( token.charAt(0) == ')' ) --paren;
      
      if( paren == 0 )
        return i;
    }
    
    return count;
  }
  protected boolean exprIsOp( String token ) {
    //System.out.println("op? " + token);
    for( String op : exprOps ) {
      if( token.equals(op) )
        return true;
    }
    for( String op : exprOps2 ) {
      if( token.equals(op) )
        return true;
    }
    return false;    
  }  
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  private ArrayList<String> splitExpr( String[] keys, String expression ) { return splitExpr( keys, expression, new ArrayList<String>() ); }
  private ArrayList<String> splitExpr( String[] keys, String expression, ArrayList<String> out ) {
    // Don't return anything if the token's only a char
    if( expression.length() <= 1 ) return null;
    for( String key : keys ) {
      expression = expression.replaceAll( "\\Q"+key+"\\E", "~"+key+"~" );
    }
    
    String[] result = expression.split( "~" );
    for( String s : result ) {
      if( s == null ) continue;
      s = s.trim();
      if( s.length() == 0 || s.contains("~") ) continue;
      out.add( s );
    }
    
    return out;
  }
  
  // --------------------------------------------------------------------------------------------------------------------------------------- //
  private boolean addCommandRecurse( TreeMap<String,Command> nameToCommand, Command cmd, StringTokenizer st, int index ) {

    // Make sure we have a token to process
    if( st.countTokens() > 0 ) {

      // get the next token
      String command = st.nextToken();
      
      // Check if the token is the start of another command definition
      if( command.equals("|") ) {
        cmd.data.index = index;
        return true;
      // Check if we should be parsing a parameter type
      } else if( command.charAt(0) == '+' ) {
        String param = command.substring(1);
        
        boolean expression = false;
        if( param.charAt(0) == '@' )
        {
          expression = true;
          param = param.substring(1);
        }

        cmd.addVarParam( param, expression );
        cmd.data.index = index;
        
        return addCommandRecurse( nameToCommand, cmd, st, index );
      }
      // Otherwise we're still traversing the command tree
      else {
        // Try to find command the parent's list of known commands
        if( nameToCommand.containsKey( command ) ) {
          // This command already exists, let's see if we should add it as a child
          return addCommandRecurse( ((Command)nameToCommand.get( command )).nameToCommand, cmd, st, index );
        // The command didn't exist so let's add it to the list and continue
        } else {
          // Update the command name
          Command newCmd    = cmd.clone();
          newCmd.identifier = command;
          newCmd.code       = commandList.size();
          commandList.add( newCmd );

          // Update the command maps
          //codeToCommand.put( newCmd.code, newCmd );
          nameToCommand.put( command,   newCmd );
          
          return addCommandRecurse( newCmd.nameToCommand, newCmd, st, index );
        }
      }
    }
    else
    {
      //System.out.println( "i: " + index  );
      cmd.data.index = index;
    }


    return false;
  }


  
}
