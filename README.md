# Таможенное оформление с Camunda BPM и XML/XSLT

Автоматизация обработки таможенной декларации: валидация XML, классификация товаров, расчёт пошлины и формирование отчёта инспектора.

## Цель проекта

Демонстрация навыка работы с Camunda BPM и XML/XSLT. Решение выполняет:

- приём декларации через REST API;
- проверку структуры по XSD-схеме;
- определение ставки пошлины по коду товара;
- расчёт итоговой пошлины через XSLT-трансформацию;
- генерацию HTML-отчёта инспектора.

## Технологии

- **Java 19**
- **Spring Boot** 
- **Camunda BPM 7.21.0** (управление процессом)
- **XSLT / XML / XSD** (трансформации и валидация, стандартный `javax.xml`)
- **Liquibase** (создание справочной таблицы `CUSTOMS_RATES`)
- **H2 in‑memory** (демонстрационная БД)
- **Maven** (сборка)

> **Примечание:** В демонстрационных целях используется H2. Для промышленной эксплуатации замените его на Oracle (или другую целевую СУБД) в `application.yml`.

## Как запустить

1. Убедитесь, что установлена JDK 19+ и Maven.
2. Склонируйте репозиторий.
3. Выполните команду:
   
   `mvn spring-boot:run`

## Как протестировать

После запуска приложения можно с помощью Postman направить POST запрос на `http://localhost:8080/api/customs/declaration/process` с таможенной декларацией в формате XML (представлена ниже) в теле запроса. 

> **Примечание:** В демонстрационных целях в декларации используются 2 товара с разными кодами. В результате теста должен быть получен отчёт в формате HTML (в Postman можно отрисовать во вкладке Preview) с таблицей содержащей товары, пошлины (на основании ставки из таблицы `CUSTOMS_RATES`) по каждой категории товаров и итоговую сумму всех пошлин.

### Пример таможенной декларации в формате XML:

```<?xml version="1.0" encoding="UTF-8"?>
<customsDeclaration
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="declaration.xsd">
    <declarationId>10702030/140522/0012345</declarationId>
    <declarant>ООО «Импортёр»</declarant>
    <goods>
        <item>
            <code>8471300000</code>
            <name>Ноутбуки</name>
            <quantity>100</quantity>
            <customsValue>5000000</customsValue>
            <origin>Китай</origin>
        </item>
        <item>
            <code>6204623100</code>
            <name>Смартфоны</name>
            <quantity>100</quantity>
            <customsValue>7000000</customsValue>
            <origin>Китай</origin>
        </item>
    </goods>
</customsDeclaration>
```
## Скриншоты

### Отчёт:

<img width="1366" height="588" alt="image" src="https://github.com/user-attachments/assets/abaf56b8-27b4-46c7-a53a-a30d4d3c1d47" />

### Схема бизнес-процесса:

<img width="1460" height="295" alt="image" src="https://github.com/user-attachments/assets/777e2473-3595-4c40-8d7d-5d26891424a1" />

