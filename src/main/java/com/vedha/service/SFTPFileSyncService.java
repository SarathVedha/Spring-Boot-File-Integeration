package com.vedha.service;

import com.vedha.config.SFTPConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class SFTPFileSyncService {

    private final SFTPConfiguration sftpConfiguration;

    private final SftpInboundFileSynchronizer sftpInboundFileSynchronizer;

    @Bean
    @InboundChannelAdapter(channel = "sftpChannelDownload", poller = @Poller(fixedDelay = "10000"))
    MessageSource<File> sftpMessageSource() {

        SftpInboundFileSynchronizingMessageSource sftpInboundFileSynchronizingMessageSource = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer);
        sftpInboundFileSynchronizingMessageSource.setLocalDirectory(new File(sftpConfiguration.getLocalDownloadDirectory()));
        sftpInboundFileSynchronizingMessageSource.setAutoCreateLocalDirectory(true);
        sftpInboundFileSynchronizingMessageSource.setLocalFilter(new AcceptOnceFileListFilter<>());
        sftpInboundFileSynchronizingMessageSource.setMaxFetchSize(1);

        return sftpInboundFileSynchronizingMessageSource;
    }

    @ServiceActivator(inputChannel = "sftpChannelDownload")
    public void sftpFileHandler(File sftpMessageSource) {

        log.warn("SFTP file received: {}", sftpMessageSource.getName());
        log.warn("SFTP file Size: {}", FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(sftpMessageSource)));
    }

}
