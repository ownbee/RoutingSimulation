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
	    RouterPacket newPkt = new RouterPacket(this.myID, nodeID, this.costs[nodeID]);
	    sendUpdate(newPkt);
	}
    }
    
  }

  // Called by the simulator (executed) when a node receives an update from one of its neighbors
  public void recvUpdate(RouterPacket pkt) {
      System.arraycopy(pkt.mincost, 0, costs[pkt.sourceid], 0, RouterSimulator.NUM_NODES);
      
      for( int curNode = 0; curNode < RouterSimulator.NUM_NODES; curNode++){
	  if(curNode == myID){
	      costs[myID][curNode] = 0;
	  }
	  else{
	      int min = 0;
	      for( int i = 0; i < RouterSimulator.NUM_NODES; i++ ){
		  if( i == myID){
		      continue;
		  }
		  min = Math.min(costs[curNode][i] + costs[curNode][curNode], );
	      }
	      costs[myID][curNode] = min;
	  }
      }
  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);
  }
  

  // used for debugging and testing of code, and also for demonstration of your solution to the assignment. 
  // This method should print the distance vector table (i.e. the routing table) in a format that you and 
  // your lab assistant can read and understand.
  public void printDistanceTable() {
	  myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());

	  myGUI.println("---------------------------------");
	  for(int col = 0; col < RouterSimulator.NUM_NODES; col++){
	      for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
		  myGUI.print("  " + this.costs[col][row]);
	      }
	      myGUI.println();
	  }
	 
  }

  // called by the simulator (executed) when the cost of link of a node is about to change
  public void updateLinkCost(int dest, int newcost) {

  }

}
