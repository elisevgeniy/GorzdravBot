package ru.kusok_piroga.gorzdravbot.domain.models;

import lombok.Getter;
import lombok.Setter;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.TimeLimitParseException;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class TaskTimeLimits implements Serializable {
    private List<List<LocalTime>> includedLimits = new ArrayList<>();
    private List<List<LocalTime>> excludedLimits = new ArrayList<>();

    /**
     * @param rawLimitString string with type "(чч:мм - чч:мм, чч:мм - чч:мм)?(\n| )?(!чч:мм - чч:мм, чч:мм - чч:мм)?"
     */
    public TaskTimeLimits(String rawLimitString) throws TimeLimitParseException {
        if (rawLimitString.isEmpty()) return;

        int indexOfNegativeSign = rawLimitString.indexOf("!");
        switch (indexOfNegativeSign) {
            case 0 -> parseExcludedLimits(rawLimitString.substring(1));
            case -1 -> parseIncludedLimits(rawLimitString);
            default -> {
                parseIncludedLimits(rawLimitString.substring(0, indexOfNegativeSign));
                parseExcludedLimits(rawLimitString.substring(indexOfNegativeSign + 1));
            }
        }
    }

    private void parseIncludedLimits(String s) throws TimeLimitParseException {
        includedLimits = getRanges(s);
        validateRangesOrThrow(includedLimits);
    }

    private void parseExcludedLimits(String s) throws TimeLimitParseException {
        excludedLimits = getRanges(s);
        validateRangesOrThrow(excludedLimits);
    }

    private List<List<LocalTime>> getRanges(String s) throws TimeLimitParseException {
        try {
            return Arrays.stream(s.split(","))
                    .map(this::getRange)
                    .toList();
        } catch (DateTimeParseException e) {
            throw new TimeLimitParseException();
        }
    }

    private List<LocalTime> getRange(String s) {
        return Arrays.stream(s.split("-"))
                .map(String::trim)
                .map(LocalTime::parse)
                .toList();
    }

    private void validateRangesOrThrow(List<List<LocalTime>> ranges) throws TimeLimitParseException {
        for (var range : ranges) {
            validateRangeOrThrow(range);
        }
    }

    private void validateRangeOrThrow(List<LocalTime> range) throws TimeLimitParseException {
        if (range.size() == 1 ||
            !range.get(0).isBefore(range.get(1)) &&
            !range.get(0).equals(range.get(1))) {
            throw new TimeLimitParseException();
        }
    }

    public boolean validateTime(LocalTime time) {
        for (var range : includedLimits) {
            if (!(
                    time.equals(range.get(0)) ||
                    time.equals(range.get(1)) ||
                    time.isAfter(range.get(0)) &&
                    time.isBefore(range.get(1))
            )) {
                return false;
            }
        }
        for (var range : excludedLimits) {
            if (
                    time.equals(range.get(0)) ||
                    time.equals(range.get(1)) ||
                    time.isAfter(range.get(0)) &&
                    time.isBefore(range.get(1))
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (!includedLimits.isEmpty()) {
            includedLimits.forEach(range -> result
                    .append(range.get(0).toString())
                    .append("-")
                    .append(range.get(1).toString())
                    .append(",")
            );
            result.deleteCharAt(result.length() - 1);
        }

        if (!excludedLimits.isEmpty()) {
            result.append(" !");
            excludedLimits.forEach(range -> result
                    .append(range.get(0).toString())
                    .append("-")
                    .append(range.get(1).toString())
                    .append(",")
            );
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString().trim();
    }
}
