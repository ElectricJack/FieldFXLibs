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



import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;

import hypermedia.net.*;

import com.fieldfx.util.*;
import com.fieldfx.serialize.BinarySerializer;
import com.fieldfx.serialize.Serializer;

public class NetMesh {

    // -------------------------------------------------------------------------------- //
  // Network message IDs
  private static class MsgID {
    public static final byte BEAT             = 1;
    public static final byte BEAT_REPLY       = 2;
    public static final byte OBJECT_ID_RANGE  = 3;
    public static final byte OBJECT_UPDATE    = 4;
  }

  private final int                          broadcastPort          = 13337;
  private boolean                            logging                = true;
  private boolean                            loggingErrors          = true;

  // -------------------------------------------------------------------------------- //
  private NetTimer                           updateHeartbeat, becomeCoordinator, sendUpdate;
  private PApplet                            parent                 = null;

  private List<NetRule>                      rules                  = new ArrayList<NetRule>();
  private Map<NetObject, List<NetObject>>    relations              = new HashMap<NetObject, List<NetObject>>();
  
  private ConcurrentLinkedQueue<NetMessage>  messages               = new ConcurrentLinkedQueue<NetMessage>();
  
  private HashMap<String, NetNode>           guidToNode             = new HashMap<String,  NetNode>();
  private Map<Integer, NetNode>              suidToNode             = new TreeMap<Integer, NetNode>();
  private List<NetNode>                      nodes                  = new ArrayList<NetNode>();
  
  private NetObjectIDBank                    netObjectIDBank        = new NetObjectIDBank();
  private NetObjectSet                       localObjectSet         = new NetObjectSet();
  private NetObjectSet                       remoteObjectSet        = new NetObjectSet();
  //private NetTracker                         trackerClient          = null;
  
  private UDP                                listenConnection       = null;
  private UDP                                meshConnection         = null;
  private String                             meshName               = null;
  
  private String                             localIP                = "";
  private String                             localNodeGUID          = null;
  private String                             coordinatorNodeGUID    = null;
  private boolean                            isCoordinator          = false;
  
  private Serializer                         serializer             = new BinarySerializer();
  private String                             ipv4 = "";
  private String                             ipv6 = "";
  
  public List<NetObject> getRemoteObjects( String type ) {
    return remoteObjectSet.getAll(type);
  }
  public List<NetObject> getLocalObjects( String type ) {
    return localObjectSet.getAll(type);
  }
  public boolean isCoordinator() {
    return isCoordinator;
  }

  // -------------------------------------------------------------------------------- //
  // Passing 'null' for trackerAddress will restrict the mesh to the LAN
  /*public NetMesh( PApplet parent, String meshName ) {
    this( parent, meshName, null );
  }*/

  public NetMesh( PApplet parent, String meshName ) {
    this(parent, meshName, true);
  }
  public NetMesh( PApplet parent, String meshName, Boolean logging ) {
    this.parent            = parent;
    this.meshName          = meshName;
    this.localNodeGUID     = getRandomGuid();
    this.updateHeartbeat   = new NetTimer( 2500 );
    this.becomeCoordinator = new NetTimer( 2000 );
    this.sendUpdate        = new NetTimer( 10 );
    
    meshConnection = new UDP(this, 0);
    meshConnection.log(false);
    meshConnection.listen(true);

    try {
      
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while ( interfaces.hasMoreElements() ) {
      
        NetworkInterface current = interfaces.nextElement();
        if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
        
        Enumeration<InetAddress> addresses = current.getInetAddresses();
        while( addresses.hasMoreElements() ) {
            
          InetAddress current_addr = addresses.nextElement();
          if( current_addr.isLoopbackAddress() ) {
            continue;
          }

          if (current_addr instanceof Inet4Address) {
            ipv4 = current_addr.getHostAddress();
          } else if (current_addr instanceof Inet6Address) {
            ipv6 = current_addr.getHostAddress();
          }
        }
    }

    }catch( Exception e ) {
    }

    
    if( logging ) { 
      log( "Local IPV4: " + ipv4 );
      log( "Local IPV6: " + ipv6 );
      log( "Connecting to UDPMesh " + meshName + " on port " + meshConnection.port() );
    }
    
    /*
    if( trackerAddress != null ) {
      trackerClient = new NetTracker(parent, trackerAddress, "192.168.1.12", meshConnection.port());
      trackerClient.setUID( localNodeGUID );
      trackerClient.beat();
    }*/
    
    sendBeatBroadcast();
    
    serializer.registerType(new NetRelation());
        
    this.localObjectSet.init  ( this );
    this.remoteObjectSet.init ( this );
    
    // Add the local node
    //addNode("127.0.0.1", meshConnection.port(), localNodeGUID);
  }

