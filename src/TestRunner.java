
public class TestRunner {
	public static void main(String[] args) throws InterruptedException {
		for (int i = 1 ; i <= 100; ++i) {
			
			Thread thread = new Thread(new TestRunnable(i));
			
			thread.run();
			thread.join(60000);
			if (thread.isAlive()) {
				Utils.DEBUG(0, "Timed out");
				thread.interrupt();
			}
		}
	}
	
	private static class TestRunnable implements Runnable {
		
		int puzzle;
		public TestRunnable(int puzzleNo) {
			puzzle = puzzleNo;
		}
		
		
		@Override
		public void run() {
			Client.main(new String[] {"dd2380.csc.kth.se", "5032", "" + puzzle});		
		}
		
	}
}
