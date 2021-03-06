## Тестовое задание
### Цель
Требуется написать работающий код, решающий задачу, и приложить инструкцию, как код собрать и запустить.
Также надо написать unittest-ы и сделать возможность получать входные данные для задачи из stdin в формате как в примере ниже.
Задачу реализовать на Kotlin(Java).

### Условия задачи
Имеется n пользователей, каждому из них соответствует список email-ов (всего у всех пользователей m email-ов).
Например:
```
user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru
user2 -> foo@gmail.com, ups@pisem.net
user3 -> xyz@pisem.net, vasya@pupkin.com
user4 -> ups@pisem.net, aaa@bbb.ru
user5 -> xyz@pisem.net
```
Считается, что если у двух пользователей есть общий email, значит это один и тот же пользователь. 
Требуется построить и реализовать алгоритм, выполняющий слияние пользователей. 
На выходе должен быть список пользователей с их email-ами (такой же как на входе).
В качестве имени объединенного пользователя можно брать любое из исходных имен. 
Список email-ов пользователя должен содержать только уникальные email-ы.
Параметры n и m произвольные, длина конкретного списка email-ов никак не ограничена.
Требуется, чтобы асимптотическое время работы полученного решения было линейным, или близким к линейному.
Возможный ответ на задачу в указанном примере:
```
user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru, ups@pisem.net, aaa@bbb.ru
user3 -> xyz@pisem.net, vasya@pupkin.com
```
ВАЖНО:
1. Программа должна полностью удовлетворять формату входных/выходных данных
2. Входные данные завершаются пустой строкой
3. Программа должна собираться в jar и запускаться через "java -jar"
4. Решения, не удовлетворяющие перечисленным требованиям, будут отправлены на доработку.

### Сборка программы
Для сборки требуется maven.
Сборка программы производиться с помощью команды:
```sh
mvn package
``` 

### Запуск программы

Запуск приложения с предварительной генерацией фейковых данных и проверкой работы приложения 
на сгенерированных данных  
```sh
java -jar target/test-1.0-SNAPSHOT-jar-with-dependencies.jar --fake-data
```
Фейковые данные сохраняются в файлы с названиями: fakeData-N%d.txt

Выходные данные сохраняются в файлы с названиями: out-N%d.txt

---
Запуск приложения, где данные поступают через stdin
```sh
java -jar target/test-1.0-SNAPSHOT-jar-with-dependencies.jar < yourData.txt
```
Выходные данные выводятся в stdout

