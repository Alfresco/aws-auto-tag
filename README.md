# AutoTag

This repository contains a CloudFormation template that creates a Lambda function 
that is triggered by a CloudWatch rule listening for CloudTrail create events in the region.

Currently new EC2 instances and S3 buckets are tracked and tagged.  

## Prerequisites

Install and configure the AWS Command Line Interface by the following the instructions [here](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-set-up.html).

CloudTrail must be actived for the region you are deploying to.

CloudWatch Events must support the EC2 and S3 services in the region you are deploying to. 

## Build & Deploy

Use the following steps to build and deploy the stack.

    mvn package
    deploy [bucket-name] [stack-name]

where **bucket-name** is the name of an existing S3 bucket to use for deployment and **stack-name** is the name to give the created CloudFormation stack.
