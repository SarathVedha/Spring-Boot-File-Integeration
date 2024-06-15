package com.vedha.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.io.File;

@MessagingGateway
public interface SFTPGateWay {

    @Gateway(requestChannel = "sftpChannelUpload")
    void uploadFile(File file);
}
