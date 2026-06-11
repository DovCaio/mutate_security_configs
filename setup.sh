#!/bin/bash

# This script is not required for the execution of AutoSecMut.
# It automates the installation and configuration of the software dependencies
# used in the experimental environment described in this study.

set -e

sudo apt update

sudo apt install -y wget curl gnupg lsb-release ca-certificates apt-transport-https

if [ ! -f /usr/share/keyrings/adoptium.gpg ]; then
  curl -fsSL https://packages.adoptium.net/artifactory/api/gpg/key/public | \
    sudo gpg --dearmor -o /usr/share/keyrings/adoptium.gpg
fi

echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" | \
  sudo tee /etc/apt/sources.list.d/adoptium.list > /dev/null

sudo apt update

sudo apt install -y temurin-21-jdk

sudo apt install -y git maven gradle

JAVA_PATH=$(dirname $(dirname $(readlink -f $(which java))))
echo "Detected: $JAVA_PATH"

sed -i '/JAVA_HOME/d' ~/.bashrc
sed -i '/PATH=.*JAVA_HOME/d' ~/.bashrc

echo "export JAVA_HOME=$JAVA_PATH" >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc

sed -i '/JAVA_HOME/d' ~/.bashrc
sed -i '/PATH=.*JAVA_HOME/d' ~/.bashrc


export JAVA_HOME=$JAVA_PATH
export PATH=$JAVA_HOME/bin:$PATH

java -version
mvn -v
gradle -v

java -version 2>&1 | tee java_version.txt
mvn -v | tee maven_version.txt
gradle -v | tee gradle_version.txt
uname -a | tee system_info.txt
lscpu | tee cpu_info.txt
free -h | tee memory_info.txt
