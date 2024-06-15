package com.vedha;

import com.vedha.service.SFTPGateWay;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "File Integration API", version = "1.0", description = "File Integration API"))
@RequiredArgsConstructor
public class SpringBootFileIntegrationApplication implements CommandLineRunner {

    private final SFTPGateWay sftpGateWay;

    private final SessionFactory<SftpClient.DirEntry> sftpSessionFactory;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFileIntegrationApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {

        Session<SftpClient.DirEntry> session = sftpSessionFactory.getSession();

        boolean test = session.test();
        log.warn("Test: {}", test);

        String hostPort = session.getHostPort();
        log.warn("HostPort: {}", hostPort);

        log.info("SFTP connection successful");
        session.close();

        ClassPathResource classPathResource = new ClassPathResource("/files/test.txt");
        log.warn("File Name: {}", classPathResource.getFilename());
        log.warn("File: {}", FileUtils.readFileToString(classPathResource.getFile(), Charset.defaultCharset()));

        sftpGateWay.uploadFile(classPathResource.getFile());
    }

}
