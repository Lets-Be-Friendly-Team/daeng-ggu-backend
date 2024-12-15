package com.ureca.common.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Service {

    private final AmazonS3 s3Client; // 의존성 주입을 통한 S3 클라이언트 사용

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName; // static 제거

    public String uploadFileImage(MultipartFile image, String file, String filename) {
        try {
            // 파일 이름 생성
            String fileName = createFileImageName(image.getOriginalFilename(), filename);

            // 최종 경로 (default/20241214201859image.jpg)
            String filePath = file + "/" + fileName;

            // 메타데이터 생성
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            // 파일 업로드
            s3Client.putObject(bucketName, filePath, image.getInputStream(), metadata);

            // 업로드된 파일의 URL 반환
            return s3Client.getUrl(bucketName, filePath).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3.", e);
        }
    }

    public String updateFileImage(String imageFileUrl, MultipartFile newImage) {
        try {
            String fileName = imageFileUrl.substring(imageFileUrl.lastIndexOf('/') + 1);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(newImage.getContentType());
            metadata.setContentLength(newImage.getSize());

            s3Client.putObject(bucketName, fileName, newImage.getInputStream(), metadata);
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3.", e);
        }
    }

    private String createFileImageName(String fileName, String filename) {
        String fileExtension = "";

        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".")); // 확장자 분리
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter) + filename /*+ fileExtension*/;
    }

    public void deleteFileImage(String imageFileUrl) {
        String fileName = imageFileUrl.substring(imageFileUrl.lastIndexOf('/') + 1);
        try {
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3.", e);
        }
    }

    @Transactional
    public List<String> stringUpload(List<MultipartFile> imgList) {
        List<String> results = new ArrayList<>();
        for (MultipartFile imglist : imgList) {
            String result = uploadFileImage(imglist, "default", imglist.getOriginalFilename());
            results.add(result);
        }
        return results;
    }
}
