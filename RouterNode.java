import javax.swing.*;        

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[][] costs = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");

    // Initialize all fields to infinite
    for( int col = 0 ; col < RouterSimulator.NUM_NODES; col++ ){
	for(int row = 0 ;row < RouterSimulator.NUM_NODES; row++ ){
	    this.costs[col][row] = RouterSimulator.INFINITY;
	}
    }

    // Copy start costs for node to table
    System.arraycopy(costs, 0, this.costs[myID], 0, RouterSimulator.NUM_NODES);
    
    // Send nodes dist-vector to neighbors.
    for( int nodeID = 0 ; nodeID < RouterSimulator.NUM_NODES; ++nodeID ){
	if( myID != nodeID ){
	    RouterPacket newPkt = new RouterPacket(this.myID, nodeID, this.costs[myID]);
	    sendUpdate(newPkt); 
	}
    }
    
  }

  // Called by the simulator (executed) when a node receives an update from one of its neighbors
  public void recvUpdate(RouterPacket pkt) {

      if(pkt.destid != this.myID){
	  return;
      }

      System.arraycopy(pkt.mincost, 0, costs[pkt.sourceid], 0, RouterSimulator.NUM_NODES);
      
      for( int myIndex = 0; myIndex < RouterSimulator.NUM_NODES; myIndex++){
	  if(myIndex == myID){
	      continue;
	  }
	  else{
	      for( int i = 0; i < RouterSimulator.NUM_NODES; i++ ){ // One Node (not this node)
		  if(i == myID) continue;
		  for( int j = 0; j < RouterSimulator.NUM_NODES; j++ ){ // Another node (not this node)
		      if( j == i || i == myID ){ continue; }

		      int mini = Math.min( this.costs[i][myID]/*Node to this node*/ + this.costs[i][myIndex]/*Node to target node*/,
					  this.costs[j][myID]/*Node to this node*/ + this.costs[j][myIndex]/*Node to target node*/ );

		      if(mini < costs[myID][myIndex]){
			  costs[myID][myIndex] = mini;
		      }
		  }
	      }
	  }
      }
  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
      // Send yourself as infinity
    sim.toLayer2(pkt);
  }
  

  // used for debugging and testing of code, and also for demonstration of your solution to the assignment. 
  // This method should print the distance vector table (i.e. the routing table) in a format that you and 
  // your lab assistant can read and understand.
  public void printDistanceTable() {
	  myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());
	  
	  myGUI.print(String.format("%-2s", ""));
	  for(int xtitle = 0; xtitle < RouterSimulator.NUM_NODES; ++xtitle){
	      myGUI.print( String.format( "%4s", (char)(xtitle+65) ) );
	  }
	  myGUI.println();

	  for(int col = 0; col < RouterSimulator.NUM_NODES; col++){
	      myGUI.print( String.format( "%1s", (char)(col+65) ) );
	      for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
		  myGUI.print( String.format( "%4s", this.costs[col][row] ) );
	      }
	      myGUI.println();
	  }
	 
  }

  // called by the simulator (executed) when the cost of link of a node is about to change
  public void updateLinkCost(int dest, int newcost) {

  }

}
