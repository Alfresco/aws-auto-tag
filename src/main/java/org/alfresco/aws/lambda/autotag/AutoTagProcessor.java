package org.alfresco.aws.lambda.autotag;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.SetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.TagSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class AutoTagProcessor implements RequestHandler<CloudWatchEvent, String>
{
    private static final String TAG_KEY_OWNER = "Owner";

    private static final String EVENTNAME_RUN_INSTANCES = "RunInstances";
    private static final String EVENTNAME_CREATE_BUCKET = "CreateBucket";

    public String handleRequest(CloudWatchEvent event, Context context)
    {
        context.getLogger().log("Received event: " + event);

        // ensure there is a detail object
        Map<String, Object> eventDetail = event.getDetail();

        if (eventDetail != null)
        {
            String eventName = (String)eventDetail.get("eventName");
            context.getLogger().log("Event name: " + eventName);

            // extract the username from the event
            String userName = extractUserName(event);
            context.getLogger().log("Extracted user name: " + userName);

            if (EVENTNAME_RUN_INSTANCES.equals(eventName))
            {
                Map<String, Object> responseElements = (Map<String, Object>)eventDetail.get("responseElements");
                if (responseElements != null)
                {
                    // extract the list
                    List<String> ids = extractEC2ResourceIds(event);
                    context.getLogger().log("Extracted instance ids: " + ids);

                    if (ids != null && !ids.isEmpty())
                    {
                        tagEC2Resources(ids, userName);
                        context.getLogger().log("Successfully tagged EC2 instances: " + ids);
                    }
                }
            }
            else if (EVENTNAME_CREATE_BUCKET.equals(eventName))
            {
                // extract the bucket name
                String bucketName = extractS3BucketName(event);
                context.getLogger().log("Extracted S3 bucket name: " + bucketName);

                if (bucketName != null)
                {
                    if (tagS3Bucket(bucketName, userName))
                    {
                        context.getLogger().log("Successfully tagged S3 bucket: " + bucketName);
                    }
                    else
                    {
                        context.getLogger().log("Skipped tagging bucket '" + bucketName + "' as it could not be found");
                    }
                }
            }
            else
            {
                context.getLogger().log("WARNING - Unsupported action: " + eventName);
            }
        }

        return "Event processing completed";
    }

    protected String extractUserName(CloudWatchEvent event)
    {
        String userName = null;

        if (event != null)
        {
            Map<String, Object> detail = event.getDetail();
            if (detail != null)
            {
                Map<String, Object> userIdentity = (Map<String, Object>)detail.get("userIdentity");
                if (userIdentity != null)
                {
                    String type = (String)userIdentity.get("type");
                    if ("IAMUser".equals(type))
                    {
                        userName = (String)userIdentity.get("userName");
                    }
                    else
                    {
                        // federated user, get the username from the principalId
                        String principalId = (String)userIdentity.get("principalId");
                        StringTokenizer tokenizer = new StringTokenizer(principalId, ":");
                        String principal = tokenizer.nextToken();
                        userName = tokenizer.nextToken();
                    }
                }
            }
        }

        return userName;
    }

    protected List<String> extractEC2ResourceIds(CloudWatchEvent event)
    {
        List<String> ids = null;

        if (event != null)
        {
            Map<String, Object> detail = event.getDetail();
            if (detail != null)
            {
                Map<String, Object> responseElements = (Map<String, Object>)detail.get("responseElements");
                if (responseElements != null)
                {
                    Map<String, Object> instancesSet = (Map<String, Object>)responseElements.get("instancesSet");
                    List<Map<String, Object>> items = (List<Map<String, Object>>)instancesSet.get("items");
                    if (items != null)
                    {
                        ids = new ArrayList<>();
                        for (Map<String, Object> item : items)
                        {
                            String instanceId = (String)item.get("instanceId");
                            if (instanceId != null)
                            {
                                ids.add(instanceId);
                            }
                        }
                    }
                }
            }
        }

        return ids;
    }

    protected String extractS3BucketName(CloudWatchEvent event)
    {
        String bucketName = null;

        if (event != null)
        {
            Map<String, Object> detail = event.getDetail();
            if (detail != null)
            {
                Map<String, Object> requestParameters = (Map<String, Object>)detail.get("requestParameters");
                if (requestParameters != null)
                {
                    bucketName = (String)requestParameters.get("bucketName");
                }
            }
        }

        return bucketName;
    }

    protected void tagEC2Resources(List<String> resourceIds, String userName)
    {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        Tag tag = new Tag().withKey(TAG_KEY_OWNER).withValue(userName);
        CreateTagsRequest createTagsRequest = new CreateTagsRequest().withResources(resourceIds).withTags(tag);
        ec2.createTags(createTagsRequest);
    }

    protected boolean tagS3Bucket(String bucketName, String userName)
    {
        boolean tagged = false;

        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

        // check bucket still exists (automated tests can create and delete them quickly!)
        if (s3.doesBucketExist(bucketName))
        {
            TagSet ownerTagSet = new TagSet();
            ownerTagSet.setTag(TAG_KEY_OWNER, userName);
            BucketTaggingConfiguration taggingConfiguration = new BucketTaggingConfiguration().withTagSets(ownerTagSet);
            s3.setBucketTaggingConfiguration(bucketName, taggingConfiguration);
            tagged = true;
        }

        return tagged;
    }
}