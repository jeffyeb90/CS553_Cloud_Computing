package iit.cs553.flw.prog4;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class DB_Handler {
	private static AmazonDynamoDBClient
		db = (new AmazonDynamoDBClient(AWSCreds.WANG)).withRegion(Regions.US_WEST_2);
	private static String tableName = "TaskTable";

	public boolean SucceedPutItem(Task task) {
		
		//if (Tables.doesTableExist(db, "TaskTable")) {
		//System.out.println("Table is already ACTIVE"); } else{
		//System.out.println("doesn't exist"); }
		

		boolean flag;

		Map<String, AttributeValue>
			item = new HashMap<String, AttributeValue>();
		item.put("TaskID", new AttributeValue(task.getTaskID()));

		Map<String, ExpectedAttributeValue>
			expected = new HashMap<String, ExpectedAttributeValue>();
		expected.put("TaskID", new ExpectedAttributeValue(false));

		PutItemRequest r = new PutItemRequest();

		r.setTableName(tableName);
		r.setItem(item);
		r.setExpected(expected);

		try {
			db.putItem(r);
			flag = true;
			//System.out.println("insert succeed");
		} catch (ConditionalCheckFailedException e) {
			flag = false;
			System.out.println("DB insert failed");
		}

		return flag;
	}
}
