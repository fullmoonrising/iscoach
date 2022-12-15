#!/usr/bin/env bash

mkdir -v /var/lib/postgresql/data/backup

echo "enabling pg_agent on maintenance database"
psql -U $POSTGRES_USER --dbname=postgres <<-'EOSQL'
  create extension if not exists pgagent;
EOSQL

echo "creating backup job on maintenance database for $POSTGRES_DB database"
psql -U $POSTGRES_USER --dbname=postgres <<-'EOSQL'
  DO $$
  DECLARE
      jid integer;
      scid integer;
  BEGIN
  -- Creating a new job
  INSERT INTO pgagent.pga_job(
      jobjclid, jobname, jobdesc, jobhostagent, jobenabled
  ) VALUES (
      1::integer, 'backup'::text, ''::text, ''::text, true
  ) RETURNING jobid INTO jid;

  -- Steps
  -- Inserting a step (jobid: NULL)
  INSERT INTO pgagent.pga_jobstep (
      jstjobid, jstname, jstenabled, jstkind,
      jstconnstr, jstdbname, jstonerror,
      jstcode, jstdesc
  ) VALUES (
      jid, 'dump'::text, true, 'b'::character(1),
      ''::text, ''::name, 'f'::character(1),
      'pg_dump --username=$POSTGRES_USER --dbname=$POSTGRES_DB --file=/var/lib/postgresql/data/backup/backup-`date +%Y-%m-%d-%H-%M-%S`.sql --column-inserts --table=folo_user --table=folo_pidor --table=folo_var'::text, ''::text
  ) ;

  -- Schedules
  -- Inserting a schedule
  INSERT INTO pgagent.pga_schedule(
      jscjobid, jscname, jscdesc, jscenabled,
      jscstart,     jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
  ) VALUES (
      jid, 'daily'::text, ''::text, true,
      '2022-12-13 21:00:00+00'::timestamp with time zone,
      -- Minutes
      ARRAY[true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]::boolean[],
      -- Hours
      ARRAY[true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]::boolean[],
      -- Week days
      ARRAY[false,false,false,false,false,false,false]::boolean[],
      -- Month days
      ARRAY[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]::boolean[],
      -- Months
      ARRAY[false,false,false,false,false,false,false,false,false,false,false,false]::boolean[]
  ) RETURNING jscid INTO scid;
  END
  $$;
EOSQL

echo "starting pg_agent"
pgagent host=/var/run/postgresql dbname=postgres user=$POSTGRES_USER