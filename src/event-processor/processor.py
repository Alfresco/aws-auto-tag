# import json
import logging
import os

import boto3

# setup logging
_logger = logging.getLogger()
if os.getenv('LOG_LEVEL') == 'DEBUG':
    _logger.setLevel(logging.DEBUG)
    logging.getLogger('boto3').setLevel(logging.WARN)
    logging.getLogger('botocore').setLevel(logging.WARN)
    logging.getLogger('urllib3').setLevel(logging.WARN)
else:
    _logger.setLevel(logging.INFO)


# setup required clients
_ec2 = boto3.client('ec2')


def lambda_handler(event, context):
    '''Lambda entry point'''

    _logger.debug('Received event: %s', event)

    # return empty object
    return {}


def extract_user_name(event):
    '''Extracts the user name from the event'''

    return ''


def is_ec2_event(event):
    '''Determines whether the given event was from EC2'''

    return False


def is_s3_event(event):
    '''Determines whether the given event was from S3'''

    return False


def is_rds_event(event):
    '''Determines whether the given event was from RDS'''

    return False


def extract_bucket_name(event):
    '''Extracts the S3 bucket name from the event'''

    return ''


def extract_ec2_instance_ids(event):
    '''Extracts the EC2 instance IDs from the event'''

    return ''
