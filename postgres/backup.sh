#!/bin/bash

comand="pg_dump --username=$DB_USER --dbname=$DB_NAME --file=/var/lib/postgresql/data/backup/backup-`date +%Y-%m-%d-%H-%M-%S`.sql --column-inserts --table=folo*"
echo $comand
docker exec -i db bash -c "pg_dump --username=$DB_USER --dbname=$DB_NAME --file=/var/lib/postgresql/data/backup/backup-`date +%Y-%m-%d-%H-%M-%S`.sql --column-inserts --table=folo*"