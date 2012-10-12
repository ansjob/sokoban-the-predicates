

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileGraph {
	
	TileNode playerPosition;
	
	public TileGraph(Matrix<Tile> serverMatrix){
		TileNode[][] tmpGraph = new TileNode[serverMatrix.getHeight()][];

		for(int row = 0;row<serverMatrix.getHeight(); row++){
			tmpGraph[row] = new TileNode[serverMatrix.getWidth()];
			for(int column = 0;column<serverMatrix.getWidth(); column++){

				switch(serverMatrix.get(row, column)){
					case Player:
					case PlayerOnGoal:
						playerPosition = new TileNode(new Point(row, column), false);
						tmpGraph[row][column] = playerPosition;
						break;
					case Empty:
					case Goal:
						tmpGraph[row][column] = new TileNode(new Point(row, column), false);
						break;
					case BoxOnGoal:
					case Box:
						tmpGraph[row][column] = new TileNode(new Point(row, column), true);
						break;
					default:
						break;
				}	
			}
		}
		
		for(int row = 0;row<serverMatrix.getHeight()-1; row++){
			for(int column = 0;column<serverMatrix.getWidth()-1; column++){
				if(tmpGraph[row][column]!=null){
					tmpGraph[row][column].setEastNeighbour(tmpGraph[row][column+1]);
					tmpGraph[row][column].setSouthNeighbour(tmpGraph[row+1][column]);
					//System.out.println("e" + tmpGraph[row][column] + " " + tmpGraph[row][column+1]);
					//System.out.println("s" + tmpGraph[row][column] + " " + tmpGraph[row+1][column]);
					
				}

			}
		}
		
		int row = serverMatrix.getHeight()-1;
				
		for(int column = 0;column<serverMatrix.getWidth()-1; column++){
			if(tmpGraph[row][column]!=null){
				tmpGraph[row][column].setEastNeighbour(tmpGraph[row][column+1]);
			}
		}
		
		int column = serverMatrix.getWidth()-1;
		
		for(row = 0;row<serverMatrix.getHeight()-1; row++){
			if(tmpGraph[row][column]!=null){
				tmpGraph[row][column].setSouthNeighbour(tmpGraph[row+1][column]);
			}
		}
		
	}
	
	public List<Point> getPath(List<Action> actions){
		
		LinkedList<Point> path = new LinkedList<Point>();
		
		TileNode playerNode = playerPosition;
		Iterator<Action> goals = actions.iterator();
		while(goals.hasNext()){
			Action goalAction = goals.next();

			/*Add all nodes from player postioni to where teh action is performed*/
			LinkedList<TileNode> nodes = search(playerNode, goalAction.getPlayerPosition());

			for(TileNode n : nodes){
				path.addLast(n.index);
			}
			
			/*Get the new player postion and mark the boxs new postion as blocked*/
			switch(goalAction.getDirection()){
				case Up:
					//path.addLast(nodes.getLast().northNeighbour.index);
					playerNode = nodes.getLast().northNeighbour;
					playerNode.northNeighbour.blocked = true;
					break;
				case Down:
					playerNode = nodes.getLast().southNeighbour;
					playerNode.southNeighbour.blocked = true;
					break;
				case Right:
					playerNode = nodes.getLast().eastNeighbour;
					playerNode.eastNeighbour.blocked = true;
					break;
				case Left:
					playerNode = nodes.getLast().westNeighbour;
					playerNode.westNeighbour.blocked = true;
					break;
			}	
			/*Don't add the last player position since we will get that in the next iteration*/
			
			playerNode.blocked = false;
			
		
		}
		/*Add the last player position since we didn't get it form the last iteration*/
		path.addLast(playerNode.index);	

		return path;
	}
	
	private LinkedList<TileNode> search(TileNode from, Point to){
		LinkedList<TileNode> queue = new LinkedList<TileNode>();
		LinkedList<TileNode> path = new LinkedList<TileNode>();
		HashSet<TileNode> visited = new HashSet<TileNode>();
		HashMap<TileNode, TileNode> nodeEnteredFrom = new HashMap<TileNode, TileNode>();
		
		queue.addFirst(from);
		visited.add(from);
		
		while(!queue.isEmpty()){
			TileNode currentNode = queue.removeFirst();
			if(currentNode.index.equals(to)){
				
				path.addFirst(currentNode);
				while(nodeEnteredFrom.containsKey(currentNode)){
					TileNode parent = nodeEnteredFrom.get(currentNode);
					path.addFirst(parent);
					currentNode = parent;
				}
				
				return path;
			}
			
			TileNode[] neighbours = {currentNode.northNeighbour,currentNode.southNeighbour,currentNode.eastNeighbour,currentNode.westNeighbour};

			for(TileNode n : neighbours){

				if(n!=null&&!visited.contains(n)&&!n.blocked){
					
					visited.add(n);
					queue.addLast(n);
					if(!nodeEnteredFrom.containsKey(n)){
						nodeEnteredFrom.put(n, currentNode);
					}
				}
			}
		}
		return null;
	}
	
	private class TileNode{
		public Point index;
		public boolean blocked;
		TileNode northNeighbour;
		TileNode westNeighbour;
		TileNode eastNeighbour;
		TileNode southNeighbour;
		
		public TileNode(Point index, boolean blocked){
			this.blocked = blocked;
			this.index = index;
		}
		
		/*
		public void setNorthNeighbour(TileNode northNeighbour){
			this.northNeighbour = northNeighbour;
			if(northNeighbour!=null) northNeighbour.southNeighbour = this;
		}*/
		
		public void setSouthNeighbour(TileNode southNeighbour){
			this.southNeighbour = southNeighbour;
			if(southNeighbour!=null) southNeighbour.northNeighbour = this;
		}
		public void setEastNeighbour(TileNode eastNeighbour){
			this.eastNeighbour = eastNeighbour;
			if(eastNeighbour!=null) eastNeighbour.westNeighbour = this;
		}
		
		@Override
		public String toString(){
			return index.toString();
		}
		
		/*
		public void setWestNeighbour(TileNode westNeighbour){
			this.westNeighbour = westNeighbour;
			if(westNeighbour!=null) westNeighbour.eastNeighbour = this;
		}
		
		public TileNode getNorthNeighbour(){
			return northNeighbour;
		}
		
		public TileNode getSouthNeighbour(){
			return southNeighbour;
		}
		public TileNode getEastNeighbour(){
			return eastNeighbour;
		}
		
		public TileNode getWestNeighbour(){
			return westNeighbour;
		}
		*/
	}
}
