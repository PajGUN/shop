	SHOP (spring-boot [amqp,web,data,mail], rabbitmq, postgresql, flyway, docker|docker-compose)

	# commit - 97765e4 использовать если хотите сразу запустить в контенерах, размер порядка 40Мб
	# commit - ca5002b аналогичен верхнему только без *.jar
	# commit - 3b97b4a без контейнеризации с конфигами под локальный postgre, rabbit

	О ПРОЕКТЕ

		Проект представляет из себя back-end магазина (в данном случае по продаже ПК комплектующих), целью которого было построение
	микросервисной архитектуры где каталог товаров он же склад будет отделеён от покупательской корзины. Данное разнесение функционала 
	позволяет совершить покупку уже имеющегося товара в корзине при недоступности каталога продукции. В свою очередь каталог сможет 
	корректно работать без сервиса корзины. 
		Проект идёт вместе с двумя модулями: шлюз банка в котором симулируется отказ или подтверждение денежных транзакций и 
	email-уведомитель клиентов о номере заказа и списка купленной продукции. Коммуникация между сервисами осуществляется
	посредствам Rabbit-MQ.


	ЗАПУСК ПРИЛОЖЕНИЯ

		Если запуск предполагается посредством docker то использовать комиит - 97765e4. Команда запуска - docker-compose up из домашней
	директории репозитория.
		Второй вариант запуска предполагает установленный локально брокер очередей rabbitmq , а так же postgresql server с созданными БД: 
	shop и shop-basket. Параметры учётных записей дополнительно необходимо скорректировать в конфигурационных файлах application.properties. 


Basic REST endpoits:

#Shop
<ip address:8080>/api/1.0/product
	GET 	/getbytype/{type} - получение всех продуктов заданного типа (пагинация)
	GET 	/getallbyproductname/{productName} - поиск продукции по ключевому слову (пагинация)
<ip address:8080>/api/1.0/count
	GET 	/getallbyproductid/{productId} - получение количества товара в магазинах

#Basket
<ip address:8090>/api/1.0/basket
	POST 	/addgoods	{body} - добавление товара в корзину
	DELETE 	/deletegoods{body} - удаление товара из корзины
	GET 	/get/{clientId} - получения списка товаров клиента
	GET 	createorder/{clientId}{body} - оформить заказ на товары в корзине
<ip address:8090>/api/1.0/order
	GET 	/getactiveorders/{clientId} - получение неоплаченных заказов
	GET 	/getarchiveorders/{clientId} - получение архивных заказов
	POST 	/payorder	{body} - оплатить заказ

#Bank
<ip address:8100>/api/1.0/bank
	PUT 	/changemode - изменяет режим ответа на платежи (одобрен/нет)

*это основные ключи для работы магазина, в проекте их больше






