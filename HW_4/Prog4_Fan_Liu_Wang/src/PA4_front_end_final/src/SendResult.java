import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

public class SendResult implements Runnable{
	private int clientPortNumber;
	private InetAddress clientAddress;
	
	private ExecutorCompletionService<Task> ecs;
	
	private Socket socket;
	private ObjectOutputStream output;
	
	public SendResult(int p, InetAddress addr, ExecutorCompletionService<Task> e) {
		clientPortNumber = p;
		clientAddress = addr;
		ecs = e;
		
		System.out.println("initialized sending thread!");
	}

	@Override
	public void run() {
		try{
			socket = new Socket(clientAddress, clientPortNumber);
			System.out.println("connection to send result established!");
		}catch(IOException e){
			System.out.println(e);
		}
		
		try{
			output = new ObjectOutputStream(socket.getOutputStream());
		}catch(IOException e){
			System.out.println(e);
		}
		
		while(true){
			try {
				Task task = ecs.take().get();
				output.writeObject(task);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getCause());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
