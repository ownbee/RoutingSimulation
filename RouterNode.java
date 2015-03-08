import javax.swing.*;        

public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[][] costs = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    private int[] myNbr = new int[RouterSimulator.NUM_NODES];
    private int[] myRoutings = new int[RouterSimulator.NUM_NODES];

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

    // Copy start costs for node to our table
    System.arraycopy(costs, 0, this.myNbr, 0, RouterSimulator.NUM_NODES);
    System.arraycopy(costs, 0, this.costs[myID], 0, RouterSimulator.NUM_NODES);
    
    for( int i = 0; i < RouterSimulator.NUM_NODES; i++){
	if(myNbr[i] == RouterSimulator.INFINITY)
	    myRoutings[i] = RouterSimulator.INFINITY;
	else
	    myRoutings[i] = i;
    }

    sendAllUpdate();
    
  }

    public boolean updateCosts(){
	boolean costChange = false;
	for( int target = 0; target < RouterSimulator.NUM_NODES; target++){
	    if(target == myID)
		continue;

	    int minRouteNbr = myRoutings[target];
	    int minCost = costs[myID][target];
	    for( int nbr = 0; nbr < RouterSimulator.NUM_NODES; nbr++ ){ // One Node (not this node)
		if(nbr == myID) 
		    continue;
		
		int costToTarget = this.myNbr[nbr]/*dist to nbr*/ + this.costs[nbr][target]/*dist from nbr to target*/;
		// check if nbr == myRoutings[target], .
		
		if( costToTarget < minCost || ( nbr == myRoutings[target] && costToTarget != costs[myID][target] )){
		    minRouteNbr = nbr;
		    minCost = costToTarget;
		}
	    }
	    if( minCost <= myNbr[target] && minCost != costs[myID][target]){
		costs[myID][target] = minCost;
		myRoutings[target] = minRouteNbr;
		costChange = true;
	    }		
		
	    if( minCost > myNbr[target] && minCost != costs[myID][target] ){
		costs[myID][target] = myNbr[target];
		myRoutings[target] = target;
		costChange = true;
	    }
	    
	}
	return costChange;
    }

    // Called by the simulator (executed) when a node receives an update from one of its neighbors
    public void recvUpdate(RouterPacket pkt) {

      if(pkt.destid != this.myID){
	  return;
      }

      System.arraycopy(pkt.mincost, 0, costs[pkt.sourceid], 0, RouterSimulator.NUM_NODES);

      if(updateCosts()){
	  sendAllUpdate();
      }
  }
  
    private void sendAllUpdate(){
	// Send nodes dist-vector to neighbors.
	for( int recieverID = 0 ; recieverID < RouterSimulator.NUM_NODES; ++recieverID ){
	    if( myID != recieverID && myNbr[recieverID] != RouterSimulator.INFINITY ){
		RouterPacket newPkt = new RouterPacket(this.myID, recieverID, this.costs[myID]);
		for( int route = 0; route < RouterSimulator.NUM_NODES; ++route){
		    if(myRoutings[route] == recieverID && route == recieverID)
			newPkt.mincost[route] = RouterSimulator.INFINITY;
		}
		sendUpdate(newPkt);
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
	  myGUI.println("\nCurrent table for " + myID +
			"  at time " + sim.getClocktime());
	  
	  
	  myGUI.println("Distancetable:");
	  myGUI.print(String.format( "%7s %4s", "Dst", "|"));
	  for(int xtitle = 0; xtitle < RouterSimulator.NUM_NODES; ++xtitle){
	      myGUI.print( String.format( "%6s", xtitle ) );
	  }
	  myGUI.println("\n--------------------------------------------------------");

	  for(int col = 0; col < RouterSimulator.NUM_NODES; col++){
	      if( col == myID ||  myNbr[col] == RouterSimulator.INFINITY ) continue;
	      myGUI.print( String.format( "%1s %3s %3s", "nbr" , col, "|" ) );
	      for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
		  myGUI.print( String.format( "%6s", this.costs[col][row] ) );
	      }
	      myGUI.println();
	  }

	  myGUI.println("Our distance vector and routes:");
	  myGUI.print(String.format( "%7s %4s", "Dst", "|"));
	  for(int xtitle = 0; xtitle < RouterSimulator.NUM_NODES; ++xtitle){
	      myGUI.print( String.format( "%6s", xtitle ) );
	  }
	  myGUI.println("\n--------------------------------------------------------");

	  myGUI.print( String.format( "%7s %3s", "Dist", "|" ) );
	  for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
	      myGUI.print( String.format( "%6s", this.myNbr[row] ) );
	  }
	  myGUI.println();
	   myGUI.print( String.format( "%7s %3s", "Cost", "|" ) );
	  for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
	      myGUI.print( String.format( "%6s", this.costs[myID][row] ) );
	  }
	  myGUI.println();
	  myGUI.print( String.format( "%7s %2s", "Route", "|" ) );
	  for(int row = 0; row < RouterSimulator.NUM_NODES; row++){
	      myGUI.print( String.format( "%6s", this.myRoutings[row] ) );
	  }
	  myGUI.println("\n");	 
  }

  // called by the simulator (executed) when the cost of link of a node is about to change
  public void updateLinkCost(int dest, int newcost) {
      myNbr[dest] = newcost;
      costs[myID][dest] = newcost;
      costs[dest][myID] = newcost;
      myRoutings[dest] = dest;
    
      if(updateCosts())
	  sendAllUpdate();
  }

}
