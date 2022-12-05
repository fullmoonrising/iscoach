update folo_pidor set score = 0, last_win_date = '1900-01-01', messages_per_day = 0;

delete from folo_var where type = 'LAST_FOLOPIDOR_DATE' or type = 'LAST_FOLOPIDOR_USERID';
