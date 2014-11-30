import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.*;

public class Frontend_remote {
	
	private String requestQueue = "https://sqs.us-east-1.amazonaws.com/749229355397/RequestQueue";
	private String resultQueue = "https://sqs.us-east-1.amazonaws.com/749229355397/ResultQueue";
	private SQS_Handler queueHandler = new SQS_Handler(requestQueue, resultQueue);
	
	private int clientPortNumber = 8888;
	private InetAddress clientAddress; 
	
	private int portNumber;
	
	private ServerSocket frontEndServerSocket;
	private Socket frontEndSocket;
	private Socket sendResultSocket;
	
	private ObjectInputStream input;
	private ObjectOutputStream output;

	////////////////////////////////////////////////////////////////////////////////
	
	public void setPortNumber(int n){
		
		portNumber = n;
	}

	public void startServing() throws ClassNotFoundException, ExecutionException, InterruptedException{
		
		try{
			frontEndServerSocket = new ServerSocket(portNumber);
			System.out.println("Start serving!");
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		try{
			frontEndSocket = frontEndServerSocket.accept();
			System.out.println("connection established!");
			clientAddress = frontEndSocket.getInetAddress();
			
			//create a new socket to connect to client, and create an output stream
			sendResultSocket = new Socket(clientAddress, clientPortNumber);
			//System.out.println("connection to send result established!");
			output = new ObjectOutputStream(sendResultSocket.getOutputStream());

			
			
			//periodically call a runnable
			ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
			service.scheduleAtFixedRate(new SendResultPeriodically(output, queueHandler), 1000, 1000, TimeUnit.MILLISECONDS);
			
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		
		try{
			input = new ObjectInputStream(frontEndSocket.getInputStream());
			
			while(true){
				
				Task task = (Task)input.readObject();
				System.out.println("recieve task: " + task);
				queueHandler.sendTaskInQueue(task);
			}
		}
		catch(IOException e){
			System.out.println("Object reading completed.");
		}		
		
		try {
	        input.close();
	    	frontEndSocket.close();
	    	frontEndServerSocket.close();
		} 
		catch (IOException e) {
			System.out.println(e);
		}

	}
	
	public static void main(String[] args) throws Exception{
		
		Frontend_remote fnd = new Frontend_remote();
		
		fnd.setPortNumber(Integer.parseInt(args[0]));
		
		fnd.startServing();
	}	
}