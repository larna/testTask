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
import ru.larna.model.User;
import ru.larna.util.parsers.UserParser;
import ru.larna.util.parsers.UserParserImpl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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
        List<String> capturedArguments = argumentCaptor.getAllValues();
        Set<User> actualUsers = getUsersFromOutput(capturedArguments);

        Assertions.assertAll(() -> assertEquals(expectedUsersCount, userMigration.getActualUsersCount()),
                () -> assertEquals(arg.getExpected(), actualUsers));
    }

    private Set<User> getUsersFromOutput(List<String> capturedArguments) {
        UserParser parser = new UserParserImpl();
        return capturedArguments.stream()
                .map(parser::parse)
                .collect(Collectors.toSet());
    }

    private static class FakeDataPortion implements Answer<String> {
        private final List<String> fakeData;
        private Integer currentReadIndex;

        public FakeDataPortion(List<String> fakeData) {
            this.fakeData = fakeData;
            this.currentReadIndex = 0;
        }

        @Override
        public String answer(InvocationOnMock invocationOnMock){
            if (currentReadIndex < fakeData.size())
                return fakeData.get(currentReadIndex++);
            return "";
        }
    }
}