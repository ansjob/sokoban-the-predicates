
public class Action {
	
	
	private Point playerPosition;
	private Direction direction;
	
	public Action(Point playerPosition, Direction direction) {
		this.playerPosition = playerPosition;
		this.direction = direction;
	}
	
	public Point getPlayerPosition() {
		return playerPosition;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public Point getNewBoxPosition(){
		return direction.getNewPosition(getNewPlayerPosition());
	}
	
	public Point getNewPlayerPosition(){
		return direction.getNewPosition(playerPosition);
	}
	
	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');
		sb.append(playerPosition.toString());
		sb.append(',');
		sb.append(direction.getAsciiCode());
		sb.append(')');
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return playerPosition.hashCode() + direction.getAsciiCode() * 10000;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null || !(o instanceof Action)){
			return false;
		}
		Action a = (Action) o;
		if(a.getDirection()==direction&&a.getPlayerPosition().equals(playerPosition)){
			return true;
		}
		return false;
	}
}
