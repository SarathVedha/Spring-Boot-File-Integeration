package com.vedha.service;

import com.vedha.config.SFTPConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class SFTPFileUploadService {

    private final SFTPConfiguration sftpConfiguration;

    @Bean
    @ServiceActivator(inputChannel = "sftpChannelUpload")
    public MessageHandler processUpload(SessionFactory<SftpClient.DirEntry> sftpSessionFactory) {

        log.warn("Uploading file to SFTP server");
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(sftpSessionFactory);
        sftpMessageHandler.setAutoCreateDirectory(true);
        sftpMessageHandler.setRemoteDirectoryExpression(new LiteralExpression(sftpConfiguration.getRemoteUploadDirectory()));
        sftpMessageHandler.setFileNameGenerator(message -> ((File) message.getPayload()).getName());
        log.warn("File uploaded successfully");

        return sftpMessageHandler;
    }

}
