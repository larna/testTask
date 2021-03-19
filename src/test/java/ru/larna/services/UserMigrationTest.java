package ru.larna.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.larna.model.Email;
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@DisplayName("Класс UserMigration")
@ExtendWith(MockitoExtension.class)
class UserMigrationTest {
    @Mock
    private IOService ioService;
    private UserMigration userMigration;
    private final UserParser parser = new UserParserImpl();

    @BeforeEach
    public void init() {
        userMigration = new UserMigration(ioService, parser);
    }

    @DisplayName("Должен корректно определять дубликаты пользователей по повторяющимся у них email и производить слияние")
    @ParameterizedTest
    @ArgumentsSource(FakeDataArgumentProvider.class)
    public void shouldCorrectMigrateUsers(FakeDataArgumentProvider.FakeDataArgument arg) throws IOException {
        FakeDataPortion mockAnswer = new FakeDataPortion(arg.getFakeData());
        given(ioService.read()).willAnswer(mockAnswer);

        final int expectedUsersCount = arg.getExpected().size();
        userMigration.migrate();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService, times(expectedUsersCount)).write(argumentCaptor.capture());
        List<String> capturedArguments = argumentCaptor.<String>getAllValues();
        List<User> actualUsers = getUsersFromOutput(capturedArguments);

        Assertions.assertAll(() -> assertEquals(expectedUsersCount, userMigration.getActualUsersCount()),
                () -> assertEquals(true, equalsFakeUsers(arg.getExpected(), actualUsers)));
    }

    private boolean equalsFakeUsers(List<User> users1, List<User> users2) {
        if (users1 == null || users2 == null)
            return true;

        int size1 = users1 != null ? users1.size() : 0;
        int size2 = users2 != null ? users2.size() : 0;
        if (size1 != size2)
            return false;

        for (int i = 0; i < users1.size(); i++) {
            User user1 = users1.get(i);
            User user2 = users2.get(i);
            if (!user1.getName().equals(user2.getName()))
                return false;
            //сравнение email, только их наличия, порядок не важен
            HashSet<Email> emails1 = new HashSet<Email>(user1.getEmails());
            HashSet<Email> emails2 = new HashSet<Email>(user2.getEmails());
            if (!emails1.equals(emails2))
                return false;
        }
        return true;
    }

    private List<User> getUsersFromOutput(List<String> capturedArguments) {
        UserParser parser = new UserParserImpl();
        return capturedArguments.stream()
                .map(parser::parse)
                .collect(Collectors.toList());
    }

    private static class FakeDataPortion implements Answer<String> {
        private final List<String> fakeData;
        private Integer currentReadIndex;

        public FakeDataPortion(List<String> fakeData) {
            this.fakeData = fakeData;
            this.currentReadIndex = 0;
        }

        @Override
        public String answer(InvocationOnMock invocationOnMock) throws Throwable {
            if (currentReadIndex < fakeData.size())
                return fakeData.get(currentReadIndex++);
            return "";
        }
    }
}