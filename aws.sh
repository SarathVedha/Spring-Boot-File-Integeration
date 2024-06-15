#!/bin/bash

# Update the system
yum update -y

# sftp -P 20 sftpuser@<public-ip>
# create a user
adduser sftpuser

# set password
usermod --password $(echo Welcome@01 | openssl passwd -1 -stdin) sftpuser

# create a directory for sftp
mkdir -p /var/sftp && \
  chown root:root /var/sftp && \
  chmod 755 /var/sftp

# create a directory for download
mkdir -p /var/sftp/download && \
  chown sftpuser:sftpuser /var/sftp/download && \
  chmod 755 /var/sftp/download

# create a directory for upload
mkdir -p /var/sftp/upload && \
  chown sftpuser:sftpuser /var/sftp/upload && \
  chmod 755 /var/sftp/upload

# add the following lines to /etc/ssh/sshd_config file to enable sftp
echo "Match User sftpuser" >> /etc/ssh/sshd_config && \
#  echo "Subsystem sftp internal-sftp" >> /etc/ssh/sshd_config && \ # already exists in sshd_config
  echo "ChrootDirectory /var/sftp" >> /etc/ssh/sshd_config && \
  echo "ForceCommand internal-sftp" >> /etc/ssh/sshd_config && \
  echo "PasswordAuthentication yes" >> /etc/ssh/sshd_config && \
  echo "PermitTunnel no" >> /etc/ssh/sshd_config && \
  echo "AllowAgentForwarding no" >> /etc/ssh/sshd_config && \
  echo "AllowTcpForwarding no" >> /etc/ssh/sshd_config && \
  echo "X11Forwarding no" >> /etc/ssh/sshd_config

# restart ssh service
systemctl restart sshd