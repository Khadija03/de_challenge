#this is the Dockerfile for cassandra

FROM bitnami/cassandra

COPY ./script/create-table.cql /create-table.cql

EXPOSE 9042