package ru.kusok_piroga.gorzdravbot.domain.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskDateLimitsTest {

    static Stream<Arguments> getValidData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return Stream.of(
                Arguments.of(
                        "10.10.2050",
                        List.of(
                                List.of(
                                        LocalDate.parse("01.01.2000", formatter),
                                        LocalDate.parse("10.10.2050", formatter)
                                )
                        ),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "10.10.2022-12.10.2022, 13.01.2020-09.09.2029 !18.10.2022-19.10.2022",
                        List.of(
                                List.of(
                                        LocalDate.parse("10.10.2022", formatter),
                                        LocalDate.parse("12.10.2022", formatter)
                                ),
                                List.of(
                                        LocalDate.parse("13.01.2020", formatter),
                                        LocalDate.parse("09.09.2029", formatter)
                                )
                        ),
                        List.of(
                                List.of(
                                        LocalDate.parse("18.10.2022", formatter),
                                        LocalDate.parse("19.10.2022", formatter)
                                )
                        )
                ),
                Arguments.of(
                        "10.10.2022-12.10.2022, 13.01.2020-09.09.2029 \n !18.10.2022-19.10.2022",
                        List.of(
                                List.of(
                                        LocalDate.parse("10.10.2022", formatter),
                                        LocalDate.parse("12.10.2022", formatter)
                                ),
                                List.of(
                                        LocalDate.parse("13.01.2020", formatter),
                                        LocalDate.parse("09.09.2029", formatter)
                                )
                        ),
                        List.of(
                                List.of(
                                        LocalDate.parse("18.10.2022", formatter),
                                        LocalDate.parse("19.10.2022", formatter)
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidData")
    void valid_range_data(String text, List<List<LocalDate>> include, List<List<LocalDate>> exclude) {
        try {
            TaskDateLimits dateLimits = new TaskDateLimits(text);
            assertThat(dateLimits.getIncludedLimits())
                    .isEqualTo(include);
            assertThat(dateLimits.getExcludedLimits())
                    .isEqualTo(exclude);

        } catch (DateLimitParseException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    static Stream<Arguments> getThrowsData() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("50.20.2020"),
                Arguments.of("20.12.2024 - "),
                Arguments.of("- 20.12.2024"),
                Arguments.of("1adasdasd2:00"),
                Arguments.of("20.12.2024 - sd 20.12.2025"),
                Arguments.of("22.12.2024 - 20.12.2024"),
                Arguments.of("!22.12.2024")
        );
    }

    @ParameterizedTest
    @MethodSource("getThrowsData")
    void check_throws(String text) {
        assertThatThrownBy(() -> new TaskDateLimits(text))
                .isInstanceOf(DateLimitParseException.class);
    }

    static Stream<Arguments> getSerializeData() {
        return Stream.of(
                Arguments.of("10.10.2022-12.10.2022,13.01.2020-09.09.2029 !18.10.2022-19.10.2022"),
                Arguments.of("10.10.2050")
        );
    }

    @ParameterizedTest
    @MethodSource("getSerializeData")
    void serialize_and_deserialize(String text) throws DateLimitParseException {
        TaskDateLimits dateLimits = new TaskDateLimits(text);
        assertThat(new TaskDateLimits(dateLimits.toString()))
                .isEqualTo(dateLimits);
    }

    static Stream<Arguments> getValidateData() {
        return Stream.of(
                Arguments.of(
                        "10.10.2022-12.10.2022",
                        "11.10.2022",
                        true
                ),
                Arguments.of(
                        "10.10.2022-12.10.2022",
                        "10.10.2022",
                        true
                ),
                Arguments.of(
                        "13.01.2020-09.09.2029",
                        "20.10.2029",
                        false
                ),
                Arguments.of(
                        "!17.10.2022-19.10.2022",
                        "18.10.2022",
                        false
                ),
                Arguments.of(
                        "!17.10.2022-19.10.2022",
                        "17.10.2022",
                        false
                ),
                Arguments.of(
                        "10.10.2022-12.10.2022 !10.10.2022-12.10.2022",
                        "11.10.2022",
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidateData")
    void validate_test(String limits, String date, boolean result) throws DateLimitParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        TaskDateLimits dateLimits = new TaskDateLimits(limits);
        LocalDate checkingDate = LocalDate.parse(date, formatter);

        assertThat(dateLimits.validateDate(checkingDate))
                .isSameAs(result);
    }


}