  // -------------------------------------------------------------------------------- //
  public  boolean  isConnected()            { return (nodes.size() > 1 || isCoordinator); }
  public  boolean  isDisconnected()         { return !isConnected(); }
  public  int      getNodeCount()           { return nodes.size(); }
  public  NetNode  getNode( int index )     { return (index >= 0 && index < nodes.size()) ? nodes.get(index) : null; }
  public  String   getLocalGUID()           { return localNodeGUID; }
  public  String   getLocalIP( boolean v6 ) { return v6? ipv6 : ipv4; }
  // -------------------------------------------------------------------------------- //
  public void registerNetObject(NetObject obj) {
    serializer.registerType(obj);
  }
  // -------------------------------------------------------------------------------- //
  // Adds a NetObject and assigns a unique ID
  public NetObject addNetObject( String objectType ) {
    NetObject newObj = createNetObject(objectType, netObjectIDBank.reserveID());
    if( newObj != null ) {
      if( logging ) log( "Adding local " + objectType );
      
      newObj.life = -1;
      localObjectSet.addObject( newObj );
      newObj.notifyAdd();
    }
    
    return newObj;
  }
  
  // -------------------------------------------------------------------------------- //
  public void addNode( String address, int port, String guid, int suid ) {
    if(logging) log( "addNode \"" + guid + "\" with addr " + address + " : " + port + " and suid " + suid );
    
    if( findNode(guid) != null ) {
      if(loggingErrors) logError( "Node with guid "+guid+" already exists. Returning..." );
      return;
    }
    
    NetNode          newNode = new NetNode(address, port, guid, meshConnection);
    nodes.add    (     newNode );
    guidToNode.put ( guid, newNode );
    suidToNode.put ( suid, newNode );
  }
  
  // -------------------------------------------------------------------------------- //
  public NetNode findNode( String guid ) { return guidToNode.get(guid); }
  public NetNode findNode( int    suid ) { return suidToNode.get(suid); }
  
  // -------------------------------------------------------------------------------- //
  public void receive( byte[] data, String ip, int port ) {
    messages.add( new NetMessage(data, ip, port) );
  }
  
  // -------------------------------------------------------------------------------- //
  public void update() {
    
    processMessages();

    //if( coordinatorNodeGUID != null ) {
      //updateTracker();
    //}

    updateRules();
    updateRelations();
    updateNetObjects();
    
    updateWhoIsMeshCoordinator();
  }
  
  // -------------------------------------------------------------------------------- //
  public void addRule( NetRule r ) {
    rules.add( r );
  }
  
  /*
  // -------------------------------------------------------------------------------- //
  private void updateTracker() {
    if( trackerClient != null )
      trackerClient.update();
    if( isTimeTo( updateHeartbeat ) ) {
      if( trackerClient != null ) {
        trackerClient.beat();
      }
          
      if( coordinatorNodeGUID == null )
        sendBeatBroadcast();
    }
  }*/

