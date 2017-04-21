package org.alfresco.aws.lambda.autotag;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AutoTagProcessorTest
{
    @Test
    public void testUserExtraction()
    {
        // test event with IAM user
        CloudWatchEvent event = createRunInstancesEvent(true);
        AutoTagProcessor lambda = new AutoTagProcessor();
        String userName = lambda.extractUserName(event);
        assertTrue("Expected user name to be 'jdoe' but it was: " + userName, "jdoe".equals(userName));

        // test event with federated user
        event = createRunInstancesEvent(false);
        userName = lambda.extractUserName(event);
        assertTrue("Expected user name to be 'jdoe@example.com' but it was: " + userName, "jdoe@example.com".equals(userName));
    }


    @Test
    public void testEC2InstanceIdExtraction()
    {
        CloudWatchEvent event = createRunInstancesEvent(true);
        AutoTagProcessor lambda = new AutoTagProcessor();
        List<String> ids = lambda.extractEC2ResourceIds(event);
        assertNotNull("Expected to get a List object", ids);
        assertFalse("Expected list to have one entry but it has: " + ids.size(), ids.isEmpty());
        String instanceId = ids.get(0);
        assertTrue("Expected instance id to be 'i-01234567890' but it was: " + instanceId, "i-01234567890".equals(instanceId));
    }

    @Test
    public void testBucketNameExtraction()
    {
        CloudWatchEvent event = createCreateBucketEvent(true);
        AutoTagProcessor lambda = new AutoTagProcessor();
        String bucketName = lambda.extractS3BucketName(event);
        assertTrue("Expected bucket name to be 'test-bucket' but it was: " + bucketName, "test-bucket".equals(bucketName));
    }

    private CloudWatchEvent createRunInstancesEvent(boolean iamUser)
    {
        // create the user map
        Map<String, Object> user = null;
        if (iamUser)
        {
            user = createIamUser();
        }
        else
        {
            user = createFederatedUser();
        }

        // create the detail map
        Map<String, Object> detail = new HashMap<>();
        detail.put("eventVersion", "1.05");
        detail.put("eventTime", "2017-04-18T14:20:45Z");
        detail.put("eventSource", "ec2.amazonaws.com");
        detail.put("eventName", "RunInstances");
        detail.put("awsRegion", "eu-central-1");
        detail.put("userIdentity", user);

        Map<String, Object> ec2Instance = new HashMap<>();
        ec2Instance.put("instanceId", "i-01234567890");
        ec2Instance.put("imageId", "ami-5b06d634");
        ec2Instance.put("privateDnsName", "ip-172-31-2-19.eu-central-1.compute.internal");
        ec2Instance.put("vpcId", "vpc-4bad4b22");

        List<Map<String,Object>> items = new ArrayList<>();
        items.add(ec2Instance);
        Map<String, Object> instancesSet = new HashMap<>();
        instancesSet.put("items", items);

        Map<String, Object> responseElements = new HashMap<>();
        responseElements.put("requestId", "fbc25774-0721-4314-a01d-ded9e42eaa2c");
        responseElements.put("instancesSet", instancesSet);

        detail.put("responseElements", responseElements);

        // create the event object
        CloudWatchEvent event = new CloudWatchEvent();
        event.setAccount("123456789000");
        event.setVersion("0");
        event.setId("3e76771f-4472-42f1-8a22-f0ddb8a57460");
        event.setSource("aws.ec2");
        event.setRegion("eu-central-1");
        event.setTime("2017-04-18T14:20:45Z");
        event.setResources(new ArrayList());
        event.setDetail(detail);

        return event;
    }

    private CloudWatchEvent createCreateBucketEvent(boolean iamUser)
    {
        // create the user map
        Map<String, Object> user = null;
        if (iamUser)
        {
            user = createIamUser();
        }
        else
        {
            user = createFederatedUser();
        }

        // create the detail map
        Map<String, Object> detail = new HashMap<>();
        detail.put("eventVersion", "1.04");
        detail.put("eventTime", "2017-04-18T14:20:45Z");
        detail.put("eventSource", "s3.amazonaws.com");
        detail.put("eventName", "CreateBucket");
        detail.put("awsRegion", "eu-central-1");
        detail.put("userIdentity", user);

        Map<String, Object> bucketConfiguration = new HashMap<>();
        bucketConfiguration.put("LocationConstraint", "eu-central-1");
        bucketConfiguration.put("xmlns", "http://s3.amazonaws.com/doc/2006-03-01/");

        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put("CreateBucketConfiguration", bucketConfiguration);
        requestParameters.put("bucketName", "test-bucket");

        detail.put("requestParameters", requestParameters);

        // create the event object
        CloudWatchEvent event = new CloudWatchEvent();
        event.setAccount("123456789000");
        event.setVersion("0");
        event.setId("3e76771f-4472-42f1-8a22-f0ddb8a57460");
        event.setSource("aws.s3");
        event.setRegion("eu-central-1");
        event.setTime("2017-04-18T14:20:45Z");
        event.setResources(new ArrayList());
        event.setDetail(detail);

        return event;
    }

    private Map<String, Object> createIamUser()
    {
        Map<String, Object> user = new HashMap<>();

        user.put("type", "IAMUser");
        user.put("principalId", "AAAAAAAAAAAAAAAAAAAAA");
        user.put("arn", "arn:aws:iam::123456789000:user/jdoe");
        user.put("accountId", "123456789000");
        user.put("accessKeyId", "BBBBBBBBBBBBBBBBBBBB");
        user.put("userName", "jdoe");
        user.put("invokedBy", "signin.amazonaws.com");

        Map<String, Object> sessionContext = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("mfaAuthenticated", "false");
        attributes.put("creationDate", "2017-04-18T09:18:51Z");
        sessionContext.put("sessionContext", attributes);

        user.put("sessionContext", sessionContext);

        return user;
    }

    private Map<String, Object> createFederatedUser()
    {
        Map<String, Object> user = new HashMap<>();

        user.put("type", "AssumedRole");
        user.put("principalId", "AAAAAAAAAAAAAAAAAAAAA:jdoe@example.com");
        user.put("arn", "arn:aws:iam::123456789000:assumed-role/RoleName/jdoe@example.com");
        user.put("accountId", "123456789000");
        user.put("accessKeyId", "BBBBBBBBBBBBBBBBBBBB");

        Map<String, Object> sessionContext = new HashMap<>();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("mfaAuthenticated", "false");
        attributes.put("creationDate", "2017-04-18T09:18:51Z");
        sessionContext.put("sessionContext", attributes);

        Map<String, Object> sessionIssuer = new HashMap<>();
        sessionIssuer.put("type", "Role");
        sessionIssuer.put("principalId", "AAAAAAAAAAAAAAAAAAAAA");
        sessionIssuer.put("arn", "arn:aws:iam::123456789000:assumed-role/RoleName");
        sessionIssuer.put("accountId", "123456789000");
        sessionIssuer.put("userName", "RoleName");
        sessionContext.put("sessionIssuer", sessionIssuer);

        user.put("sessionContext", sessionContext);

        return user;
    }
}
