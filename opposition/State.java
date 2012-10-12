

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class State {
	
	private HashSet<Point> boxPositions = new HashSet<Point>();
	private HashSet<Action> actions = new HashSet<Action>();
	
	private State parentState;
	private Action sourceAction;
	
	private int distance;
	
	private int depth;
	
	
	public State(Matrix<Tile> tileMatrix, Board board) {
		Point playerPosition = null;
		
		for(int row = 0;row<tileMatrix.getHeight(); row++){
			for(int column = 0;column<tileMatrix.getWidth(); column++){
				Point p = new Point(row, column);
				Tile tile = tileMatrix.get(p);
				if(tile == Tile.Box || tile == Tile.BoxOnGoal) boxPositions.add(p);
				else if(tile == Tile.Player || tile == Tile.PlayerOnGoal) playerPosition = p;
			}
		}
		
		updateActions(playerPosition, board);
		removeIvalidActions(board);
		depth = 0;
		
		distance = Heuristics.evaluate(board, this);
	}

	
	public State(State parentState, Action parentAction, Board board) {
		Point playerPosition = parentAction.getNewPlayerPosition();
		Point newBoxPosition = parentAction.getNewBoxPosition();
		boxPositions.addAll(parentState.getBoxPositions());
		
		boxPositions.remove(playerPosition);
		boxPositions.add(newBoxPosition);
		
		updateActions(playerPosition, board);
		removeIvalidActions(board);
		this.parentState = parentState;
		this.sourceAction = parentAction;
		depth = parentState.depth + 1;
		
		distance = Heuristics.evaluate(board, this);
	}

	private void removeIvalidActions(Board board) {
		
		Iterator<Action> iter = actions.iterator();
		List<Action> deadEnds = new ArrayList<Action>();
		
		while(iter.hasNext()){
			Action a = iter.next();
			if(board.at(a.getNewBoxPosition())==Element.DeadEnd|| //Remove deadends marked on board
					(board.at(a.getNewBoxPosition())==Element.Wall||boxPositions.contains(a.getNewBoxPosition())) //Remove collison with walls and boxes
					||isDeadEnd(a.getNewBoxPosition(), a.getNewPlayerPosition(),board)){//Remove deadends caused by boxes
				deadEnds.add(a);
			}
		}
		actions.removeAll(deadEnds);
	}
	
	private boolean isDeadEnd(Point newBoxPosition, Point oldBoxPosition, Board board){
		
		Point up = Direction.Up.getNewPosition(newBoxPosition);
		Point down = Direction.Down.getNewPosition(newBoxPosition);
		Point right = Direction.Right.getNewPosition(newBoxPosition);
		Point left = Direction.Left.getNewPosition(newBoxPosition);
		Point upRight = Direction.Right.getNewPosition(up);
		Point upLeft = Direction.Left.getNewPosition(up);
		Point downRight = Direction.Right.getNewPosition(down);
		Point downLeft = Direction.Left.getNewPosition(down);
		
		//Track which points are blocked so we later can check if they are boxes on goals or if tehy are boxes that are not on goals
		Set<Point> blockingPoints = new HashSet<Point>();
		
		
		if(tileIsBlocked(up, board)&&!oldBoxPosition.equals(up)){

			if(tileIsBlocked(right, board)&&!oldBoxPosition.equals(right)){
				if(tileIsBlocked(upRight, board)&&!oldBoxPosition.equals(upRight)){
					blockingPoints.add(up);
					blockingPoints.add(right);
					blockingPoints.add(upRight);
				}
			} 
			if(tileIsBlocked(left, board)&&!oldBoxPosition.equals(left)){
				if(tileIsBlocked(upLeft, board)&&!oldBoxPosition.equals(upLeft)){
					blockingPoints.add(up);
					blockingPoints.add(left);
					blockingPoints.add(upLeft);
				}
			}
		} 
		if(tileIsBlocked(down,board)&&!oldBoxPosition.equals(down)){
			if(tileIsBlocked(right, board)&&!oldBoxPosition.equals(right)){
				if(tileIsBlocked(downRight, board)&&!oldBoxPosition.equals(downRight)){
					blockingPoints.add(down);
					blockingPoints.add(right);
					blockingPoints.add(downRight);
				}
			} 
			if(tileIsBlocked(left, board)&&!oldBoxPosition.equals(left)){
				if(tileIsBlocked(downLeft, board)&&!oldBoxPosition.equals(downLeft)){
					blockingPoints.add(down);
					blockingPoints.add(left);
					blockingPoints.add(downLeft);
				}
			}
		}
			
		if(board.at(newBoxPosition)==Element.Goal){
			for(Point blockedTile : blockingPoints){
				if(board.at(blockedTile)!=Element.Wall&&board.at(blockedTile)!=Element.Goal){
					return true;
				}
			}
			return false;
		}
		
		if(blockingPoints.size()!=0){
			return true;
		}
		return false;
	}
	
	private boolean tileIsBlocked(Point tile, Board board){
		return board.tileIsUnreachable(tile)||boxPositions.contains(tile);
	}
	
	private void updateActions(Point playerPosition, Board board){
		
		HashMap<Point,List<Point>> boxNeighbours = new HashMap<Point,List<Point>>(); //search goals
		LinkedList<Point> queue = new LinkedList<Point>();
		HashSet<Point> visited = new HashSet<Point>();
		
		/*We add all positions which are next to a box and also is walkable*/
		for(Point b : boxPositions){
			
			for(Point neighbour : board.neighbours(b)){
				//Only add positions where the player can actually stand
				if(walkableTile(board,neighbour)){
					//Two boxes may have the same neighbour so we don't want to add it twice
					if(!boxNeighbours.containsKey(neighbour)){
						boxNeighbours.put(neighbour, new LinkedList<Point>());
					}
					//we keep track of which boxes are neighbour to which points so we later can calculate the dirction
					boxNeighbours.get(neighbour).add(b);
				}
			}
		}
		
		queue.add(playerPosition);
		
		/*Do a simple bfs to all neighbours and once we detect one create an action and store*/
		while(!queue.isEmpty()&&boxNeighbours.size()>0){
			Point currentTile = queue.removeFirst();
			
			if(boxNeighbours.containsKey(currentTile)){
				for(Point box : boxNeighbours.get(currentTile)){
					actions.add(new Action(currentTile, Direction.getDirection(currentTile, box)));
				}
				boxNeighbours.remove(currentTile);
			}
			
			if(!visited.contains(currentTile)){
				visited.add(currentTile);
				for(Point neighbour : board.neighbours(currentTile)){
					if(walkableTile(board,neighbour)&&!boxPositions.contains(neighbour)){
						queue.addLast(neighbour);
					}
				}
			}
		}
	}
	
	private boolean walkableTile(Board board, Point tile){
		return board.at(tile)!=Element.Wall&&!boxPositions.contains(board);
	}
	
	public HashSet<Point> getBoxPositions() {
		return boxPositions;
	}
	
	public HashSet<Action> getActions() {
		return actions;
	}
	
	public int getDistance() {
		return distance;
	}

	public Action getSourceAction() {
		return sourceAction;
	}

	public State getParentState() {
		return parentState;
	}
	
	public int getDepth() {
		return depth;
	}

	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Boxes: " );
		for(Point p : boxPositions){
			sb.append(p.toString());
		}
		sb.append('\n');
		
		sb.append("Actions: " );
		for(Action a : actions){
			sb.append(a.toString());
		}
		sb.append('\n');
		
		return null;
	}
	
	public boolean equals(Object o){ 
		if(o == null || !(o instanceof State)) return false;
		
		State otherState = (State) o;
		
		if(otherState.getActions().size() != actions.size() || otherState.getBoxPositions().size() != boxPositions.size()) {
			return false;
		}

		return boxPositions.equals(otherState.getBoxPositions()) && actions.equals(otherState.getActions());
	}
	
	public int hashCode() {
		return boxPositions.hashCode() + actions.hashCode();
	}
}
