# Use Ubuntu latest as the base image
FROM ubuntu:latest

# Set the maintainer
LABEL version="1.0"
LABEL maintainer="sarath vedha"
LABEL description="SFTP server with OpenSSH Server and vim installed."
LABEL usage1="docker run -d -p 22:22 --name sftp_server sftp_server"
LABEL usage2="sftp -P 22 sftpuser@localhost"
#LABEL ussage3="ssh-keygen -t rsa -b 4096 -C "your_email@example.com" -f $HOME/.ssh/id_rsa"

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Update packages and install OpenSSH Server and vim
RUN apt-get update && \
    apt-get install -y openssh-server vim && \
    rm -rf /var/lib/apt/lists/*

# Set up user for SFTP with no shell login
RUN useradd -m -d /home/sftpuser -s /usr/sbin/nologin sftpuser && \
    mkdir -p /home/sftpuser/.ssh && \
    chown sftpuser:sftpuser /home/sftpuser/.ssh && \
    chmod 700 /home/sftpuser/.ssh

# Copy the public key
# Ensure you replace 'docker_rsa.pub' with your actual public key file name
COPY src/main/resources/keys/docker_rsa.pub /home/sftpuser/.ssh/authorized_keys

# Set permissions for the public key
RUN chmod 600 /home/sftpuser/.ssh/authorized_keys && \
    chown sftpuser:sftpuser /home/sftpuser/.ssh/authorized_keys

# Create a directory for SFTP that the user will have access to
RUN mkdir -p /home/sftpuser/sftp/download && \
    mkdir -p /home/sftpuser/sftp/upload && \
    chown root:root /home/sftpuser /home/sftpuser/sftp && \
    chmod 755 /home/sftpuser /home/sftpuser/sftp && \
    chown sftpuser:sftpuser /home/sftpuser/sftp/download && \
    chmod 755 /home/sftpuser/sftp/download && \
    chown sftpuser:sftpuser /home/sftpuser/sftp/upload && \
    chmod 755 /home/sftpuser/sftp/upload

# Configure SSH for SFTP
RUN mkdir -p /run/sshd && \
    echo "Match User sftpuser" >> /etc/ssh/sshd_config && \
    echo "ChrootDirectory /home/sftpuser/sftp" >> /etc/ssh/sshd_config && \
    echo "ForceCommand internal-sftp" >> /etc/ssh/sshd_config && \
    echo "PasswordAuthentication no" >> /etc/ssh/sshd_config && \
    echo "PubkeyAuthentication yes" >> /etc/ssh/sshd_config && \
    echo "PermitTunnel no" >> /etc/ssh/sshd_config && \
    echo "AllowAgentForwarding no" >> /etc/ssh/sshd_config && \
    echo "AllowTcpForwarding no" >> /etc/ssh/sshd_config && \
    echo "X11Forwarding no" >> /etc/ssh/sshd_config

# Expose the SSH port
EXPOSE 22

# Run SSHD on container start
CMD ["/usr/sbin/sshd", "-D", "-e"]