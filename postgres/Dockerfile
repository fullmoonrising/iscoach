FROM postgres:latest
RUN apt-get update -y && apt-get install -y pgagent
COPY install-extensions.sh /docker-entrypoint-initdb.d
