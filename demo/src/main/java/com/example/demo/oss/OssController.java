package com.example.demo.oss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * OSS 控制层 (Object Storage Service Controller)
 */
@RestController
@RequestMapping("/api/oss")
@CrossOrigin(origins = "*") // 允许前端跨域
public class OssController {

    @Autowired
    private OssService ossService;

    /**
     * 上传文件接口
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = ossService.uploadFile(file);
            return ResponseEntity.ok("上传成功: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有文件列表接口
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> list() {
        try {
            List<String> files = ossService.listFiles();
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载/预览文件接口
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) {
        try {
            byte[] content = ossService.getFile(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 删除文件接口
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> delete(@PathVariable String fileName) {
        boolean success = ossService.deleteFile(fileName);
        if (success) {
            return ResponseEntity.ok("删除成功");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在或删除失败");
        }
    }
}
