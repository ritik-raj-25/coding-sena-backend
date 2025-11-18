package com.codingsena.codingsena_backend.services;

public interface BlockchainService {
	void storeResult(long studentId, long testId, long attemptId, double score, String hashOfData) throws Exception;
	Boolean verifyResult(long attemptId, String hashOfData) throws Exception;	
}
