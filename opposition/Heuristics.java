

public class Heuristics {
	
	public static int evaluate(Board board, State state) {
		return minimumDistanceAndGoalNeighborsAndDepth(board, state);
	}
	
	public static int minimumDistanceAndGoalNeighborsAndDepth(Board board, State state) {
		
		int goalScore = 0;
		int minimumDistance = Integer.MAX_VALUE;
		
		for(Point box : state.getBoxPositions()){
			if(board.getGoalPositions().contains(box)) {
				int nAdjacentBlockingElements = 0;
				for(Point p : Point.neighborPoints(box)) {
					Element e = board.getLayout().get(p);
					if(e == Element.Wall || state.getBoxPositions().contains(p))
						nAdjacentBlockingElements++;
				}
				goalScore -= (1 + nAdjacentBlockingElements);
			}
			else {
				for(Point goal : board.getGoalPositions()) {
					int goalDistance = Math.abs(box.row - goal.row) + Math.abs(box.column - goal.column);
					minimumDistance = Math.min(minimumDistance, goalDistance);
				}
			}
		}
		return (int)(minimumDistance + 20 * goalScore + 1 * state.getDepth());
	}
	
	public static int totalDistance(Board board, State state) {
		int totalDistance = 0;
		for(Point box : state.getBoxPositions()){
			int minimumDistance = Integer.MAX_VALUE;
			for(Point goal : board.getGoalPositions()) {
				int distance  = Math.abs(box.row - goal.row) + Math.abs(box.column - goal.column);
				
				if(distance < minimumDistance) {
					minimumDistance = distance;
				}
			}
			totalDistance += minimumDistance;
		}
		
		return totalDistance;
	}
	
	public static int minimumDistance(Board board, State state) {
		
		int minimumDistance = Integer.MAX_VALUE;
		for(Point box : state.getBoxPositions()){
			if(!board.getGoalPositions().contains(box)) {
				for(Point goal : board.getGoalPositions()) {
					int goalDistance = Math.abs(box.row - goal.row) + Math.abs(box.column - goal.column);
					minimumDistance = Math.min(minimumDistance, goalDistance);
				}
			}
		}
		return (int)minimumDistance;
	}
	
	public static int zeroHeuristic(Board board, State state) {
		return 0;
	}

	public static int breathFirstHeuristic(Board board, State state) {
		breathFirstCounter++;
		return breathFirstCounter;
	}
	private static int breathFirstCounter = 0;
	

	public static int depthFirstHeuristic(Board board, State state) {
		depthFirstCounter--;
		return depthFirstCounter;
	}
	private static int depthFirstCounter = 0;
}
