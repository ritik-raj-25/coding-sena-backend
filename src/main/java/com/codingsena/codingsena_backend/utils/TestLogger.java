package com.codingsena.codingsena_backend.utils;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class TestLogger extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50610bac8061001c5f395ff3fe608060405234801561000f575f5ffd5b506004361061003f575f3560e01c80636d98ab8d14610043578063d027ca7d1461005f578063f72b219a1461008f575b5f5ffd5b61005d600480360381019061005891906104ee565b6100c4565b005b6100796004803603810190610074919061059d565b6101f1565b6040516100869190610611565b60405180910390f35b6100a960048036038101906100a4919061062a565b61022b565b6040516100bb969594939291906106c4565b60405180910390f35b5f5f5f8581526020019081526020015f206002015414610119576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101109061077b565b60405180910390fd5b6040518060c00160405280868152602001858152602001848152602001838152602001828152602001428152505f5f8581526020019081526020015f205f820151815f0155602082015181600101556040820151816002015560608201518160030190816101879190610996565b50608082015181600401908161019d9190610996565b5060a082015181600501559050507fd479a84c9aed98df967ed370ee57c824e67dff2b8a1820def14fdca74b9f6cb785858585856040516101e2959493929190610a65565b60405180910390a15050505050565b5f81805190602001205f5f8581526020019081526020015f2060040160405161021a9190610b60565b604051809103902014905092915050565b5f602052805f5260405f205f91509050805f01549080600101549080600201549080600301805461025b906107c6565b80601f0160208091040260200160405190810160405280929190818152602001828054610287906107c6565b80156102d25780601f106102a9576101008083540402835291602001916102d2565b820191905f5260205f20905b8154815290600101906020018083116102b557829003601f168201915b5050505050908060040180546102e7906107c6565b80601f0160208091040260200160405190810160405280929190818152602001828054610313906107c6565b801561035e5780601f106103355761010080835404028352916020019161035e565b820191905f5260205f20905b81548152906001019060200180831161034157829003601f168201915b5050505050908060050154905086565b5f604051905090565b5f5ffd5b5f5ffd5b5f819050919050565b6103918161037f565b811461039b575f5ffd5b50565b5f813590506103ac81610388565b92915050565b5f5ffd5b5f5ffd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b610400826103ba565b810181811067ffffffffffffffff8211171561041f5761041e6103ca565b5b80604052505050565b5f61043161036e565b905061043d82826103f7565b919050565b5f67ffffffffffffffff82111561045c5761045b6103ca565b5b610465826103ba565b9050602081019050919050565b828183375f83830152505050565b5f61049261048d84610442565b610428565b9050828152602081018484840111156104ae576104ad6103b6565b5b6104b9848285610472565b509392505050565b5f82601f8301126104d5576104d46103b2565b5b81356104e5848260208601610480565b91505092915050565b5f5f5f5f5f60a0868803121561050757610506610377565b5b5f6105148882890161039e565b95505060206105258882890161039e565b94505060406105368882890161039e565b935050606086013567ffffffffffffffff8111156105575761055661037b565b5b610563888289016104c1565b925050608086013567ffffffffffffffff8111156105845761058361037b565b5b610590888289016104c1565b9150509295509295909350565b5f5f604083850312156105b3576105b2610377565b5b5f6105c08582860161039e565b925050602083013567ffffffffffffffff8111156105e1576105e061037b565b5b6105ed858286016104c1565b9150509250929050565b5f8115159050919050565b61060b816105f7565b82525050565b5f6020820190506106245f830184610602565b92915050565b5f6020828403121561063f5761063e610377565b5b5f61064c8482850161039e565b91505092915050565b61065e8161037f565b82525050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f61069682610664565b6106a0818561066e565b93506106b081856020860161067e565b6106b9816103ba565b840191505092915050565b5f60c0820190506106d75f830189610655565b6106e46020830188610655565b6106f16040830187610655565b8181036060830152610703818661068c565b90508181036080830152610717818561068c565b905061072660a0830184610655565b979650505050505050565b7f5465737420417474656d707420496420616c72656164792065786973747300005f82015250565b5f610765601e8361066e565b915061077082610731565b602082019050919050565b5f6020820190508181035f83015261079281610759565b9050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806107dd57607f821691505b6020821081036107f0576107ef610799565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f600883026108527fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610817565b61085c8683610817565b95508019841693508086168417925050509392505050565b5f819050919050565b5f61089761089261088d8461037f565b610874565b61037f565b9050919050565b5f819050919050565b6108b08361087d565b6108c46108bc8261089e565b848454610823565b825550505050565b5f5f905090565b6108db6108cc565b6108e68184846108a7565b505050565b5b81811015610909576108fe5f826108d3565b6001810190506108ec565b5050565b601f82111561094e5761091f816107f6565b61092884610808565b81016020851015610937578190505b61094b61094385610808565b8301826108eb565b50505b505050565b5f82821c905092915050565b5f61096e5f1984600802610953565b1980831691505092915050565b5f610986838361095f565b9150826002028217905092915050565b61099f82610664565b67ffffffffffffffff8111156109b8576109b76103ca565b5b6109c282546107c6565b6109cd82828561090d565b5f60209050601f8311600181146109fe575f84156109ec578287015190505b6109f6858261097b565b865550610a5d565b601f198416610a0c866107f6565b5f5b82811015610a3357848901518255600182019150602085019450602081019050610a0e565b86831015610a505784890151610a4c601f89168261095f565b8355505b6001600288020188555050505b505050505050565b5f60a082019050610a785f830188610655565b610a856020830187610655565b610a926040830186610655565b8181036060830152610aa4818561068c565b90508181036080830152610ab8818461068c565b90509695505050505050565b5f81905092915050565b5f819050815f5260205f209050919050565b5f8154610aec816107c6565b610af68186610ac4565b9450600182165f8114610b105760018114610b2557610b57565b60ff1983168652811515820286019350610b57565b610b2e85610ace565b5f5b83811015610b4f57815481890152600182019150602081019050610b30565b838801955050505b50505092915050565b5f610b6b8284610ae0565b91508190509291505056fea2646970667358221220d156ad8888460e649fbaca65240d766019488c4fa2eaf23732132251cdad9a7264736f6c634300081e0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_STORERESULT = "storeResult";

    public static final String FUNC_TESTRECORDS = "testRecords";

    public static final String FUNC_VERIFYRESULT = "verifyResult";

    public static final Event TESTSTORED_EVENT = new Event("TestStored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected TestLogger(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TestLogger(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TestLogger(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TestLogger(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> storeResult(BigInteger _studentId,
            BigInteger _testId, BigInteger _testAttemptId, String _score, String _hashOfData) {
        final Function function = new Function(
                FUNC_STORERESULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_studentId), 
                new org.web3j.abi.datatypes.generated.Uint256(_testId), 
                new org.web3j.abi.datatypes.generated.Uint256(_testAttemptId), 
                new org.web3j.abi.datatypes.Utf8String(_score), 
                new org.web3j.abi.datatypes.Utf8String(_hashOfData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<TestStoredEventResponse> getTestStoredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TESTSTORED_EVENT, transactionReceipt);
        ArrayList<TestStoredEventResponse> responses = new ArrayList<TestStoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TestStoredEventResponse typedResponse = new TestStoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.studentId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.testId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.testAttemptId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.score = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.hashOfData = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TestStoredEventResponse getTestStoredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TESTSTORED_EVENT, log);
        TestStoredEventResponse typedResponse = new TestStoredEventResponse();
        typedResponse.log = log;
        typedResponse.studentId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.testId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.testAttemptId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.score = (String) eventValues.getNonIndexedValues().get(3).getValue();
        typedResponse.hashOfData = (String) eventValues.getNonIndexedValues().get(4).getValue();
        return typedResponse;
    }

    public Flowable<TestStoredEventResponse> testStoredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTestStoredEventFromLog(log));
    }

    public Flowable<TestStoredEventResponse> testStoredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TESTSTORED_EVENT));
        return testStoredEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, String, String, BigInteger>> testRecords(
            BigInteger param0) {
        final Function function = new Function(FUNC_TESTRECORDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, String, String, BigInteger>>(function,
                new Callable<Tuple6<BigInteger, BigInteger, BigInteger, String, String, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, BigInteger, String, String, BigInteger> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, BigInteger, String, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> verifyResult(BigInteger _testAttemptId, String _hashOfData) {
        final Function function = new Function(FUNC_VERIFYRESULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_testAttemptId), 
                new org.web3j.abi.datatypes.Utf8String(_hashOfData)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static TestLogger load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new TestLogger(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TestLogger load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestLogger(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TestLogger load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new TestLogger(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TestLogger load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new TestLogger(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TestLogger> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TestLogger.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<TestLogger> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TestLogger.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<TestLogger> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TestLogger.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<TestLogger> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TestLogger.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class TestStoredEventResponse extends BaseEventResponse {
        public BigInteger studentId;

        public BigInteger testId;

        public BigInteger testAttemptId;

        public String score;

        public String hashOfData;
    }
}
