
import java.util.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQS_Handler {
	
	private static AWSCredentials credential = new BasicAWSCredentials("AKIAIVYUUZTRDTFH6KCQ", "x6/mAB84aGSsejsP0+HXrILx0w0aYGxrNaxnzw3N");
	private static AmazonSQS sqs = new AmazonSQSClient(credential);

	private String outQueueURL; 
	private String inQueueURL;
	
	public SQS_Handler(String out, String in){
		
		outQueueURL = out;
		inQueueURL = in;
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


