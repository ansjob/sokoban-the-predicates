

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Board {
	
	private HashSet<Point> goalPositions = new HashSet<Point>();
	
	private Matrix<Element> layout;
	private HashMap<Point, List<Point>> neighbourLists;
	
	public Board(Matrix<Tile> tileMatrix) {
		
		layout = new Matrix<Element>();
		neighbourLists =  new HashMap<Point,List<Point>>();
		for(int row = 0; row < tileMatrix.getHeight(); row++){
			for(int column = 0;column < tileMatrix.getWidth(); column++) {
				Point p = new Point(row, column);
				
				switch(tileMatrix.get(p)) {
					case BoxOnGoal:
					case PlayerOnGoal:
					case Goal:
						layout.set(p, Element.Goal); 
						goalPositions.add(p);
						break;
					case Wall: layout.set(p, Element.Wall); break;
					default: layout.set(p, Element.Empty);
				}
				
			}
		}
		
		for(int row = 0; row < tileMatrix.getHeight(); row++){
			for(int column = 0;column < tileMatrix.getWidth(); column++) {
				Point p = new Point(row, column);
				List<Point> nList = calculateNeighbours(p);
				neighbourLists.put(p, nList);
			}
		}
		
		/*Dead end detection should perhaps make use of the boxes initial position and the players position.
		 * This would make a more complex deadend detection algorithm but would also yield better result
		 * Examples below.
		 * First board we can't make the deadends at the top since we have to push a box there and we start inside the deadend
		 *     ###            ###
			   #@#            #x#
			   #$#            # #
			#### ####      #### ####
			#. $   .#  =>  #.     .#
			#### ## #      #### ## #
			   # ## #         # ## #
			   # ##$#         # ## #
			   #.   #         #.  x#
			   ######         ######
			Second board we can make the tunnel filled with deadends since if we push a box in there it ain't comming out. If we assune every board is solvable we simply need to make sure we don't classify a tunnel as deadend if a box is inside it.
			   ###           ###
			   #@#           #x#
			   # #           #x#
			#### ####     ####x####
			#. $   .#     #.     .#
			####$## #  => #### ## #
			   # ## #        # ## #
			   # ##$#        # ## #
			   #.   #        #.  x#
			   ######        ######
		Doing this will be better but will not be optimal since a box can still be pushed into a deadend with no way of reaching it once the box has been pushed out of the deadend.
	     */
		detectDeadEnds();
	}
	
	public ArrayList<Point> getDeadEndPositions() {
		ArrayList<Point> deadEnds = new ArrayList<Point>();
		for(int row = 0; row < layout.getHeight(); row++) {
			for(int column = 0; column < layout.getWidth(); column++) {
				if(layout.get(row, column) == Element.DeadEnd) {
					deadEnds.add(new Point(row, column));
				}
			}
		}
		return deadEnds;
	}
	
	private List<Direction> openDirections(Point tile){
		List<Direction> openDirs = new LinkedList<Direction>();
		for(Direction dir : Direction.values()){
			Point neighbour = dir.getNewPosition(tile);
			if(!tileIsUnreachable(neighbour)){
				openDirs.add(dir);
			}
		}
		return openDirs;
	}
	
	/*
	 * One flaw with the current implementation is that it does not consider if a position is reachable for a player, but to implement this would make the whole thing much more complex
	 */
	private void detectDeadEnds() {
	//	List<Point> corners = new LinkedList<Point>();
		HashMap<Point,List<Direction>> corners = new HashMap<Point,List<Direction>>();
		
		for(int row = 0;row<layout.getHeight(); row++){
			for(int column = 0;column<layout.getWidth(); column++){
				switch(layout.get(row, column)){
					case Empty: //We only want corners that doesn't lead to goals to be deadends
						if(isCorner(row, column)){
							layout.set(row, column, Element.DeadEnd);
							corners.put(new Point(row,column),openDirections(new Point(row,column)));
							//corners.add(new Point(row,column));
						}
					default:
						break;
				}
			}
		}
		
		//Expand deads using the corners as basis to detect new deadends
		for(Point corner : corners.keySet()){
			List<Direction> direction = corners.get(corner);
			
			for(Direction openDirection : direction){
				
				Point position = openDirection.getNewPosition(corner);
				Direction left = openDirection.getDirectionLeftOf();
				Direction right = openDirection.getDirectionRightOf();
				List<Point> deadEndCandidates = new LinkedList<Point>();
				
				while(withinBounds(position)&&layout.get(position)==Element.Empty){ //Continue until we run into a deadend or wall
					Point leftPoint = left.getNewPosition(position);
					Point rightPoint = right.getNewPosition(position);
					
					if((tileIsUnreachable(leftPoint)||tileIsUnreachable(rightPoint))||//If new position is next to a wall (on left/right side) it's a deadend since we can't push ini the left(right direction
							(layout.get(leftPoint)==Element.DeadEnd&&layout.get(rightPoint)==Element.DeadEnd)){//if deadends on both sides we can't push the box to either side 
						deadEndCandidates.add(position);
					} else {
						break;
					}
										
					position = openDirection.getNewPosition(position);
				}
				if(withinBounds(position)) {
					if(layout.get(position)==Element.DeadEnd){
						for(Point deadEnd : deadEndCandidates){
							layout.set(deadEnd, Element.DeadEnd);
						}
					}
				}
			}
		}
		
		/*NOTE:Realised method below is stupid, since we won't be able to push a box into the center of four deadends unless it starts there, in which case we're doomed*/
		/*Finally, if a tile is surrounded by deadends, it's classified as a deadend. Run this iteration until no deadends are detected.
		 * Note that tiles like:       only like this will be detected:
		 * xxxx                                      xxx    xxx
		 * x  x                                      x x => xxx
		 * xxxx                                      xxx    xxx
		 * won't be detected since you can loop around between the two free tiles. This should be solved/avoided with loop detection in the main algorithm (i.e. check if we've allready been in a state with a visisted hashset)
		 * This may be a bit overkill and simply makes writing testcases harder :/ But it's easier to remove than writting it again (even though doing that is trivial)
		 * */
		/*
		int newDeadendCount;
		do{
			newDeadendCount = 0;
			for(int row = 0;row<layout.length; row++){
		
				for(int column = 0;column<layout[row].length; column++){
					switchStmt : switch(layout[row][column]){
						case Empty: //We only want corners that doesn't lead to goals to be deadends
							for(Point neighbour : neighbours(row, column)){
								System.out.println(layout[neighbour.row][neighbour.column]);
								if(layout[neighbour.row][neighbour.column]!=Element.DeadEnd){
									break switchStmt;
								}
							}
							layout[row][column]=Element.DeadEnd;
							newDeadendCount++;
						default:
							break;
					}
				}
			}
			
		} while(newDeadendCount!=0);*/
	}

	private boolean isCorner(int row, int column){
		Point up = Direction.Up.getNewPosition(row,column);
		Point down = Direction.Down.getNewPosition(row,column);
		Point right = Direction.Right.getNewPosition(row,column);
		Point left = Direction.Left.getNewPosition(row,column);
		
		if(tileIsUnreachable(up)){
			if(tileIsUnreachable(right)||tileIsUnreachable(left)){
				return true;
			}
		} else if(tileIsUnreachable(down)){
			if(tileIsUnreachable(right)||tileIsUnreachable(left)){
				return true;
			}
		}
		
		return false;
	}
	
	public Matrix<Element> getLayout() {
		return layout;
	}
	
	public boolean tileIsUnreachable(Point tile){
		return !withinBounds(tile)||layout.get(tile)==Element.Wall;
	}
	
	public boolean withinBounds(Point point){
		return withinBounds(point.row,point.column);
	}
	
	public boolean withinBounds(int height, int width){
		return layout.withinBounds(new Point(height, width));
	}
	
	public List<Point> neighbours(Point target){
		return neighbourLists.get(target);
		//return neighbours(target.row, target.column);
	}
	
	private List<Point> calculateNeighbours(Point target){
		//meh, kinda ugly, perhaps store all the neighbours in a list in the board to avoid having to recalulate it all the time and only do it at initialization?
		ArrayList<Point> neighbours = new ArrayList<Point>(2);
		int row = target.row;
		int column = target.column;
		int neighbourRow = row - 1;
		int neighbourColumn= column;
		
		if(withinBounds(neighbourRow,neighbourColumn)){
			neighbours.add(new Point(neighbourRow, neighbourColumn));
		}
		
		neighbourRow = row + 1;

		if(withinBounds(neighbourRow,neighbourColumn)){
			neighbours.add(new Point(neighbourRow, neighbourColumn));
		}
		
		neighbourRow = row;
		neighbourColumn= column - 1;
		
		if(withinBounds(neighbourRow,neighbourColumn)){
			neighbours.add(new Point(neighbourRow, neighbourColumn));
		}
		
		neighbourColumn= column + 1;
		if(withinBounds(neighbourRow,neighbourColumn)){
			neighbours.add(new Point(neighbourRow, neighbourColumn));
		}
		
		return neighbours;
	}

	public HashSet<Point>getGoalPositions() {
		return goalPositions;
	}
	
	
	public Element at(Point boardPosition){
		return layout.get(boardPosition);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int row = 0;row<layout.getHeight();row++){
			for(int column = 0;column<layout.getWidth();column++){
				sb.append(layout.get(row, column).getAsciiCode());
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
