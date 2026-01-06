package com.example.demo.oss;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OSS 业务逻辑实现类 (Object Storage Service Service)
 * 模拟对象存储服务，将文件存储在本地目录
 */
@Service
public class OssService {

    // 模拟存储根目录
    private final String STORAGE_PATH = "D:/yudao/myproject/coder/demo/storage/";

    public OssService() {
        File directory = new File(STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * 上传文件
     * @param file 待上传的文件
     * @return 文件存储的相对路径或标识
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(STORAGE_PATH + fileName);
        Files.write(path, file.getBytes());
        
        return fileName;
    }

    /**
     * 获取文件列表
     * @return 文件名列表
     */
    public List<String> listFiles() throws IOException {
        return Files.list(Paths.get(STORAGE_PATH))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileName) {
        File file = new File(STORAGE_PATH + fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 下载文件/获取文件字节数组
     * @param fileName 文件名
     * @return 文件二进制内容
     */
    public byte[] getFile(String fileName) throws IOException {
        Path path = Paths.get(STORAGE_PATH + fileName);
        return Files.readAllBytes(path);
    }
}
