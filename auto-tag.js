'use strict'; 
const AWS = require('aws-sdk');

exports.handler =  function(event, context, callback) {
    console.log("Received event: " + JSON.stringify(event, null, 2));

    var userName = extractUserName(event);
    console.log("Extracted username: " + userName);
};

exports.extractUserName = function(event) {
    var userName = null;

    if (event.userIdentity) {
        let type = event.userIdentity.type;
        if (type === "IAMUser") {
            userName = event.userIdentity.userName;
        } else if (type === "Root") {
            userName = "root";
        } else {
            let principalId = event.userIdentity.principalId;
            let tokens = principalId.split(":");
            if (tokens.length == 2) {
                userName = tokens[1];
            }
        }
    }

    return userName;
};

exports.extractEC2ResourceIds = function(event) {
    let instanceIds = [];

    if (event.responseElements && 
        event.responseElements.instancesSet && 
        event.responseElements.instancesSet.items) {
            let instances = event.responseElements.instancesSet.items;
            for (let instance of instances) {
                if (instance.instanceId) {
                    instanceIds.push(instance.instanceId);
                }
            }
    }

    return instanceIds;
};

exports.extractS3BucketName = function(event) {
    let bucketName = null;

    if (event.requestParameters) {
        bucketName = event.requestParameters;
    }

    return bucketName;
}