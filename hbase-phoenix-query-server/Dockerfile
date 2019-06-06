FROM openjdk:8-jre-alpine
LABEL maintainer="https://arima.eu"

ENV HBASE_VERSION=2.0.0 HBASE_MINOR_VERSION=2.0 PHOENIX_VERSION=5.0.0 HBASE_HADOOP_VERSION=2.7.4 REPLACEMENT_HADOOP_VERSION=2.8.5

RUN apk --no-cache --update add bash ca-certificates gnupg openssl python tar wget \
 && apk --no-cache --update --repository https://dl-3.alpinelinux.org/alpine/edge/community/ add xmlstarlet \
 && update-ca-certificates \
\
# Set up directories
 && mkdir -p /opt/hbase \
 && mkdir -p /opt/phoenix \
 && mkdir -p /opt/phoenix-server \
\
# Download HBase
 && wget -q -O /tmp/hbase.tar.gz https://archive.apache.org/dist/hbase/$HBASE_VERSION/hbase-$HBASE_VERSION-bin.tar.gz \
 && tar -xzf /tmp/hbase.tar.gz -C /opt/hbase  --strip-components 1 \
\
# Download Phoenix
 && wget -O /tmp/KEYS https://www-us.apache.org/dist/phoenix/KEYS \
 && gpg --import /tmp/KEYS \
 && wget -q -O /tmp/phoenix.tar.gz https://www-eu.apache.org/dist/phoenix/apache-phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION/bin/apache-phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-bin.tar.gz \
 && wget -O /tmp/phoenix.asc https://www-eu.apache.org/dist/phoenix/apache-phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION/bin/apache-phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-bin.tar.gz.asc \
 && gpg --verify /tmp/phoenix.asc /tmp/phoenix.tar.gz \
 && tar -xzf /tmp/phoenix.tar.gz -C /opt/phoenix --strip-components 1 \
\
# Set up HBase and Phoenix
 && mv /opt/phoenix/phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-server.jar /opt/hbase/lib/ \
 && cp /opt/phoenix/phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-client.jar /opt/hbase/lib/ \
 && mv /opt/phoenix/phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-queryserver.jar /opt/phoenix-server/ \
 && mv /opt/phoenix/phoenix-$PHOENIX_VERSION-HBase-$HBASE_MINOR_VERSION-client.jar /opt/phoenix-server/ \
 && mv /opt/phoenix/bin /opt/phoenix-server/bin \
\
# Replace HBase's Hadoop 2.7.4 jars with Hadoop 2.8.5 jars
 && for i in /opt/hbase/lib/hadoop-*; do \
      case $i in \
        *test*);; \
        *) \
          NEW_FILE=$(echo $i | sed -e "s/$HBASE_HADOOP_VERSION/$REPLACEMENT_HADOOP_VERSION/g; s/\/opt\/hbase\/lib\///g"); \
          FOLDER=$(echo $NEW_FILE | sed -e "s/-$REPLACEMENT_HADOOP_VERSION.jar//g"); \
          wget -O /opt/hbase/lib/$NEW_FILE https://search.maven.org/remotecontent?filepath=org/apache/hadoop/$FOLDER/$REPLACEMENT_HADOOP_VERSION/$NEW_FILE;; \
      esac; \
\
      rm $i; \
    done \
\
# Clean up
 && apk del gnupg openssl tar \
 && rm -rf /opt/phoenix /tmp/* /var/tmp/* /var/cache/apk/*

EXPOSE 8765

ADD start-hbase-phoenix.sh /start-hbase-phoenix.sh

CMD ["./start-hbase-phoenix.sh"]