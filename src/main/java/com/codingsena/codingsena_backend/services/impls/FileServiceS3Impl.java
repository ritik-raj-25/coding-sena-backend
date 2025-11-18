package com.codingsena.codingsena_backend.services.impls;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.services.FileService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class FileServiceS3Impl implements FileService{
	
	private S3Client s3Client;
	private S3Presigner s3Presigner;
	
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	public FileServiceS3Impl(S3Client s3Client, S3Presigner s3Presigner) {
		super();
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	@Override
	public String saveFile(String subBucketName, MultipartFile file) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		String key =  subBucketName+ "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
		
		// build meta-data of upload
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();
		
		
		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
		
		return key;
	}

	@Override
	public String getFileUrl(String fileName) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.build();
		
		PresignedGetObjectRequest presignedGetObjectRequest =
		        s3Presigner.presignGetObject(builder -> builder
		        		.getObjectRequest(getObjectRequest)
		        		.signatureDuration(Duration.ofMinutes(24*60)));
		        
		        
		return presignedGetObjectRequest.url().toString();
	}

	@Override
	public void deleteFile(String fileName) {
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.build();
		
		s3Client.deleteObject(deleteObjectRequest);
	}

}
