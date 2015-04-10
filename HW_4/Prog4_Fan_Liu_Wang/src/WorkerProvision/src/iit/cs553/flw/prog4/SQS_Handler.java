package iit.cs553.flw.prog4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public SQS_Handler() {
		outQueueURL = sqs.getQueueUrl(ResultQName).getQueueUrl();
		inQueueURL = sqs.getQueueUrl(TaskQName).getQueueUrl();
	}
	
	public static void main(String[] args) {
		SQS_Handler q = new SQS_Handler();
		System.out.println("taskq length: " + q.approxTaskQLen());
	}
	
	public int approxTaskQLen() {
		String r = sqs.getQueueAttributes(inQueueURL, Arrays.asList(QAttr_NMsg))
					.getAttributes().get(QAttr_NMsg);
		
		return Integer.valueOf(r);
	}

    private String outQueueURL; 
    private String inQueueURL;

    private static AmazonSQSClient
    	sqs = (new AmazonSQSClient(AWSCreds.WANG)).withRegion(Regions.US_WEST_2);
    private final static String TaskQName = "Tasks";
	private final static String ResultQName = "Results";
	private final static String QAttr_NMsg = "ApproximateNumberOfMessages";
}
