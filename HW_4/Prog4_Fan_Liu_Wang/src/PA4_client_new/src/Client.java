import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class Client {
	private static long beginTime;
	
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
			service.execute(new ReceiveResult(beginTime, taskList.size()));
			
			clientSocket = new Socket(address, port);
			System.out.println("connection established!");
		} catch (IOException e){
			System.out.println(e);
		}
	}
	
	public void disconnect(){
		try {
	        output.close();
	    	clientSocket.close();
	    	System.out.println("connection shutdown");
		} catch (IOException e) {
			System.out.println(e);
		}	
	}	
	
	public void sendTasks()
		throws IOException, ClassNotFoundException, InterruptedException
	{
		try{
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			
			Iterator<Task> iter = taskList.iterator();
			
			Calendar cal = Calendar.getInstance();
	    	cal.getTime();
	    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    	System.out.println( sdf.format(cal.getTime()) );
	    	
			while(iter.hasNext()){
				output.writeObject(iter.next());
			}
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		beginTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("This is client running!");
		Client client = new Client(args[0], Integer.parseInt(args[1]));
		
		client.generateTasks(args[2]);
		client.connect();
		client.sendTasks();
		client.disconnect();
	}
}
