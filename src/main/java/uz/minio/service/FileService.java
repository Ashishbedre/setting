package uz.minio.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {


    String uploadFile(MultipartFile file,String tenant);


    ResponseEntity<byte[]> downloadFile(String tenant);

    void remove(String tenant);


}
