package ru.larna.services;

import lombok.Value;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FakeDataArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(fakeDataOneUserResult()),
                Arguments.of(fakeDataTwoUserResult()),
                Arguments.of(fakeDataThreeUserResult()),
                Arguments.of(fakeDataCircleAssignUsersResult()),
                Arguments.of(fakeDataAnotherOneTestUsersResult()),
                Arguments.of(fakeDataAnotherTwoTestUsersResult())
        );
    }

    private FakeDataArgument fakeDataOneUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user3@mail.ru",
                "user3 -> user6@mail.ru, user7@mail.ru, user1@mail.ru",
                "user3 -> user8@mail.ru, user9@mail.ru, user4@mail.ru");
        String expected = "user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user4@mail.ru, " +
                "user5@mail.ru, user6@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        return new FakeDataArgument(fakeData, List.of(expected));
    }

    private FakeDataArgument fakeDataTwoUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user8@mail.ru, user9@mail.ru, user3@mail.ru");

        String expected1 = "user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        String expected2 = "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru";
        return new FakeDataArgument(fakeData, List.of(expected1, expected2));
    }

    private FakeDataArgument fakeDataThreeUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user2@mail.ru, user3@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user44@mail.ru, user41@mail.ru, user4@mail.ru",
                "user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru",
                "user6 -> user8@mail.ru, user9@mail.ru");

        String expected1 = "user1 -> user1@mail.ru, user2@mail.ru, user3@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        String expected2 = "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru, user44@mail.ru, user41@mail.ru";
        String expected3 = "user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru";

        return new FakeDataArgument(fakeData, List.of(expected1, expected2, expected3));
    }

    private FakeDataArgument fakeDataCircleAssignUsersResult() {
        List<String> fakeData = List.of("user_0 -> email_5@gmail.com, email_1@gmail.com",
                "user_6 -> email_3@gmail.com, email_4@gmail.com",
                "user_5 -> email_4@gmail.com, email_5@gmail.com",
                "user_1 -> email_1@gmail.com, email_2@gmail.com");
        String expected = "user_0 -> email_5@gmail.com, email_1@gmail.com, email_3@gmail.com, email_4@gmail.com, email_2@gmail.com";
        return new FakeDataArgument(fakeData, List.of(expected));
    }

    private FakeDataArgument fakeDataAnotherOneTestUsersResult() {
        List<String> fakeData = List.of("user_0 -> email_1@gmail.com, email_2@gmail.com",
                "user_1 -> email_3@gmail.com, email_4@gmail.com",
                "user_2 -> email_9@gmail.com, email_6@gmail.com",
                "user_3 -> email_7@gmail.com, email_1@gmail.com",
                "user_4 -> email_8@gmail.com, email_4@gmail.com",
                "user_5 -> email_2@gmail.com, email_3@gmail.com");
        String expected1 = "user_0 -> email_1@gmail.com, email_2@gmail.com, email_7@gmail.com, email_3@gmail.com, email_4@gmail.com, email_8@gmail.com";
        String expected2 = "user_2 -> email_9@gmail.com, email_6@gmail.com";
        return new FakeDataArgument(fakeData, List.of(expected1, expected2));
    }

    private FakeDataArgument fakeDataAnotherTwoTestUsersResult() {
        List<String> fakeData = List.of(
                "user_1 -> email_1@gmail.com, email_2@gmail.com, email_3@gmail.com",
                "user_2 -> email_4@gmail.com, email_5@gmail.com, email_6@gmail.com",
                "user_3 -> email_7@gmail.com, email_8@gmail.com, email_9@gmail.com",
                "user_4 -> email_10@gmail.com, email_11@gmail.com, email_12@gmail.com",
                "user_5 -> email_14@gmail.com, email_5@gmail.com, email_13@gmail.com",
                "user_6 -> email_15@gmail.com, email_16@gmail.com, email_10@gmail.com",
                "user_7 -> email_11@gmail.com, email_1@gmail.com, email_17@gmail.com",
                "user_8 -> email_18@gmail.com, email_19@gmail.com, email_20@gmail.com",
                "user_9 -> email_21@gmail.com, email_15@gmail.com, email_22@gmail.com",
                "user_10 -> email_23@gmail.com, email_24@gmail.com, email_4@gmail.com");
        String expected1 = "user_1 -> email_1@gmail.com, email_2@gmail.com, email_3@gmail.com, email_10@gmail.com, email_11@gmail.com, email_12@gmail.com, email_15@gmail.com, email_16@gmail.com, email_17@gmail.com, email_21@gmail.com, email_22@gmail.com";
        String expected2 = "user_2 -> email_4@gmail.com, email_5@gmail.com, email_6@gmail.com, email_14@gmail.com, email_13@gmail.com, email_23@gmail.com, email_24@gmail.com";
        String expected3 = "user_3 -> email_7@gmail.com, email_8@gmail.com, email_9@gmail.com";
        String expected4 = "user_8 -> email_18@gmail.com, email_19@gmail.com, email_20@gmail.com";
        return new FakeDataArgument(fakeData, List.of(expected1, expected2, expected3, expected4));
    }

    @Value
    public static class FakeDataArgument {
        private final List<String> fakeData;
        private final List<String> expected;
    }
}
