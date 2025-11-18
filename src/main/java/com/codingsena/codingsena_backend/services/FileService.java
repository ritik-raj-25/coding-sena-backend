package com.codingsena.codingsena_backend.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public interface FileService {
	String saveFile(String subBucketName, MultipartFile file) throws S3Exception, AwsServiceException, SdkClientException, IOException;
	String getFileUrl(String fileName);
	void deleteFile(String fileName);
}
