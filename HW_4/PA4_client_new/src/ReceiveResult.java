import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ReceiveResult implements Runnable{

	private ServerSocket serverSocket;
	private Socket receiveResultSocket;	
	private ObjectInputStream inStream;
	
	public ReceiveResult(){
		System.out.println("initialized a runnable receiveResult");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try{
			serverSocket = new ServerSocket(8888);
			receiveResultSocket = serverSocket.accept();
		}
		catch(IOException e){
			System.out.println(e);
		}

		try {
			
			inStream = new ObjectInputStream(receiveResultSocket.getInputStream());
			
			while(true){
				
				Task task = (Task)inStream.readObject();
				System.out.println("Received task " + task.getTaskID() + " , result is " + task.getTaskResult());
			}

		} 
		catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} 
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
}
