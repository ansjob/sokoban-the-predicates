import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class SokobanBoard {
	
	public static Set<Coordinate> goalPositions;
	public static Set<Coordinate> startBoxPositions;
	public static boolean[][] staticDead;
	public static HashMap<Coordinate, Coordinate> tunnels;
	public static char[][] cells;
	
	
	
	public final SokobanState startingState;
	
	
	
	public SokobanBoard(List<String> rows){
		
		Set<Coordinate> boxPositions = new HashSet<Coordinate>();
		Coordinate startingPosition = null;
		Set<Coordinate> goalPositions = new HashSet<Coordinate>();
		cells = new char[rows.size()][rows.get(0).length()];
		Coordinate.coordinates = new Coordinate[cells.length][cells[0].length];
		staticDead = new boolean[cells.length][cells[0].length];
		for (int i = 0; i < staticDead.length;i++){
			for(int j = 0; j < staticDead[0].length; j++){
				staticDead[i][j] = true;
				Coordinate.coordinates[i][j] = new Coordinate(i, j);
			}
		}
		int rowNum = 0;
		for (String row : rows) {
			int colNum = 0;
			for (char c : row.toCharArray()) {
				if (c == '@') { // Player
					startingPosition = Coordinate.get(rowNum, colNum);
					cells[rowNum][colNum] = ' ';
				}
				else if (c == '+') { // Player on goal
					startingPosition = Coordinate.get(rowNum, colNum);
					goalPositions.add(startingPosition);
					cells[rowNum][colNum] = '.';
				}
				else if (c == '$') { // Box
					boxPositions.add(Coordinate.get(rowNum, colNum));
					cells[rowNum][colNum] = ' ';
				}
				else if (c == '*') { // Box on goal
					Coordinate cor = Coordinate.get(rowNum, colNum);
					boxPositions.add(cor);
					cells[rowNum][colNum] = '.';
					goalPositions.add(cor);
				}
				else if (c == '.') { // Goal
					cells[rowNum][colNum] = c;
					
					goalPositions.add(Coordinate.get(rowNum, colNum));
				}
				else if (c == ' ') { // Empty space
					cells[rowNum][colNum] = c;
				}
				else if (c == '#') {
					cells[rowNum][colNum] = c;
				}
				else { // Spurious 
					throw new RuntimeException("Unknown character: " + c);
				}
				colNum++;
			}
			rowNum++;
		}
		
		SokobanBoard.goalPositions = goalPositions;
		SokobanBoard.startBoxPositions = boxPositions;
		findTunnels(startingPosition);
		

		
		
		for (Coordinate goal : goalPositions){
			Set<Coordinate> visited = new HashSet<Coordinate>();
			Queue<Coordinate> q = new LinkedList<Coordinate>();
			q.add(goal);
			visited.add(goal);
			staticDead[goal.row][goal.col] = false;
			while (!q.isEmpty()){
				Coordinate cur = q.remove();
				for(Coordinate neighbour : cur.getNeighbours()){
					if (!visited.contains(neighbour)  
							&& cells[neighbour.row][neighbour.col] != '#' 
							&& cur.canBePulledFromNoBoxes(neighbour))
					{
						staticDead[neighbour.row][neighbour.col] = false;
						visited.add(neighbour);
						q.add(neighbour);
					}
				}
			}
		}
		
		
		startingState = new SokobanState(boxPositions, startingPosition, null, null, false);

	}


	private void findTunnels(Coordinate startPos){
		
		Set<Coordinate> visited = new HashSet<Coordinate>();
		
		findTunnelsHelp(startPos, null, visited);
		
		
	}

	private void findTunnelsHelp(Coordinate cur, Coordinate tunnelStart, Set<Coordinate> visited) {
		if (visited.contains(cur)){
			return;
		}
		
		Coordinate nextTunnelCord = null;
		
		if (tunnelStart != null){
			if (cells[cur.left().row][cur.left().col] == '#' && cells[cur.right().row][cur.right().col] == '#'){
				nextTunnelCord = (tunnelStart.row < cur.row ? cur.down() : cur.up());
			}  else if (cells[cur.up().row][cur.up().col] == '#' && cells[cur.down().row][cur.down().col] == '#'){
				nextTunnelCord = (tunnelStart.col < cur.col ? cur.right() : cur.left());
			}
			
			if (cells[nextTunnelCord.row][nextTunnelCord.col] == '#'){
				// SLUTET PÅ TUNNELS
				tunnels.put(tunnelStart, cur);
				tunnels.put(cur, tunnelStart);
			}
		}
		
		
		
		visited.add(cur);
	}



	public Set<SokobanState> getReverseStartingStates() {
		Set<SokobanState> rs = new HashSet<SokobanState>();
		Set<Coordinate> neighbours = null;
		
		
		for (Coordinate box : goalPositions){
			neighbours = box.getNeighbours();
			for (Coordinate neigh: neighbours){
				if (cells[neigh.row][neigh.col] == '#' || !box.canBePulledFrom(neigh, goalPositions) ) continue;
				SokobanState state = new SokobanState(goalPositions, neigh, null, null, true);
				rs.add(state);
			}
		}
		
		return rs;
	}

}
