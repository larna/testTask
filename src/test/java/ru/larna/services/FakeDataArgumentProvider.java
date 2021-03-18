package ru.larna.services;

import lombok.Value;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

public class FakeDataArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(fakeDataOneUserResult()),
                Arguments.of(fakeDataTwoUserResult()),
                Arguments.of(fakeDataThreeUserResult())
        );
    }

    private FakeDataArgument fakeDataOneUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user3@mail.ru",
                "user3 -> user6@mail.ru, user7@mail.ru, user1@mail.ru",
                "user3 -> user8@mail.ru, user9@mail.ru, user4@mail.ru");
        String expected = "user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user4@mail.ru, user5@mail.ru, " +
                "user6@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        return new FakeDataArgument(fakeData, List.of(expected));
    }

    private FakeDataArgument fakeDataTwoUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user8@mail.ru, user9@mail.ru, user4@mail.ru");
        final String expectedOne = "user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        final String expectedTwo = "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru";
        return new FakeDataArgument(fakeData, List.of(expectedOne, expectedTwo));
    }

    private FakeDataArgument fakeDataThreeUserResult() {
        List<String> fakeData = List.of("user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru",
                "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru",
                "user3 -> user7@mail.ru, user8@mail.ru, user1@mail.ru",
                "user4 -> user44@mail.ru, user41@mail.ru, user4@mail.ru",
                "user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru",
                "user6 -> user8@mail.ru, user9@mail.ru");
        final String expectedOne = "user1 -> user1@mail.ru, user3@mail.ru, user2@mail.ru, user7@mail.ru, user8@mail.ru, user9@mail.ru";
        final String expectedTwo = "user2 -> user4@mail.ru, user5@mail.ru, user6@mail.ru, user44@mail.ru, user41@mail.ru";
        final String expectedThree = "user5 -> user55@mail.ru, user51@mail.ru, user52@mail.ru";
        return new FakeDataArgument(fakeData, List.of(expectedOne, expectedTwo, expectedThree));
    }

    @Value
    public class FakeDataArgument {
        private final List<String> fakeData;
        private final List<String> expected;
    }
}
