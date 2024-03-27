package uz.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.url}")
    private String url;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;
    private static Logger LOG = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try{
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(url)
                        .credentials(accessKey, secretKey)
                        .build();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build());
        }
        // Load file from the classpath
        InputStream inputStream = getClass().getResourceAsStream("/default.png");

        minioClient.putObject(
                PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("default.png")
                            .stream(inputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                            .contentType("image/png")
                            .build());
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
        e.printStackTrace();
        System.err.println("Failed to upload file: " + e.getMessage());
    }
    }
}
