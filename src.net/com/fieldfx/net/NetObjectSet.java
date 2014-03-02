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
package com.fieldfx.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fieldfx.serialize.BinarySerializer;
import com.fieldfx.serialize.Serializable;
import com.fieldfx.serialize.Serializer;


// Encapsulates a set of NetObjects
public class NetObjectSet implements Serializable {
  // -------------------------------------------------------------------------------- //
  private NetMesh                 parent        = null;
  private List<NetObject>         netObjects    = new ArrayList<NetObject>();
  private Map<Integer, NetObject> idToNetObject = new TreeMap<Integer, NetObject>();
  
  // -------------------------------------------------------------------------------- //
  public String       getType ( ) { return "NetObjectSet"; }
  public Serializable clone   ( ) { return this;           }  
  // -------------------------------------------------------------------------------- //
  public void serialize ( Serializer s ) {
    if( s.isLoading() ) load((BinarySerializer)s);
    else                save((BinarySerializer)s);
  }
  // -------------------------------------------------------------------------------- //
  public void init   ( NetMesh parent ) {
    this.parent = parent;
  }
  // -------------------------------------------------------------------------------- //
  public void tick   () {
    List<NetObject> toRemove = new ArrayList<NetObject>();
    for( NetObject obj : netObjects ) {
      obj.tick( parent );
      if( obj.life == 0 )
        toRemove.add(obj);
    }
    for( NetObject obj : toRemove )
      netObjects.remove(obj);
  }
  // -------------------------------------------------------------------------------- //
  public void addObject(NetObject obj) {
    netObjects.add(obj);
    idToNetObject.put(obj.id, obj);
  }
  // -------------------------------------------------------------------------------- //
  public List<NetObject> getAll( String type ) {
    List<NetObject> objs = new ArrayList<NetObject>();
    for( NetObject n : netObjects ) {
      if( n.getType().equals( type ) )
        objs.add( n );
    }
    return objs;
  }
  // -------------------------------------------------------------------------------- //
  public List<NetObject> get() { return netObjects; }
  // -------------------------------------------------------------------------------- //
  public NetObject getById( int ID ) {
   if( idToNetObject.containsKey(ID) )
     return idToNetObject.get(ID);
   return null;
  }
  
  // -------------------------------------------------------------------------------- //
  private void load( BinarySerializer s ) {
    int count = 0;
    count = s.serialize("count", count);
    for(int i = 0; i < count; ++i) {
      int objID = NetObject.invalid_id;
      Byte objType = 0;
      objID   = s.serialize( "id",   objID   );
      objType = s.serialize( "type", objType );
      
      // Look up the object
      NetObject obj = idToNetObject.get(objID);
      if(obj == null) {
        //System.out.println( "Creating object "+objID );
        obj = (NetObject)s.cloneByID(objType);
        obj.id = objID;
        addObject(obj);
      }
      
      obj.life = 30; // Some default tick life
      List<NetRelation> updatedRelations = new ArrayList<NetRelation>();
                 s.serialize( "relations", updatedRelations ); //This should only replace external relations
                 s.serialize( "pos",       obj.pos );
                 
      // Now we need to update our mappings of all external relations for these?
      List<NetRelation> existingRelations = new ArrayList<NetRelation>();           
      for( NetRelation updated : updatedRelations ) {
        for( NetRelation ours : obj.relations ) {
          if( updated.equals( ours ) ) {
            ours.renew();
            existingRelations.add(updated);
          } 
        }
      }
      
      for( NetRelation existingRelation : existingRelations ) {
        updatedRelations.remove( existingRelation );
      }
      
      for( NetRelation updated : updatedRelations ) {
        updated.renew();
        obj.relations.add( updated );
      }
      
      // Finally serialize the object
      obj.serialize( s );
    }
  }
  // -------------------------------------------------------------------------------- //
  private void save( BinarySerializer s ) {
    s.serialize("count", netObjects.size());
    for(NetObject obj : netObjects) {
      s.serialize( "id",        obj.id );
      s.serialize( "type",      s.getTypeIdFromType( obj.getType() ) );
      s.serialize( "relations", obj.relations );
      s.serialize( "pos",       obj.pos );
      obj.serialize( s );
    }
  }

}
