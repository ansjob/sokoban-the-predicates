import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

	public static void main(String[] pArgs) 
	{
		if(pArgs.length<3)
		{
			System.out.println("usage: java Client host port boardnum");
			return;
		}
	
		try
		{
			Socket lSocket=new Socket(pArgs[0],Integer.parseInt(pArgs[1]));
			PrintWriter lOut=new PrintWriter(lSocket.getOutputStream());
			BufferedReader lIn=new BufferedReader(new InputStreamReader(lSocket.getInputStream()));
	
            lOut.println(pArgs[2]);
            lOut.flush();

            String lLine=lIn.readLine();

            //read number of rows
            int lNumRows=Integer.parseInt(lLine);

            List<String> rows = new ArrayList<String>(lNumRows);
            
            //read each row
            
            Utils.DEBUG(1, "Recieved board: (#%s)\n", pArgs[2]);
            for(int i=0;i<lNumRows;i++)
            {
                lLine=lIn.readLine();
                rows.add(lLine);
                Utils.DEBUG(1, "%s\n", lLine);
            }
            
            long startTime = System.currentTimeMillis();
            
            SokobanSolver solver = new SokobanSolver(new SokobanBoard(rows));        
            String solution = solver.getForwardReverseSolution();
            long timeElapsed = System.currentTimeMillis() - startTime;
            Utils.DEBUG(1, "Our solution: %s \n(took %d ms)\n", solution, timeElapsed);

            //send the solution to the server
            lOut.println(solution);
            lOut.flush();
    
            //read answer from the server
            lLine=lIn.readLine();
    
            System.out.println(lLine);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
