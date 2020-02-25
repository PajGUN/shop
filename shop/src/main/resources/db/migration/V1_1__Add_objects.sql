insert into store (address, name)
values ('Свтелановский пр.79', 'Северный'),
       ('Комендантский пр.4', 'Западный'),
       ('Московский пр.65', 'Южный');

--

insert into product (article_number, type,
                         product_name, price)
values (10035600,'VIDEO_CARD','видеокарта RTX2080',70000),
       (10265400,'RAM','оперативная память Kingston DDR4 8Gb',3500),
       (10278900,'HDD_SSD','жёсткий диск Seagate Baracuda 1Tb',3300),
       (26572200,'VIDEO_CARD','видеокарта 1070GTX',16000),
       (85664200,'HDD_SSD','жёсткий диск Seagate Baracuda 4Tb',8300);

--

insert into product_count (product_id, quantity, store_id)
values (1,3,1), (1,2,2), (1,1,3),
       (3,3,1), (3,6,2), (3,1,3),
       (5,3,1), (5,2,2), (5,0,3),
       (2,1,1), (2,1,2), (2,3,3),
       (4,3,1), (4,3,2), (4,1,3);



