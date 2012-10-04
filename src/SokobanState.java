import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class SokobanState implements Comparable<SokobanState> {
	
	
	public final Set<Coordinate> boxLocations;
	public final Set<Coordinate> reachableLocations;
	public final Coordinate currentPosition, pushOrPullFromPosition;
	public final int priority;
	public final SokobanState parent;
	public final boolean isReverse;
	private final int hashCode;
	
	
	public SokobanState(Set<Coordinate> boxLocations,
			 Coordinate currentPosition, Coordinate pushFromPosition, SokobanState parent, boolean isReverse) {
		this.boxLocations = boxLocations;
		this.currentPosition = currentPosition;
		this.pushOrPullFromPosition = pushFromPosition;
		this.isReverse = isReverse;
		this.parent = parent;
		this.reachableLocations = getReachablePositions();
		this.priority = evaluate();
		this.hashCode = computeHashCode();
	}

	
	public int computeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((boxLocations == null) ? 0 : boxLocations.hashCode());
		result = prime
				* result
				+ ((reachableLocations == null) ? 0 : reachableLocations
						.hashCode());
		return result;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	};

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SokobanState other = (SokobanState) obj;
		if (boxLocations == null) {
			if (other.boxLocations != null)
				return false;
		} else if (!boxLocations.equals(other.boxLocations))
			return false;
		if (reachableLocations == null) {
			if (other.reachableLocations != null)
				return false;
		} else if (!reachableLocations.equals(other.reachableLocations))
			return false;
		return true;
	}

	@Override
	public int compareTo(SokobanState o) {
		return o.priority - priority; // Higher priority first
	}
	
	public Set<SokobanState> getChildren() {
		Set<SokobanState> children = new HashSet<SokobanState>();
		for (Coordinate box : boxLocations) {
			for (Coordinate from : box.getNeighbours()) {
				if ((!isReverse && reachableLocations.contains(from) && box.isPushableFrom(from, boxLocations)) || (isReverse && reachableLocations.contains(from) && box.canBePulledFrom(from, boxLocations))) {
					Coordinate playerPosition = (isReverse ? from.push(box) : box), newBoxPosition = (isReverse ? from : box.push(from));
					Set<Coordinate> newBoxPositions = new HashSet<Coordinate>();
					newBoxPositions.addAll(boxLocations);
					newBoxPositions.remove(box);
					newBoxPositions.add(newBoxPosition);
					SokobanState child = new SokobanState(newBoxPositions, playerPosition, from, this, isReverse);
					if (!child.isDead())
						children.add(child);
				}
			}
		}
		return children;
	}	

	private int evaluate() {
		int val = 0;
		for (Coordinate goal : (isReverse ? SokobanBoard.startBoxPositions : SokobanBoard.goalPositions)) {
			if (boxLocations.contains(goal))
				val += 5;
		}

		val += 1*distanceValue();

		if(isReverse){
			return val;
		}
		
		for (Coordinate box : boxLocations) {
			for (Coordinate from : box.getNeighbours()) {
				if (reachableLocations.contains(from) && box.isPushableFrom(from, boxLocations)) {
					val++;
				}
			}
		}
		
		return val;
	}
	
	private int distanceValue(){
		int val = 0;
		
		for (Coordinate box : boxLocations){
			int dist = Integer.MAX_VALUE;
			for (Coordinate goal : (isReverse ? SokobanBoard.startBoxPositions : SokobanBoard.goalPositions)){
				dist = Math.min(dist, (int)Math.sqrt((box.col + goal.col)*(box.col + goal.col) + (box.row + goal.row)*(box.row + goal.row)));
			}
			val += dist;
		}
		
		return val;
	}

	private boolean isDead() {
		return false;
//		for (Coordinate box : boxLocations){
//			boolean up = boxLocations.contains(box.up());
//			boolean down = boxLocations.contains(box.down());
//			boolean left;
//			boolean right;
//			if (!up && ! down){
//				continue;
//			}
//				
//			
//			left = boxLocations.contains(box.left());
//			right = boxLocations.contains(box.right());
//			if(!left && !right){
//				continue;
//			}
//			
//			if(left){
//				if(up && boxLocations.contains(box.up().left())){
//					return true;
//				} 
//				if (down && boxLocations.contains(box.down().left())){
//					return true;
//				}
//					
//			}
//			if(right){
//				if (up && boxLocations.contains(box.up().right())){
//					return true;
//				}
//				if (down && boxLocations.contains(box.down().right()));
//			}
//		}
//		return false;
	}

	private Set<Coordinate> getReachablePositions() {
		Set<Coordinate> result = new HashSet<Coordinate>();
		Queue<Coordinate> q = new LinkedList<Coordinate>();
		q.add(currentPosition);
		result.add(currentPosition);
		while (!q.isEmpty()){
			Coordinate cur = q.remove();
			for (Coordinate neighbour : cur.getNeighbours()) {
				if (SokobanBoard.cells[neighbour.row][neighbour.col] != '#' 
						&& !boxLocations.contains(neighbour)
						&& !result.contains(neighbour))
				{
					result.add(neighbour);
					q.add(neighbour);
				}
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < SokobanBoard.cells.length; row++) {
			for (int col = 0; col < SokobanBoard.cells[0].length; ++col) {
				char c = SokobanBoard.cells[row][col];
				Coordinate currentPos = Coordinate.get(row, col);
				if (boxLocations.contains(currentPos)) {
					if (c == '.') {
						c = '*';
					}
					else {
						c = '$';
					}
				}
				if (currentPos.equals(this.currentPosition)) {
					if (c == '.') {
						c = '+';
					}
					else {
						c = '@';
					}
				}
//				if (c != '#' && SokobanBoard.staticDead[row][col]){
//					c = ',';
//				}
				sb.append(c);
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
	
}
