package ru.larna.services;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.larna.model.User;

/**
 * Класс обертка для хранения пользователей и порядка их поступления на обработку.
 * Пользователи идентифицируются  в коллекции по номеру их поступления, 
 *
 */
@Value
@AllArgsConstructor
class OrderedUser {
   /**
    * Пользователь
    */
   @EqualsAndHashCode.Exclude
   private final User user;
   /**
    * Порядковый номер
    * Этот номер представляет из себя счетчик и
    * для отдельного вызова UserMigration.migrate() для каждого получаемого пользователя уникален.
    */
   private final Long orderNum;
}
