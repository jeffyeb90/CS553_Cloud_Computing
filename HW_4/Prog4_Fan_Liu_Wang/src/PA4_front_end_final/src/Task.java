
import java.io.Serializable;

public class Task implements Serializable {
	private static final long serialVersionUID = 4313678225059169361L;

	private String TaskID;
	private String TaskDesc;
	private String Result;

	public Task(String tid, String des, String res) {
		TaskID = tid;
		TaskDesc = des;
		Result = res;
	}
	
	public Task withID(String id) { TaskID = id; return this; }
	public Task withDesc(String desc) { TaskDesc = desc; return this; }
	public Task withResult(String r) { Result = r; return this; }
	
	public String getTaskID() { return TaskID; }
	public String getTaskDesc() { return TaskDesc; }
	public String getTaskResult() { return Result; }
	
	public String toString() {
		return "[" + TaskID + ", " + TaskDesc + ", " + Result + "]";
	}
}