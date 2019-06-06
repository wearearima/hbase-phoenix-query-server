# HBase Phoenix Query Server demo

This repository contains a Phoenix Query Server (PQS) and HBase demo composed of two parts:

 - hbase-phoenix-query-server: Docker image with HBase and PQS installed.
 - hbase-phoenix-thin-client: Docker image with a Spring Boot application which makes queries to PQS with Phoenix Thin Client.

Run the demo with this command:

```
docker-compose up
```

To create a table and its content in HBase, make a request with this URL: `http://localhost:8080/create`

Once HBase table is initialized, you can query the content with this URL: `http://localhost:8080/read/{id}`

> This demo is **not suitable for production** use as it does not use HDFS for storage.

# Credits

By https://www.arima.eu

![ARIMA Software Design](https://arima.eu/arima-claim.png)