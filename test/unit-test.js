let assert = require("assert");
let autoTagFunction = require("../auto-tag.js");
let fs=require('fs');

describe("extractUserName function", function() {
    it("should return null if data is missing", function() {
        let event = {};
        let user = autoTagFunction.extractUserName(event);
        assert.strictEqual(user, null);
    });

    it("should extract IAM user", function() {
        let event = JSON.parse(fs.readFileSync('test/event-ec2-iam-user.json', 'utf8'));
        let iamUser = autoTagFunction.extractUserName(event);
        assert.strictEqual(iamUser, "joe-bloggs");
    });
    
    it("should extract federated user", function() {
        let event = JSON.parse(fs.readFileSync('test/event-ec2-federated-user.json', 'utf8'));
        let federatedUser = autoTagFunction.extractUserName(event);
        assert.strictEqual(federatedUser, "joe-bloggs@alfresco.com");
    });

    it("should extract root user", function() {
        let event = JSON.parse(fs.readFileSync('test/event-ec2-root-user.json', 'utf8'));
        let rootUser = autoTagFunction.extractUserName(event);
        assert.strictEqual(rootUser, "root");
    });
});

describe("extractInstanceIds function", function() {
    it("should return empty array if data is missing", function() {
        let event = {};
        let instanceIds = autoTagFunction.extractEC2ResourceIds(event);
        assert.strictEqual(0, instanceIds.length);
    });

    it("should extract single instance ID", function() {
        let event = JSON.parse(fs.readFileSync('test/event-ec2-federated-user.json', 'utf8'));
        let instanceIds = autoTagFunction.extractEC2ResourceIds(event);
        assert.strictEqual(1, instanceIds.length);
        assert.strictEqual(instanceIds[0], "i-abcd12345efghi678");
    });

    it("should extract multiple instance IDs", function() {
        let event = JSON.parse(fs.readFileSync('test/event-ec2-multiple.json', 'utf8'));
        let instanceIds = autoTagFunction.extractEC2ResourceIds(event);
        assert.strictEqual(2, instanceIds.length);
        assert.strictEqual(instanceIds[0], "i-abcd12345efghi678");
        assert.strictEqual(instanceIds[1], "i-efghi56789jklmn0");
    });
});

describe("extractS3BucketName function", function() {
    it("should return null if data is missing", function() {
        let event = {};
        let user = autoTagFunction.extractS3BucketName(event);
        assert.strictEqual(user, null);
    });

    it("should return name of bucket", function() {
        let event = JSON.parse(fs.readFileSync('test/event-s3.json', 'utf8'));
        let bucketName = autoTagFunction.extractS3BucketName(event);
        assert.strictEqual(bucketName, "i-abcd12345efghi678");
    });
});