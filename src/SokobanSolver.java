import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;


public class SokobanSolver {

	SokobanBoard board;
	
	public SokobanSolver(SokobanBoard sokobanBoard) {
		board = sokobanBoard;
	}

	public String getSolution() {
		
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
				}
			}
		}
		
		return "";
	}

	private boolean isWinningState(SokobanState state) {
		for (Coordinate box : state.boxLocations) {
			if (board.cells[box.row][box.col] != '.') {
				return false;
			}
		}
		return true;
	}

	private String buildSolution(SokobanState finalState) {
		// TODO Auto-generated method stub
		return null;
	}

}
