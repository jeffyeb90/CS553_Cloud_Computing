import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;


public class Frontend {
	
	private ExecutorService executor;
	private ExecutorCompletionService<Task> ecs;
	
	private int portNumber;
	
	private int clientPortNumber = 8888;
	private InetAddress clientAddress; 
			
	private ServerSocket frontEndServerSocket;
	private Socket frontEndSocket;
	
	private ObjectInputStream input;
	/////////////////////////////////////////////////////////////
	
	public void createThreadPool(int n){
		executor = Executors.newFixedThreadPool(n);
	}
	
	public void shutdownThreadPool(){
	
		executor.shutdown();
		while(!executor.isTerminated()){
			;
		}
		System.out.println("Finished all threads");	
	}
	
	public void setPortNumber(int n){
		portNumber = n;
	}

	//////////////////////////////////////////////////////////////
	
	public void startServing() throws ClassNotFoundException, ExecutionException, InterruptedException{
		
		ecs = new ExecutorCompletionService<Task>(executor);
		
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
			
			(new Thread(new SendResult(clientPortNumber, clientAddress,ecs))).start();
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		try{
			input = new ObjectInputStream(frontEndSocket.getInputStream());
			
			while(true){
				
				Task task = (Task)input.readObject();
				System.out.println("recieve task: " + task);
				ecs.submit(new LocalWorker(task));
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
}