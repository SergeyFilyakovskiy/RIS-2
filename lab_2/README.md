# Запуск и демонстрация


+ База данных

``` bash
sudo psql -u postgres -c "CREATE DATABASE inventory_var27;"

```

+ Запуск приложения

``` bash
mvn spring-boot:run
```

Веб-интерфейс: http://localhost:8080/inventory   (переключение языка: ?lang=en)

+ Тесты

``` bash
mvn test
```

+ GET: все записи

``` bash
curl -s http://localhost:8080/api/inventory
```

+ POST: создание (201 + Location)

``` bash
curl -s -i -X POST http://localhost:8080/api/inventory \
  -H 'Content-Type: application/json' \
  -d '{"inventoryNumber":"INV-0001","name":"Проектор","room":"305","quantity":1,"status":"WORKING"}'
```

+ POST: ошибка валидации -> 400 с пояснением по каждому полю

```bash
curl -s -X POST http://localhost:8080/api/inventory \
  -H 'Content-Type: application/json' \
  -d '{"inventoryNumber":"","name":"P","room":"","quantity":0}'
```

{"timestamp":"...","status":400,"error":"Bad Request","message":"Validation failed",
"fieldErrors":[{"field":"inventoryNumber","message":"Инв. номер не должен быть пустым"}, ...]}

+ POST: дубликат номера -> 409
+ GET/DELETE несуществующего id -> 404 с текстом, битый JSON -> 400 с текстом