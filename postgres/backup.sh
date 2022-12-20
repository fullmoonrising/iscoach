#!/bin/bash

filename="backup-`date +%Y-%m-%d-%H-%M-%S`.sql"
docker exec -i db bash -c "pg_dump --username=$DB_USER --dbname=$DB_NAME --file=/var/lib/postgresql/data/backup/$filename --column-inserts --table=folo*"

echo "Folobot db backup `date +%Y-%m-%d-%H-%M-%S`" | mail -a "From: Folobot <folobot@folomkin.ru>" -s "Folobot db backup" aravin.roman@yandex.ru -A "./data/backup/$filename"

find ./data/backup/ -mtime +3 -delete