package com.vedha.controller;

import com.vedha.config.SFTPConfiguration;
import com.vedha.service.SFTPGateWay;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "Upload Controller", description = "File Upload To SFTP")
public class FileUploadController {

    private final SFTPGateWay sftpGateWay;

    private final SFTPConfiguration sftpConfiguration;

    @Operation(summary = "Upload SFTP", description = "File To SFTP")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "/sftp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> fileUpload(@RequestParam MultipartFile multipartFile) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        log.warn("File Name: {}", originalFilename);

        if (!Files.isDirectory(Path.of(sftpConfiguration.getLocalUploadDirectory()))){

            Files.createDirectory(Path.of(sftpConfiguration.getLocalUploadDirectory()));
        }

        File file = new File(sftpConfiguration.getLocalUploadDirectory().concat(originalFilename));
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        sftpGateWay.uploadFile(file);

        return ResponseEntity.ok(Map.of("file", originalFilename, "message", "successfully uploaded"));
    }
}
