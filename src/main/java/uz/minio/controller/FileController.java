package uz.minio.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.minio.service.FileService;


@RestController
    @RequestMapping("/settings/company-logo")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload/{tenant}")
    public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable String tenant) {
        return fileService.uploadFile(file,tenant);
    }

    @GetMapping("download/{tenant}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String tenant) {
        return fileService.downloadFile(tenant);
    }

    @DeleteMapping("delete/{tenant}")
    public ResponseEntity<?> removeFile(@PathVariable String tenant){
        fileService.remove(tenant);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
