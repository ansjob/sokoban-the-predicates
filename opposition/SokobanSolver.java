

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class SokobanSolver {
	
	/*Orders states so that the one with less cost is smaller*/
	private static final Comparator<State> costComparator = new Comparator<State>(){

		@Override
		/*NOTE: If used in a set collection two states with the same cost will overwrite each other because the cost is the same and they will be treated as equal*/
		public int compare(State stateOne, State stateTwo) {
			int value = stateOne.getDistance()-stateTwo.getDistance();

			return value;	
		}		
	};
	
	public static String findSolution(Matrix<Tile> tileMatrix) {
		
		Board board = new Board(tileMatrix);
		State rootState = new State(tileMatrix, board);
		
		List<Action> actions = solve(board, rootState);
		
		return createSolutionString(actions, tileMatrix);
	}
	
	public static List<Action>solve(Board board, State rootState) {
		PriorityQueue<State> queue = new PriorityQueue<State>(10, costComparator);
		HashSet<State> visited = new HashSet<State>();
		
		queue.add(rootState);
		
		//check if root state is solution
		if(rootState.getBoxPositions().equals(board.getGoalPositions())){
			return new LinkedList<Action>();
		}
		
		while(!queue.isEmpty()){
			State currentState = queue.poll();
			//System.out.println(currentState.getDistance());
			for(Action a : currentState.getActions()){
				
				State nextState = new State(currentState, a, board);
				
				if(visited.add(nextState)){
					if(nextState.getBoxPositions().equals(board.getGoalPositions())){
						return backtrack(nextState);
					}
					queue.add(nextState);
				}
			}
		}
		/*
		Pseudo code:
		
		solve(board, rootState):
	 		q.add(rootState, 0)
	 
	 		while(!q.isEmpty()):
	   			currentState = q.next()
	   			visisted.add(currentState)
	   
	   			for action in currentState.actions:
	     			nextState = new State(currentState, action, board)
	     
	     			if nextState.boxPositions.equals(board.goalPositions):
	       				return backtrack(nextState)
	
	     			if !visited.contains(nextState):
	       				q.add(nextState, nextState.cost)
	 
	 		return null
		 */
		
		return null;
	}
	
	private static List<Action> backtrack(State endState) {
		State currentState = endState;
		LinkedList<Action> actions = new LinkedList<Action>();
		
		while(currentState.getParentState() != null && currentState.getSourceAction() != null) {
			actions.addFirst(currentState.getSourceAction());
			currentState = currentState.getParentState();
		}
		
		return actions;
	}
	
	public static String createSolutionString(List<Action> actions, Matrix<Tile> tileMatrix) {
		
		if(actions == null){ // actions are null :(
			return null;
		}
		
		TileGraph graph = new TileGraph(tileMatrix);
		List<Point> path = graph.getPath(actions);
		
		if(path.size()==0){
			//maybe all boxes are allready in place, perhaps shuold perform a check for this earlie in the pgrogram though
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		Iterator<Point> positions = path.iterator();
		Point previousPosition = positions.next();
		
		while(positions.hasNext()){
			Point newPosition = positions.next();
			sb.append(Direction.getDirection(previousPosition, newPosition).getAsciiCode());
			previousPosition = newPosition;
		}
		
		return sb.toString();
	}
}
