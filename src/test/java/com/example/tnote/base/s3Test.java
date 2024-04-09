package com.example.tnote.base;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(S3MockConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public class s3Test {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private static final String BUCKET_NAME = "tnote";

    @BeforeAll
    static void setUp(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
        s3Mock.start();
        amazonS3.createBucket(BUCKET_NAME);
    }

    @AfterAll
    static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
        amazonS3.shutdown();
        s3Mock.stop();
    }

//    @Test
//    @DisplayName("s3 import 테스트")
//    void S3Import() throws IOException {
//        // given
//        String path = "test/02.txt";
//        String contentType = "text/plain";
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(contentType);
//        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, path,
//                new ByteArrayInputStream("".getBytes(
//                        StandardCharsets.UTF_8)), objectMetadata);
//        amazonS3.putObject(putObjectRequest);
//
//        // when
//        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, path);
//
//        // then
//        assertThat(s3Object.getObjectMetadata().getContentType()).isEqualTo(contentType);
//        assertThat(new String(FileCopyUtils.copyToByteArray(s3Object.getObjectContent()))).isEqualTo("");
//    }
}