  // -------------------------------------------------------------------------------- //
  // Called by rules to keep relations alive
  protected void updateRelation( NetObject a, NetObject b) {
    if(  a.id == b.id  ) return;
      //PApplet.println("Adding relation!");
    if( a.id < b.id ) {
      a.updateRelation(this, b);
    } else {
      b.updateRelation(this, a);
    }

    if( relations.containsKey( a ) ) {
      List<NetObject> alist = relations.get(a);
      if( !alist.contains(b) )
        alist.add(b);
    } else {
      List<NetObject> alist = new ArrayList<NetObject>();
      alist.add(b);
      relations.put(a, alist);
    }
    
    if( relations.containsKey(b) ) {
      List<NetObject> blist = relations.get(b);
      if( !blist.contains(a) )
        blist.add(a);
    } else {
      List<NetObject> blist = new ArrayList<NetObject>();
      blist.add(a);
      relations.put(b, blist);
    }
  }
  // -------------------------------------------------------------------------------- //
  protected void killRelation( NetObject a, NetObject b ) {
    if( relations.containsKey( a ) ) {
      List<NetObject> alist = relations.get(a);
      if( alist.contains(b) ) {
        alist.remove(b);
      }
    }
    if( relations.containsKey( b ) ) {
      List<NetObject> blist = relations.get(b);
      if( blist.contains(a) ) {
        blist.remove(a);
      }
    }
  }
  // -------------------------------------------------------------------------------- //
  private String getRandomGuid() {
    final char[] alpha = {
      'a','b','c','d','e','f','g','h','i','j','k','l','m',
      'n','o','p','q','r','s','t','u','v','w','x','y','z'
    };
    String guid   = new String();
    int    digits = 4;
    for( int i=0; i<digits; ++i ) {
      guid += alpha[ ((int)parent.random(0.f,26.f)) % 26 ];
    }
    return guid;
  }
  // -------------------------------------------------------------------------------- //
  private void updateNetObjects() {
    localObjectSet.tick();
    remoteObjectSet.tick();

    if( isTimeTo( sendUpdate ) )
      sendLocalUpdateBroadcast();
  }
  // -------------------------------------------------------------------------------- //
  private void updateRules() {
    //@TODO this function is shameful.
    // Here we need some quick way of going through all the potential relations 
    for( NetRule r : rules ) {
      for( NetObject a : get(true).get() ) {
        for( NetObject b : get(true).get() ) {
          r.updateRelation(a, b, this);
        }
      }
      for( NetObject a : get(true).get() ) {
        for( NetObject b : get(false).get() ) {
          r.updateRelation(a, b, this);
        }
      }
    }
  }
  // -------------------------------------------------------------------------------- //
  private void updateRelations() {
    for( NetObject key : relations.keySet() ) {
      List<NetObject> values = relations.get(key);
      for( NetRule r : rules ) {
        r.apply(key, values);
      }
    }
  }
  // -------------------------------------------------------------------------------- //
  // Becoming the coordinator if only node in mesh
  private void updateWhoIsMeshCoordinator() {
    // If we've gone too long without a coordinator, become the coordinator
    if( coordinatorNodeGUID == null && isTimeTo( becomeCoordinator ) ) {
      if( logging ) log( "UDPMesh.update: becoming the mesh coordinator" );
      
      coordinatorNodeGUID = localNodeGUID;
      isCoordinator       = true;
      netObjectIDBank.init(0, 1024*1024);
      
      createListenConnection();
    }
  }
  
  // -------------------------------------------------------------------------------- //
  private NetObject createNetObject( String objectType, int objectID ) {
    if( objectID == NetObject.invalid_id )
      return null;
      
    if(logging) log( "Creating NetObject \"" + objectType + "\" with id " + objectID );
      
    NetObject newObj = (NetObject)((BinarySerializer)serializer).cloneByType( objectType );
    newObj.id = objectID;
    return newObj;
  }

  // -------------------------------------------------------------------------------- //
  private void createListenConnection() {
    listenConnection = new UDP(this, broadcastPort);
    listenConnection.log(false);
    listenConnection.listen(true);
  }

  // -------------------------------------------------------------------------------- //
  private void sendLocalUpdateBroadcast() {
    // Send an update for our local objects
    byte[] objData = ((BinarySerializer)serializer).save(localObjectSet);
    byte[] msgData = new byte[] {MsgID.OBJECT_UPDATE};
           msgData = PApplet.concat(msgData, objData); //@TODO - better way to handle this (concat)
    
    for(NetNode node : nodes) {
      node.send(msgData);
    }
  }
  // -------------------------------------------------------------------------------- //
  // Broadcasts a "beat" message over the LAN
  private void sendBeatBroadcast() {
    byte[] msgData = new byte[] {MsgID.BEAT};
           msgData = PApplet.concat( msgData, localNodeGUID.getBytes() ); //@TODO - better way to handle this (concat)

    meshConnection.send(msgData, "255.255.255.255", broadcastPort);
  }
  // -------------------------------------------------------------------------------- //
  private void sendBeatReply(String ip, int port) {
    byte isCoordinatorByte = isCoordinator ? (byte)1 : (byte)0;
    
    byte[] msgData = new byte[] { MsgID.BEAT_REPLY, isCoordinatorByte };
           msgData = PApplet.concat( msgData, localNodeGUID.getBytes() ); //@TODO - better way to handle this (concat)

    meshConnection.send(msgData, ip, port);
  }
  // -------------------------------------------------------------------------------- //
  // Reserves a range of object IDs and sends a message
  private void sendObjectIDRange(String ip, int port) {
    int numIDs  = 1024;
    int firstID = netObjectIDBank.reserveIDRange(numIDs);
    int lastID  = firstID + numIDs;
    
    if( logging ) log( "sending object range "+firstID+", "+lastID);
    
    byte[] msgData = new byte[] {MsgID.OBJECT_ID_RANGE};
         msgData = PApplet.concat(msgData, toByta(firstID));
           msgData = PApplet.concat(msgData, toByta(lastID));
    
    meshConnection.send(msgData, ip, port);
  }

