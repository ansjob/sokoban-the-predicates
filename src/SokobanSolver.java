import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class SokobanSolver {

	SokobanBoard board;
	int reverseFrequency;
	
	public SokobanSolver(SokobanBoard sokobanBoard) {
		board = sokobanBoard;
	}
	
	public String getForwardReverseSolution(){
		
		PriorityQueue<SokobanState> forwardQ = new PriorityQueue<SokobanState>();
		HashMap<SokobanState, SokobanState> forwardQueuedStates = new HashMap<SokobanState, SokobanState>();
		
		reverseFrequency = 8;
		
		PriorityQueue<SokobanState> reverseQ = new PriorityQueue<SokobanState>();
		HashMap<SokobanState, SokobanState> reverseQueuedStates = new HashMap<SokobanState, SokobanState>();
		
		forwardQ.add(board.startingState);
		forwardQueuedStates.put(board.startingState, board.startingState);
		
		
		
		Set<SokobanState> reveseStart = board.getReverseStartingStates();
		reverseQ.addAll(reveseStart);
		for (SokobanState state : reveseStart){
			reverseQueuedStates.put(state, state);
		}
		
		
		int reverse = 0;
		int forwardCounter = 0, backwardsCounter = 0;
		
		while(!forwardQ.isEmpty() || !reverseQ.isEmpty()){
			
			SokobanState currentState = (reverse != 0 ? reverseQ.remove(): forwardQ.remove());
			
			Set<SokobanState> children = currentState.getChildren();
			if (reverse != 0)
				backwardsCounter++;
			else
				forwardCounter++;

			for (SokobanState child : children) {	
				if (reverse != 0) {
					if (forwardQueuedStates.containsKey(child)){ // VINST	
						Utils.DEBUG(0, "Köade %d states i framåtsökningen och %d states i bakåtsökningen\n ", 
								forwardQueuedStates.size(), reverseQueuedStates.size());
						return buildReverseForwardSolution(forwardQueuedStates.get(child), child);
					}
					
					if (!reverseQueuedStates.containsKey(child)) {
						reverseQ.add(child);
						reverseQueuedStates.put(child, child);
					}
				} else {
					if (reverseQueuedStates.containsKey(child)){ // VINST
						
						Utils.DEBUG(0, "Köade %d states i framåtsökningen och %d states i bakåtsökningen\n ", 
								forwardCounter, backwardsCounter);
						return buildReverseForwardSolution(child, reverseQueuedStates.get(child));
					}
					
					if (!forwardQueuedStates.containsKey(child)){
						forwardQ.add(child);
						forwardQueuedStates.put(child, child);
					}
				}
			}
			reverse = (reverse +1)%reverseFrequency;
		}

		
		
		
		return "NO SOLUTION FOUND";
	}


	public String getSolution() {
		
		Utils.DEBUG(2, "Trying to find a solution to this board:\n%s \n\n", board.startingState);
		
		PriorityQueue<SokobanState> q = new PriorityQueue<SokobanState>();
		Set<SokobanState> queuedStates = new HashSet<SokobanState>();
		q.add(board.startingState);
		queuedStates.add(board.startingState);
		
		while (!q.isEmpty()) {
			SokobanState currentState = q.remove();
			
			Set<SokobanState> children = currentState.getChildren();
			for (SokobanState child : children) {
				if (isWinningState(child)) {
					return buildSolution(child);
				}
				if (!queuedStates.contains(child)) {
					queuedStates.add(child);
					q.add(child);
					Utils.DEBUG(10, "Köar:\n%s\n\n", child);
				}
			}
		}
		
		return "NO SOLUTION FOUND";
	}

	private boolean isWinningState(SokobanState state) {
		for (Coordinate box : state.boxLocations) {
			if (!SokobanBoard.goalPositions.contains(box)) {
				return false;
			}
		}
		return true;
	}
	
	private String buildReverseForwardSolution(SokobanState forwardState, SokobanState backwardState) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildSolution(forwardState));
		
		int distance[][] = new int[SokobanBoard.cells.length][SokobanBoard.cells[0].length];
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance[0].length; j++)
				distance[i][j] = -1;
		}
		
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		Set<Coordinate> queued = new HashSet<Coordinate>();
		q.add(forwardState.currentPosition);
		queued.add(forwardState.currentPosition);
		distance[forwardState.currentPosition.row][forwardState.currentPosition.col] = 0;
		
		while (!q.isEmpty()) {
			Coordinate curPos = q.remove();
			for (Coordinate neighbour : curPos.getNeighbours()) {
				if (!queued.contains(neighbour) && !forwardState.boxLocations.contains(neighbour) && SokobanBoard.cells[neighbour.row][neighbour.col] != '#') {
					distance[neighbour.row][neighbour.col] = distance[curPos.row][curPos.col] + 1;
					q.add(neighbour);
					queued.add(neighbour);
					if (neighbour.equals(backwardState.currentPosition)) {
						/* Goal reached. */
						q.clear();
						break;
					}
				}
			}
		}
		
		StringBuilder reverseSolution = new StringBuilder();
		Coordinate currentPos = backwardState.currentPosition;
		while (distance[currentPos.row][currentPos.col] > 0) {
			for (Coordinate neighbour : currentPos.getNeighbours()) {
				if (distance[neighbour.row][neighbour.col] == distance[currentPos.row][currentPos.col] -1) {
					reverseSolution.append(currentPos.getMoveChar(neighbour));
					currentPos = neighbour;
					break;
				}
			}
		}
		sb.append(reverseSolution.reverse().toString());
		
		sb.append(buildReverseSolution(backwardState));
		return sb.toString();
	}
	
	private String buildReverseSolution(SokobanState startingState){
		StringBuilder sb = new StringBuilder();
		SokobanState currentState = startingState;
		while(currentState.parent != null){
			sb.append(getTransitionReverse(currentState, currentState.parent));
			currentState = currentState.parent;
		}
		return sb.toString();
		
	}
	
	private String getTransitionReverse(SokobanState from, SokobanState to){
		
		int distance[][] = new int[SokobanBoard.cells.length][SokobanBoard.cells[0].length];
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance[0].length; j++)
				distance[i][j] = -1;
		}
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		Set<Coordinate> queued = new HashSet<Coordinate>();
		q.add(from.pushOrPullFromPosition);
		queued.add(from.pushOrPullFromPosition);
		distance[from.pushOrPullFromPosition.row][from.pushOrPullFromPosition.col] = 0;
		
		while (!q.isEmpty()) {
			Coordinate curPos = q.remove();
			for (Coordinate neighbour : curPos.getNeighbours()) {
				if (!queued.contains(neighbour) && !to.boxLocations.contains(neighbour) && SokobanBoard.cells[neighbour.row][neighbour.col] != '#') {
					distance[neighbour.row][neighbour.col] = distance[curPos.row][curPos.col] + 1;
					q.add(neighbour);
					queued.add(neighbour);
					if (neighbour.equals(to.currentPosition)) {
						/* Goal reached. */
						q.clear();
						break;
					}
				}
			}
		}
		
		StringBuilder reverseSol = new StringBuilder();
		
		Coordinate currentPos = null;
		currentPos = to.currentPosition;
		
		while (distance[currentPos.row][currentPos.col] > 0) {
			for (Coordinate neighbour : currentPos.getNeighbours()) {
				if (distance[neighbour.row][neighbour.col] == distance[currentPos.row][currentPos.col] -1) {
					reverseSol.append(currentPos.getMoveChar(neighbour));
					currentPos = neighbour;
					break;
				}
			}
		}
		reverseSol.append(from.pushOrPullFromPosition.getMoveChar(from.currentPosition));
				
		return reverseSol.reverse().toString();
	}

	private String buildSolution(SokobanState finalState) {
		Stack<String> partials = new Stack<String>();
		SokobanState currentState = finalState;
		while (currentState.parent != null) {
			partials.push(getTransition(currentState.parent, currentState));
			Utils.DEBUG(3, "%s\n\n", currentState);
			currentState = currentState.parent;
		}
		StringBuilder sb = new StringBuilder();
		while (!partials.isEmpty()) {
			sb.append(partials.pop());
		}
		return sb.toString();
	}

	private String getTransition(SokobanState from, SokobanState to) {
		int distance[][] = new int[SokobanBoard.cells.length][SokobanBoard.cells[0].length];
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance[0].length; j++)
				distance[i][j] = -1;
		}
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		Set<Coordinate> queued = new HashSet<Coordinate>();
		q.add(from.currentPosition);
		queued.add(from.currentPosition);
		distance[from.currentPosition.row][from.currentPosition.col] = 0;
		
		while (!q.isEmpty()) {
			Coordinate curPos = q.remove();
			for (Coordinate neighbour : curPos.getNeighbours()) {
				if (!queued.contains(neighbour) && !from.boxLocations.contains(neighbour) && SokobanBoard.cells[neighbour.row][neighbour.col] != '#') {
					distance[neighbour.row][neighbour.col] = distance[curPos.row][curPos.col] + 1;
					q.add(neighbour);
					queued.add(neighbour);
					if (neighbour.equals(to.pushOrPullFromPosition)) {
						/* Goal reached. */
						q.clear();
						break;
					}
				}
			}
		}
		
		StringBuilder reverseSolution = new StringBuilder();
		Coordinate currentPos = to.pushOrPullFromPosition;
		reverseSolution.append(to.currentPosition.getMoveChar(to.pushOrPullFromPosition, true));
		while (distance[currentPos.row][currentPos.col] > 0) {
			for (Coordinate neighbour : currentPos.getNeighbours()) {
				if (distance[neighbour.row][neighbour.col] == distance[currentPos.row][currentPos.col] -1) {
					reverseSolution.append(currentPos.getMoveChar(neighbour));
					currentPos = neighbour;
					break;
				}
			}
		}
		
		return reverseSolution.reverse().toString();
	}

}
