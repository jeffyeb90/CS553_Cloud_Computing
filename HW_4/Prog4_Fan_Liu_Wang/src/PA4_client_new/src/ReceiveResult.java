import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReceiveResult implements Runnable{
	private long beginTime;
	private long endTime;
	private int ntasks;
	private int nfakes;
	
	public ReceiveResult(long t, int ntasks) {
		beginTime = t;
		this.ntasks = ntasks;
	}
	
	@Override
	public void run() {
		try (
			ServerSocket serverSocket = new ServerSocket(8888);
			Socket receiveResultSocket = serverSocket.accept();
			ObjectInputStream inStream =
				new ObjectInputStream(receiveResultSocket.getInputStream());
		) {
	    	//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			int received = 0;
			nfakes = 0;
			while (received < ntasks) {
				Task task = (Task)inStream.readObject();
				if (!task.getTaskID().equals("fake")) {
					received++;
					System.out.println(received + "[" + task.getTaskID() + ", "
						+ task.getTaskDesc() + ", " + task.getTaskResult() + "]");
				} else {
					nfakes++;
				}
			}
			endTime = Calendar.getInstance().getTimeInMillis();
			System.out.println("All tasks finished in " + (endTime - beginTime)
					+ "ms. (nfakes: " + nfakes + ")");
		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
}
