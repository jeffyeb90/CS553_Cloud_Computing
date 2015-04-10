import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQS_Handler {
    private static AmazonSQSClient
    	sqs = (new AmazonSQSClient(AWSCreds.WANG)).withRegion(Regions.US_WEST_2);
    private final static String TaskQName = "Tasks";
	private final static String ResultQName = "Results";

	private String outQueueURL; 
	private String inQueueURL;
	
	public SQS_Handler() {
		outQueueURL = sqs.getQueueUrl(TaskQName).getQueueUrl();
		inQueueURL = sqs.getQueueUrl(ResultQName).getQueueUrl();
	}
	
	public void sendTaskInQueue(Task task){
		Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		
		messageAttributes.put("TaskID", new MessageAttributeValue().withDataType("String").withStringValue(task.getTaskID()));
		messageAttributes.put("Description", new MessageAttributeValue().withDataType("String").withStringValue(task.getTaskDesc()));
		messageAttributes.put("Result", new MessageAttributeValue().withDataType("String").withStringValue(task.getTaskResult()));
		
		SendMessageRequest smRequest = new SendMessageRequest();
		smRequest.withQueueUrl(outQueueURL);
		smRequest.setMessageAttributes(messageAttributes);
		smRequest.setMessageBody("Hi");
		
		sqs.sendMessage(smRequest);
	}
	
	public List<Task> readTasksFromQueue(){
		List<String> messageAttributeNames = new ArrayList<String>();
		
		messageAttributeNames.add("TaskID");
		messageAttributeNames.add("Description");
		messageAttributeNames.add("Result");
		
		ReceiveMessageRequest rmRequest = new ReceiveMessageRequest();
		rmRequest.setQueueUrl(inQueueURL);
		rmRequest.setMessageAttributeNames(messageAttributeNames);
		
		ReceiveMessageResult rmResult = sqs.receiveMessage(rmRequest);
		
		List<Message> m_list = rmResult.getMessages();
		
		List<Task> taskList = new ArrayList<Task>();
		
		for (Message m : m_list){
			Map<String, MessageAttributeValue> messageAttributes = m.getMessageAttributes();
			
     		String id = messageAttributes.get("TaskID").getStringValue();
			String des = messageAttributes.get("Description").getStringValue();
			String res = messageAttributes.get("Result").getStringValue();
			
			taskList.add(new Task(id, des, res));
			
			String receiptHandler = m.getReceiptHandle();
			DeleteMessageRequest dmRequest = new DeleteMessageRequest(inQueueURL, receiptHandler);
			
			sqs.deleteMessage(dmRequest);
		}
		
		return taskList;
	}
}


