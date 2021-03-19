package ru.larna.services;

import lombok.Value;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.util.List;
import java.util.stream.Stream;

public class FakeDataArgumentProvider implements ArgumentsProvider {
    private final UserParser parser = new UserParserImpl();

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(fakeDataOneUserResult()),
                Arguments.of(fakeDataTwoUserResult()),
                Arguments.of(fakeDataThreeUserResult()),
                Arguments.of(fakeDataCircleAssignUsersResult())
        );
    }

    private FakeDataArgument fakeDataOneUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user3@mail.ru",
                "user3 -> user6@mail.ru, user7@mail.ru, user1@mail.ru",
                "user3 -> user8@mail.ru, user9@mail.ru, user4@mail.ru");
        User expected = parser.parse("user3 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user4@mail.ru, " +
                "user5@mail.ru, user6@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru");
        return new FakeDataArgument(fakeData, List.of(expected));
    }

    private FakeDataArgument fakeDataTwoUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user8@mail.ru, user9@mail.ru, user3@mail.ru");

        User expectedOne = parser.parse("user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru");
        User expectedTwo = parser.parse("user4 -> user1@mail.ru, user2@mail.ru, user3@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru");
        return new FakeDataArgument(fakeData, List.of(expectedOne, expectedTwo));
    }

    private FakeDataArgument fakeDataThreeUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user44@mail.ru, user41@mail.ru, user4@mail.ru",
                "user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru",
                "user6 -> user8@mail.ru, user9@mail.ru");

        User expectedOne = parser.parse("user4 -> user4@mail.ru, user5@mail.ru, user6@mail.ru, user44@mail.ru, user41@mail.ru");
        User expectedTwo = parser.parse("user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru");
        User expectedThree = parser.parse("user6 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru");

        return new FakeDataArgument(fakeData, List.of(expectedOne, expectedTwo, expectedThree));
    }

    private FakeDataArgument fakeDataCircleAssignUsersResult() {
        List<String> fakeData = List.of("user_0 -> email_5@gmail.com, email_1@gmail.com",
                "user_6 -> email_3@gmail.com, email_4@gmail.com",
                "user_5 -> email_4@gmail.com, email_5@gmail.com",
                "user_1 -> email_1@gmail.com, email_2@gmail.com");
        List<Email> emails = List.of(new Email("email_1@gmail.com"), new Email("email_2@gmail.com"),
                new Email("email_3@gmail.com"), new Email("email_4@gmail.com"), new Email("email_5@gmail.com"));
        User expected = User.builder().name("user_1").emails(emails).build();
        return new FakeDataArgument(fakeData, List.of(expected));
    }

    @Value
    public class FakeDataArgument {
        private final List<String> fakeData;
        private final List<User> expected;
    }
}
