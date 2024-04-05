package uz.minio.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.minio.dto.FileResponse;

public interface FileService {


    String uploadFile(MultipartFile file,String tenant);


    FileResponse downloadFile(String tenant);

    void remove(String tenant);


}
