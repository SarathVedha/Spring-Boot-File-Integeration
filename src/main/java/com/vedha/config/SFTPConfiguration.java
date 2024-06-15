package com.vedha.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app.sftp")
public class SFTPConfiguration {

    private String host;

    private int port;

    private String userName;

    private String password;

    private String privateKey;

    private String remoteDownloadDirectory;

    private String remoteDownloadFileFilter;

    private String localDownloadDirectory;

    private String remoteUploadDirectory;

    private String localUploadDirectory;

    @Bean
    @Order(1)
    SessionFactory<SftpClient.DirEntry> sftpSessionFactory() {

        DefaultSftpSessionFactory sftpSessionFactory = new DefaultSftpSessionFactory();
        sftpSessionFactory.setHost(host);
        sftpSessionFactory.setPort(port);
        sftpSessionFactory.setUser(userName);
        sftpSessionFactory.setAllowUnknownKeys(true);
        if (password != null) {

            sftpSessionFactory.setPassword(password);
        } else {

            sftpSessionFactory.setPrivateKey(new ClassPathResource(privateKey));
        }
        return new CachingSessionFactory<>(sftpSessionFactory);
    }

    @Bean
    SftpInboundFileSynchronizer sftpInboundFileSynchronizer(SessionFactory<SftpClient.DirEntry> sftpSessionFactory) {

        SftpInboundFileSynchronizer sftpInboundFileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory);
        sftpInboundFileSynchronizer.setRemoteDirectory(remoteDownloadDirectory);
        sftpInboundFileSynchronizer.setFilter(new SftpSimplePatternFileListFilter(remoteDownloadFileFilter));
        sftpInboundFileSynchronizer.setDeleteRemoteFiles(true);
        sftpInboundFileSynchronizer.setPreserveTimestamp(true);

        return sftpInboundFileSynchronizer;
    }
}
