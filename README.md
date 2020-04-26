# AutoTag

This repository contains a [SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) template that creates a Lambda function triggered by CloudTrail events. 

The function tags all new EC2 instances and S3 buckets with a `Creator` tag.

## Prerequisites

CloudTrail must be activated for the region you are deploying to.

Install and configure the SAM Command Line Interface by the following the instructions [here](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html).

Install Python 3.8.

## Build & Deploy

Use the following steps to manually build and deploy the stack.

```bash
sam build
sam deploy --guided
```