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

// -------------------------------------------------------------------------------- //
  // Unique ID generation
  class NetObjectIDBank {
    private int nextID = NetObject.invalid_id;
    private int lastID = NetObject.invalid_id;

    public void init(int firstID, int lastID) { 
      this.nextID = firstID;
      this.lastID = lastID;
    }

    // Reserves a single ID, if available
    public int reserveID() {
      if(nextID < lastID) {
        return nextID++;
      } else {
        return NetObject.invalid_id;
      }
    }
    
    // Reserves a range of IDs, if available
    public int reserveIDRange( int numIDs ) {
      int ret = NetObject.invalid_id;
      
      if(nextID + numIDs < lastID) {
        ret     = nextID;
        nextID += numIDs;
      }
      
      return ret;
    }

  }
