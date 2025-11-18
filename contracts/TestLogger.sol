// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract TestLogger {
    struct TestRecord {
        uint studentId;
        uint testId;
        uint testAttemptId;
        string score;
        string hashOfData;
        uint timeStamp;
    }

    mapping ( uint => TestRecord ) public testRecords; // Mapping from testAttemptId to TestRecord

    event TestStored(uint studentId, uint testId, uint testAttemptId, string score, string hashOfData);

    function storeResult(
        uint _studentId,
        uint _testId,
        uint _testAttemptId,
        string memory _score,
        string memory _hashOfData
    ) public {
        require(testRecords[_testAttemptId].testAttemptId == 0, "Test Attempt Id already exists");
        testRecords[_testAttemptId] = TestRecord(_studentId, _testId, _testAttemptId, _score, _hashOfData, block.timestamp);
        emit TestStored(_studentId, _testId, _testAttemptId, _score, _hashOfData);
    }

    function verifyResult(
        uint _testAttemptId,
        string memory _hashOfData
    ) public view returns( bool ) {
        return keccak256(bytes(testRecords[_testAttemptId].hashOfData)) == keccak256(bytes(_hashOfData));
    }
}