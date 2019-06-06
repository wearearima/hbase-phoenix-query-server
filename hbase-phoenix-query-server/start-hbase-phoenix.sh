#!/usr/bin/env bash

HBASE_SITE="/opt/hbase/conf/hbase-site.xml"

addConfig () {

    xmlstarlet ed -L -s "/configuration" -t elem -n propertyTMP -v "" \
     -s "/configuration/propertyTMP" -t elem -n name -v $2 \
     -s "/configuration/propertyTMP" -t elem -n value -v $3 \
     -r "/configuration/propertyTMP" -v "property" \
     $1
}

addConfig $HBASE_SITE "hbase.regionserver.wal.codec" "org.apache.hadoop.hbase.regionserver.wal.IndexedWALEditCodec"
addConfig $HBASE_SITE "hbase.region.server.rpc.scheduler.factory.class" "org.apache.hadoop.hbase.ipc.PhoenixRpcSchedulerFactory"
addConfig $HBASE_SITE "hbase.rpc.controllerfactory.class" "org.apache.hadoop.hbase.ipc.controller.ServerRpcControllerFactory"
addConfig $HBASE_SITE "hbase.unsafe.stream.capability.enforce" "false"
addConfig $HBASE_SITE "phoenix.schema.isNamespaceMappingEnabled" true
addConfig $HBASE_SITE "hbase.regionserver.thrift.framed" true
addConfig $HBASE_SITE "hbase.regionserver.thrift.compact" true

export HBASE_CONF_DIR=/opt/hbase/conf
export HBASE_CP=/opt/hbase/lib
export HBASE_HOME=/opt/hbase

function clean_up {
    /opt/hbase/bin/stop-hbase.sh
    /opt/phoenix-server/bin/queryserver.py stop

    exit
}

trap clean_up SIGINT SIGTERM

/opt/hbase/bin/start-hbase.sh &
/opt/phoenix-server/bin/queryserver.py start &

while true; do sleep 1; done