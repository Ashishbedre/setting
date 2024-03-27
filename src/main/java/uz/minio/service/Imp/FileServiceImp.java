package uz.minio.service.Imp;

import io.minio.*;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.minio.controller.FileController;
import uz.minio.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImp implements FileService {
    @Value("${minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;

    public FileServiceImp(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    public String uploadFile( MultipartFile file,String tenant) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs
                        .builder()
                        .bucket(bucketName)
                        .build());
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.toLowerCase().endsWith(".png")) {
                // Rename the file
                String newFilename = tenant;
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newFilename)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
                return "File uploaded successfully!";
            } else {
                return "File format not supported. Please upload a file with '.png' extension.";
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException |
                 ErrorResponseException | IllegalArgumentException | InsufficientDataException |
                 InternalException | InvalidResponseException | XmlParserException e) {
            logger.error("Error occurred while uploading file: {}", e.getMessage(), e);
            return "Failed to upload file.";
        }

    }




    public ResponseEntity<byte[]> downloadFile(String tenant) {
        try {
            InputStream stream;
            String actualFileName;

            // Check if the requested file exists in the bucket
            boolean fileExists = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(tenant)
                            .build()
            ) != null;

            if (fileExists) {
                // If the file exists, get the actual file
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(tenant)
                                .build()
                );
                actualFileName = tenant;
            } else {
                // If the file doesn't exist, get the default file
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object("default.png") // Assuming default.png is the default file name
                                .build()
                );
                actualFileName = "default.png";
            }

            byte[] fileContent = IOUtils.toByteArray(stream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", actualFileName);
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | MinioException e) {
            logger.error("Error occurred while downloading file: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void remove(String tenant) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(tenant)
                            .build());

            System.out.println("File deleted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void downloadFile(String fileName, HttpServletResponse response) {
//        try {
//            InputStream stream = minioClient.getObject(
//                    GetObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(fileName)
//                            .build()
//            );
//
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//
//            IOUtils.copy(stream, response.getOutputStream());
//
//            response.flushBuffer();
//            stream.close();
//        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | MinioException e) {
//            logger.error("Error occurred while downloading file: {}", e.getMessage(), e);
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//
//    }
}
