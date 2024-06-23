package com.example.tnote.base.utils;

import static com.example.tnote.base.exception.ErrorCode.POST_IMAGE_CONVERT_ERROR;
import static com.example.tnote.base.exception.ErrorCode.POST_IMAGE_INVALID_EXTENSION;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.tnote.base.exception.CustomException;
import com.mysema.commons.lang.Pair;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Uploader {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public Pair<String, String> upload(MultipartFile multipartFile, String dirName){
        try {
            if (!multipartFile.isEmpty()) {
                String originalFileName = multipartFile.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

                // 허용된 확장자 리스트 업데이트
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "pdf", "xls", "xlsx", "ppt", "pptx");

                // 파일 확장자 검증
                if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
                    throw new IllegalStateException("File type not allowed");
                }

                String fileName = dirName + "/" + UUID.randomUUID() + "." + fileExtension;

                File uploadFile = convert(multipartFile)
                        .orElseThrow(() -> new RuntimeException("File conversion failed"));

                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                removeNewFile(uploadFile);
                return Pair.of(amazonS3.getUrl(bucketName, fileName).toString(), originalFileName);
            }
            throw new IllegalStateException("File is empty and cannot be uploaded");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }



    // 로컬서버에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    // 로컬서버에 파일 변환 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload_", ".tmp");
        tempFile.deleteOnExit();  // JVM 종료 시 파일 삭제

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
            return Optional.of(tempFile);
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            throw e;
        }
    }

    public void deleteImage(String imageUrl) {
        log.info("Deleting image from S3: {}", imageUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, imageUrl));
    }
}