  // -------------------------------------------------------------------------------- //
  private void processMessages() {
    // Process messages
    while( messages.size() > 0 ) {
      NetMessage msg = messages.poll();
      processMessage( msg.data, msg.ip, msg.port );
    }
  }
  // -------------------------------------------------------------------------------- //
  private void processMessage( byte[] data, String ip, int port ) {
    byte msgID = data[0];
    
    if      ( msgID == MsgID.BEAT            ) processBeatMessage    ( data, ip, port );
    else if ( msgID == MsgID.BEAT_REPLY      ) processBeatReply      ( data, ip, port );
    else if ( msgID == MsgID.OBJECT_ID_RANGE ) processObjectIdRange  ( data, ip, port );
    else if ( msgID == MsgID.OBJECT_UPDATE   ) processObjectUpdate   ( data, ip, port );
    else {
      if( loggingErrors ) logError( "received unknown msg " + msgID );
    }
  }
  // -------------------------------------------------------------------------------- //
  private void processBeatMessage( byte[] data, String ip, int port ) {
    String senderNodeID = new String( Arrays.copyOfRange(data, 1, data.length) );
    PApplet.println("received Beat msg from "+senderNodeID);
    
    // If this is a new node, add it and give it some object IDs
    if( findNode(senderNodeID) == null ) {
      addNode( ip, port, senderNodeID, -1 );
      if(isCoordinator)
        sendObjectIDRange(ip, port);
    }
    
    findNode(senderNodeID).lastRecvTime = parent.millis();
    
    sendBeatReply(ip, port);
  }
  // -------------------------------------------------------------------------------- //
  private void processBeatReply( byte[] data, String ip, int port ) {
    byte   isCoordinatorData = data[1];
    String senderNodeID      = new String( Arrays.copyOfRange(data, 2, data.length) );

    if( logging ) log( "received Beat_Reply msg " + isCoordinatorData + ", " + senderNodeID);
    
    // If this is a new node, add it
    if( findNode(senderNodeID) == null ) {
      addNode(ip, port, senderNodeID, -1);
    }
    
    // If this is the coordinator, set coordinatorNodeGUID
    if( isCoordinatorData != (byte)0 ) {
      coordinatorNodeGUID = senderNodeID;
    }
      
    findNode(senderNodeID).lastRecvTime = parent.millis();
  }
  // -------------------------------------------------------------------------------- //
  private void processObjectUpdate( byte[] data, String ip, int port ) {
    //parent.println("received OBJECT_UPDATE msg");
    ((BinarySerializer)serializer).load( Arrays.copyOfRange(data, 1, data.length), remoteObjectSet );
  }
  // -------------------------------------------------------------------------------- //
  private void processObjectIdRange( byte[] data, String ip, int port ) {
    int firstID = toInt( Arrays.copyOfRange(data, 1, 5) );
    int lastID  = toInt( Arrays.copyOfRange(data, 5, 9) );
    if( logging ) log( "received OBJECT_ID_RANGE msg "+firstID+", "+lastID);
    
    netObjectIDBank.init(firstID, lastID);
  }


  
  
  // -------------------------------------------------------------------------------- //
  //TODO: Move these functions to the serializer
  // Packing functions
  static byte[] toByta(float data) {
    return toByta(Float.floatToRawIntBits(data));
  }
  // -------------------------------------------------------------------------------- //
  static byte[] toByta(int data) {
      return new byte[] {
        (byte)( (data >> 24) & 0xff ),
        (byte)( (data >> 16) & 0xff ),
        (byte)( (data >>  8) & 0xff ),
        (byte)( (data >>  0) & 0xff ),
      };
  }
  // -------------------------------------------------------------------------------- //
  static int toInt(byte[] data) {
    int     result  = 0;
            result |= (int)data[0] << 24;
            result |= (int)data[1] << 16;
            result |= (int)data[2] << 8;
            result |= (int)data[3] << 0;
    return  result;
  }

  private void log      ( String message ) { System.out.println( message ); }
  private void logError ( String error   ) { System.out.println( error );   }

  // -------------------------------------------------------------------------------- //
  protected NetObjectSet get(boolean local) { return local? localObjectSet : remoteObjectSet; }
  
  
  // -------------------------------------------------------------------------------- //
  private boolean isTimeTo(NetTimer timer) {
    if( (parent.millis() - timer.lastUpdate) > timer.period ) {
      timer.lastUpdate = parent.millis();
      return true;
    }  
    return false;
  }
  // -------------------------------------------------------------------------------- //  
  private class NetTimer {
    public NetTimer( int period ) { 
      this.period = period;
      this.lastUpdate = parent.millis();
    }
    protected int period;
    protected int lastUpdate;
  }

}
