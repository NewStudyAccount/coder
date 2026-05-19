package com.admin.service;

import com.admin.entity.OssFile;
import com.admin.mapper.OssFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class OssFileService {

    @Value("${oss.storage-path}")
    private String storagePath;

    @Autowired
    private OssFileMapper ossFileMapper;

    @PostConstruct
    public void init() {
        File dir = new File(storagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public OssFile uploadFile(MultipartFile file, Long userId, String username) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String filePath = storagePath + File.separator + fileName;

        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        OssFile ossFile = new OssFile();
        ossFile.setOriginalName(originalName);
        ossFile.setFileName(fileName);
        ossFile.setFilePath(filePath);
        ossFile.setFileSize(file.getSize());
        ossFile.setFileType(file.getContentType());
        ossFile.setUrl("/api/oss/download/" + fileName);
        ossFile.setUploadUserId(userId);
        ossFile.setUploadUserName(username);

        ossFileMapper.insert(ossFile);
        return ossFile;
    }

    public List<OssFile> getFileList(String originalName) {
        return ossFileMapper.selectList(originalName);
    }

    public OssFile getFileById(Long id) {
        return ossFileMapper.selectById(id);
    }

    public byte[] getFileContent(Long id) throws IOException {
        OssFile ossFile = ossFileMapper.selectById(id);
        if (ossFile == null) {
            throw new RuntimeException("文件不存在");
        }
        Path path = Paths.get(ossFile.getFilePath());
        if (!Files.exists(path)) {
            throw new RuntimeException("文件已被删除");
        }
        return Files.readAllBytes(path);
    }

    public byte[] getFileContentByName(String fileName) throws IOException {
        List<OssFile> list = ossFileMapper.selectList(null);
        for (OssFile f : list) {
            if (fileName.equals(f.getFileName())) {
                Path path = Paths.get(f.getFilePath());
                if (Files.exists(path)) {
                    return Files.readAllBytes(path);
                }
            }
        }
        throw new RuntimeException("文件不存在");
    }

    public void deleteFile(Long id) {
        OssFile ossFile = ossFileMapper.selectById(id);
        if (ossFile == null) {
            throw new RuntimeException("文件不存在");
        }
        Path path = Paths.get(ossFile.getFilePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // ignore
        }
        ossFileMapper.deleteById(id);
    }
}
