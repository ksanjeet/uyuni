#!/bin/bash
set -e

# make sure the package repository is up to date
zypper --non-interactive --gpg-auto-import-keys ref

# Packages required to run spacewalk-setup inside of the container
zypper in -y perl \
             perl-Params-Validate \
             perl-Mail-RFC822-Address \
             perl-XML-LibXML \
             perl-XML-SAX \
             perl-DateTime \
             perl-Frontier-RPC \
             perl-libwww-perl \
             perl-Net-LibIDN \
             perl-Satcon \
             perl-DBI \
             which \
             timezone

# Packages required to run the python unit tests
zypper in -y  cx_Oracle \
              make \
              python \
              python-argparse \
              python-base \
              python-configobj \
              python-debian \
              python-devel \
              python-dmidecode \
              python-enum34 \
              python-ethtool \
              python-gobject2 \
              python-gpgme \
              python-gzipstream \
              python-iniparse \
              python-mock \
              python-newt \
              python-nose \
              python-pam \
              python-psycopg2 \
              python-pyOpenSSL \
              python-pycrypto \
              python-pycurl \
              python-pylint \
              python-selinux \
              python-setools \
              python-unittest2 \
              python-urlgrabber \
              python-xml \
              rpm-python \
              yum

# Packages required to run the Java unit tests
zypper in -y ant \
             ant-junit \
             apache-ivy \
             java-1_7_0-ibm-devel \
             pam-modules \
             sudo \
             tar
