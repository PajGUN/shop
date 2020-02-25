insert into client
    (birthday_date, created_date, email, first_name,
     last_name, middle_name, phone_num)
values ('1984-02-23', '2020-01-03', 'zip02@gmail.com',
        'Sergey', 'Markov', 'Michailovich', 79229993455),
       ('1984-03-16', '2020-01-03', 'zip03@gmail.com',
        'Sergey', 'Govrov', 'Andreevich', 79226593535),
       ('1985-04-13', '2020-01-03', 'zip01@yandex.ru',
        'Pavel', 'Sitnikov', 'Vladimirovich', 79213662244);

--

insert into store (address, name)
values ('Свтелановский пр.79', 'Северный'),
       ('Комендантский пр.4', 'Западный'),
       ('Московский пр.65', 'Южный');

--

insert into basket_unit (client_id, product_id,
                         product_name, price, quantity_buy)
values (1,1,'видеокарта RTX2080',70000,1),
       (1,3,'оперативная память Kingston DDR4 8Gb',3500,2),
       (1,5,'жёсткий диск Seagate Baracuda 1Tb',3300,1),
       (2,2,'видеокарта 1070GTX',16000,1),
       (2,3,'оперативная память Kingston DDR4 8Gb',3500,2),
       (2,4,'жёсткий диск Seagate Baracuda 4Tb',8300,1);

--

insert into product_count (number_interested, product_id,
                           quantity_stock, store_id)
values (1,1,3,1), (1,1,2,2), (1,1,1,3),
       (2,3,3,1), (2,3,6,2), (2,3,1,3),
       (1,5,3,1), (1,5,2,2), (1,5,0,3),
       (1,2,1,1), (1,2,1,2), (1,2,3,3),
       (1,4,3,1), (1,4,3,2), (1,4,1,3);



