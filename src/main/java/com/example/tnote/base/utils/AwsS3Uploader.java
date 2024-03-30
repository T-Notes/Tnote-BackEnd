package com.example.tnote.base.utils;

import static com.example.tnote.base.exception.ErrorCode.POST_IMAGE_CONVERT_ERROR;
import static com.example.tnote.base.exception.ErrorCode.POST_IMAGE_INVALID_EXTENSION;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.tnote.base.exception.CustomException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public String upload(MultipartFile multipartFile, String dirName){

        try {
            if(!multipartFile.isEmpty()){
                // 확장자 확인
                String originFileName = multipartFile.getOriginalFilename().toUpperCase();
                log.info("fileName={}", originFileName);

                String ext = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                log.info("ext={}", ext);

                if(!ext.matches("JPEG|JPG|HEIC|PNG|" )){
                    throw new CustomException(POST_IMAGE_INVALID_EXTENSION);
                }
            }
            File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                    .orElseThrow(() -> new CustomException(POST_IMAGE_CONVERT_ERROR));

            String fileName = dirName + "/" + UUID.randomUUID();   // S3에 저장된 파일 이름

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)   // S3에 업로드
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            removeNewFile(uploadFile);
            log.info("S3 upload complete!");
            return amazonS3.getUrl(bucketName, fileName).toString();

        } catch (IOException e) {
            throw new RuntimeException();
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
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    public void deleteImage(String imageUrl) {
        log.info("deleteImage = {}", imageUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, imageUrl));
    }
}
