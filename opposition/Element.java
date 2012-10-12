
public enum Element {
	Empty(' '),
	DeadEnd('x'),
	Wall('#'),
	Goal('.');
	private char asciiCode;
	private Element(char ascii){
		asciiCode = ascii;
	}

	public char getAsciiCode(){
		return asciiCode;
	}
}