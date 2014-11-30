import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class Client {
	
	private String address;
	private int port;
	
	private IDGenerator gen = new IDGenerator();
	
	private List<Task> taskList = new ArrayList<Task>();
	
	private Socket clientSocket;
	private ObjectOutputStream output;
	
	public Client(String s, int p){
		
		address = s;
		port = p;
	}
	
	public void generateTasks(String filename) throws IOException{

		BufferedReader br = new  BufferedReader(new FileReader(filename));
		String line;
				
		while((line = br.readLine()) != null){
			String id = gen.generateID();
			taskList.add(new Task(id, line, "null"));
		}	
		br.close();
	}
	
	public void connect(){
		try{
			
     		ExecutorService service = Executors.newFixedThreadPool(1);
			service.execute(new ReceiveResult());
			
			clientSocket = new Socket(address, port);
			System.out.println("connection established!");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void disconnect(){
		try {
	        output.close();
	    	clientSocket.close();
	    	System.out.println("connection shutdown");
		} 
		catch (IOException e) {
			System.out.println(e);
		}	
		
	}	
	
	public void sendTasks(String filename) throws IOException, ClassNotFoundException, InterruptedException {
		
		generateTasks(filename);
		
		try{
			output = new ObjectOutputStream(clientSocket.getOutputStream());

			//System.out.println("Got outputStream");
			System.out.println("Now spawn a new thread to listenling from Front End");
			
			Iterator<Task> iter = taskList.iterator();
			Iterator<Task> iter2 = taskList.iterator();
			
			while(iter.hasNext()){
				output.writeObject(iter.next());
				System.out.println("I'm sending this task object, whose description is " + iter2.next().getTaskDesc());
			}
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("This is client running!");
		Client client = new Client(args[0], Integer.parseInt(args[1]));
		
		client.connect();
		client.sendTasks(args[2]);
		client.disconnect();	
	}
}
