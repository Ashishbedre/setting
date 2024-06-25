package uz.minio.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.minio.dto.FileResponse;
import uz.minio.service.FileService;


@RestController
@RequestMapping("/settings/company-logo")
@CrossOrigin
@PreAuthorize("hasAnyRole('client_user', 'client_admin')")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload/tenant={tenant}")
    public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable String tenant) {
        return fileService.uploadFile(file,tenant);
    }

    @GetMapping("download/tenant={tenant}")
    public FileResponse downloadFile(@PathVariable String tenant) {
        return fileService.downloadFile(tenant);
    }

    @DeleteMapping("delete/tenant={tenant}")
    public ResponseEntity<?> removeFile(@PathVariable String tenant){
        fileService.remove(tenant);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
