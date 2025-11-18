package com.codingsena.codingsena_backend.services.impls;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import com.codingsena.codingsena_backend.services.BlockchainService;
import com.codingsena.codingsena_backend.utils.TestLogger;

import jakarta.annotation.PostConstruct;

@Service
public class BlockchainServiceImpl implements BlockchainService {
	
	private Web3j web3j; // for RPC requests via Alchemy (Alchemy - kind of Full Node)
	private Credentials credentials; // for signing transactions with private key
	private TestLogger testLogger;
	
	public BlockchainServiceImpl(Web3j web3j) {
		super();
		this.web3j = web3j;
	}
	
	@PostConstruct
	public void init() {
		this.credentials = Credentials.create(privateKey);
		this.testLogger = TestLogger.load(
			contractAddress,
			web3j,
			credentials,
			new DefaultGasProvider()
		);
	}
	
	@Value("${private.key}")
	private String privateKey;
	
	@Value("${contract.address}")
	private String contractAddress;
	
	@Override
	@Async
	public void storeResult(long studentId, long testId, long attemptId, double score, String hashOfData) throws Exception {
		testLogger.storeResult(
			BigInteger.valueOf(studentId),
			BigInteger.valueOf(testId),
			BigInteger.valueOf(attemptId),
			String.valueOf(score),
			hashOfData
		).send();
	}

	@Override
	public Boolean verifyResult(long attemptId, String hashOfData) throws Exception {
		return testLogger.verifyResult(
			BigInteger.valueOf(attemptId),
			hashOfData
		).send();
	}

}
