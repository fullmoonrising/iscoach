alter table price_list
    add column category varchar(64),
    add column photo_url varchar(256),
    add column file_url varchar(256);

update price_list set category = 'SERVICE';

alter table price_list
    alter column category set not null,
    alter column name set not null,
    alter column description set not null;

update price_list
    set description = 'Вам нужна поддержка и понимание в сложной жизненной ситуации? Не оставайтесь один на один с вашими проблемами, доверьтесь Ирэн и начните путь к психологическому благополучию уже сегодня!'
    where id = 'CONSULTATION';
update price_list
    set description = 'Хотите изменить свою жизнь к лучшему? Начните с 1 сессии! Ирэн поможет раскрыть Ваш потенциал, достичь целей и преодолеть преграды на пути к успеху. Вы получите внимание и практические инструменты. Не откладывайте свое счастье, начинайте сейчас!'
    where id = 'SESSION';
update price_list
    set description = 'Эта сессия поможет Вам вспомнить и осознать прошлое, разобраться в его влиянии на Вашу жизнь сегодня и освободиться от эмоциональных и психологических барьеров. Откройте новые возможности, глубокое понимание себя. Начните новую главу Вашей жизни!'
    where id = 'REGRESSION';
update price_list
    set description = 'Пакет из 5 сессий - это уникальная возможность получить сопровождение, поддержку и шаги для достижения целей. Улучшить личные отношения, карьеру, самооценку. Не упустите возможность преобразить свою жизнь с пакетом из 5 сессий!'
    where id = 'PACKAGE';

insert into price_list (id, category, "order", name, description, photo_url,file_url, amount) values
    ('MEDITATION01', 'DIGITAL_GOODS', 101, 'Медитация интуиции',
    'Откройте свою внутреннюю мудрость и разблокируйте свою интуицию с помощью медитации. Позвольте себе быть в гармонии со своим внутренним "я". Откройте свой потенциал и позвольте интуиции стать вашим надежным проводником в жизни.',
    'https://iscoach.ru/images/meditation/meditation01.jpg', '/meditation/meditation01.mp3', 777 * 100),
    ('MEDITATION02', 'DIGITAL_GOODS', 102, 'Медитация самоценности',
     'Авторская практика: сонастройка ценности, позволит Вам укрепить связь с самим собой, раскрыть свою внутреннюю силу и обрести ясность мысли. Ощутить прилив энергии и активировать внутренние силы.',
     'https://iscoach.ru/images/meditation/meditation02.jpg', '/meditation/meditation02.mp3', 777 * 100),
    ('MEDITATION03', 'DIGITAL_GOODS', 103, 'Настройка на лучший день',
    'Эта практика нацелена на то, чтобы выстроить Вашу энергию и намерение на положительный и продуктивный день. Углубляться в свои внутренние мысли и желания, отпуская все негативные эмоции и сомнения.',
    'https://iscoach.ru/images/meditation/meditation03.jpg', '/meditation/meditation03.mp3', 777 * 100);