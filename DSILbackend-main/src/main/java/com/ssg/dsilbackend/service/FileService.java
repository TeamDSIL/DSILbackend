package com.ssg.dsilbackend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final AmazonS3Client amazonS3Client;
    private final ReviewRepository reviewRepository;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    public List<FileDTO> uploadFilesSample(List<MultipartFile> multipartFiles) {
        return uploadFiles(multipartFiles, "sample-folder");
    }
// 리뷰등록
    public List<FileDTO> uploadFilesReview(List<MultipartFile> multipartFiles) {
        return uploadFiles(multipartFiles, "sample-folder");
    }

    public List<FileDTO> uploadFiles(List<MultipartFile> multipartFiles, String filePath) {
        List<FileDTO> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = filePath + "/" + uploadFileName;

                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

                uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString(); // S3에서 파일 URL을 가져옴
                System.out.println(uploadFileUrl+" 히얼~~~ ");

            } catch (IOException e) {
                e.printStackTrace();
            }

            s3files.add(
                    FileDTO.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build());
        }

        return s3files;
    }
}
