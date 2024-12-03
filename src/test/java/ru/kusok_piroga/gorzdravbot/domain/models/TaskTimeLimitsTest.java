package ru.kusok_piroga.gorzdravbot.domain.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskTimeLimitsTest {

    static Stream<Arguments> getValidData() {
        return Stream.of(
                Arguments.of(
                        "10:00-12:00, 13:00-16:00 !09:00-09:30",
                        List.of(
                                List.of(
                                        LocalTime.parse("10:00"),
                                        LocalTime.parse("12:00")
                                ),
                                List.of(
                                        LocalTime.parse("13:00"),
                                        LocalTime.parse("16:00")
                                )
                        ),
                        List.of(
                                List.of(
                                        LocalTime.parse("09:00"),
                                        LocalTime.parse("09:30")
                                )
                        )
                ),
                Arguments.of(
                        "10:00 - 12:00,13:00 -16:00 \n ! 09:00- 09:30",
                        List.of(
                                List.of(
                                        LocalTime.parse("10:00"),
                                        LocalTime.parse("12:00")
                                ),
                                List.of(
                                        LocalTime.parse("13:00"),
                                        LocalTime.parse("16:00")
                                )
                        ),
                        List.of(
                                List.of(
                                        LocalTime.parse("09:00"),
                                        LocalTime.parse("09:30")
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidData")
    void valid_data(String text, List<List<LocalTime>> include, List<List<LocalTime>> exclude) {
        try {
            TaskTimeLimits timeLimits = new TaskTimeLimits(text);
            assertThat(timeLimits.getIncludedLimits())
                    .isEqualTo(include);
            assertThat(timeLimits.getExcludedLimits())
                    .isEqualTo(exclude);

        } catch (TimeLimitParseException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    static Stream<Arguments> getThrowsData() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("10:00 - "),
                Arguments.of("- 12:00,13:00 -16:00 \n ! 09:00- 09:30"),
                Arguments.of("10:00 - 1adasdasd2:00,13:00 -16:00 \n ! 09:00- 09:30"),
                Arguments.of("10:00 -sd 12:00,13:00 -16:00 \n ! 09:00- 09:30"),
                Arguments.of("12:00 - 10:00,13:00 -16:00 \n ! 09:00- 09:30")
        );
    }

    @ParameterizedTest
    @MethodSource("getThrowsData")
    void check_throws(String text) {
        assertThatThrownBy(() -> new TaskTimeLimits(text))
                .isInstanceOf(TimeLimitParseException.class);
    }

    static Stream<Arguments> getSerializeData() {
        return Stream.of(
                Arguments.of("10:00-12:00,13:00-16:00 !09:00-09:30"),
                Arguments.of("10:00-12:00"),
                Arguments.of("!09:00-09:30")
        );
    }

    @ParameterizedTest
    @MethodSource("getSerializeData")
    void serialize_and_deserialize(String text) throws TimeLimitParseException {
        assertThat(new TaskTimeLimits(text))
                .hasToString(text);
    }
}