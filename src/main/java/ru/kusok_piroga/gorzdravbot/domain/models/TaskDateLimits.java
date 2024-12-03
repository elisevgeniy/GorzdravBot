package ru.kusok_piroga.gorzdravbot.domain.models;

import lombok.Getter;
import lombok.Setter;
import ru.kusok_piroga.gorzdravbot.domain.exceptions.DateLimitParseException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskDateLimits implements Serializable {
    private List<List<LocalDate>> includedLimits = new ArrayList<>();
    private List<List<LocalDate>> excludedLimits = new ArrayList<>();

    /**
     * @param rawLimitString string with type "(дд.мм.гггг|(дд.мм.гггг-дд.мм.гггг, дд.мм.гггг-дд.мм.гггг)?(\n| )?(!дд.мм.гггг-дд.мм.гггг, дд.мм.гггг-дд.мм.гггг)?)"
     */
    public TaskDateLimits(String rawLimitString) throws DateLimitParseException {
        if (rawLimitString.trim().length() == 10) {
            includedLimits.add(List.of(
                    LocalDate.now().minusDays(1),
                    this.parseDate(rawLimitString)
            ));
        } else {
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
    }

    private void parseIncludedLimits(String s) throws DateLimitParseException {
        includedLimits = getRanges(s);
        validateRangesOrThrow(includedLimits);
    }

    private void parseExcludedLimits(String s) throws DateLimitParseException {
        excludedLimits = getRanges(s);
        validateRangesOrThrow(excludedLimits);
    }

    private List<List<LocalDate>> getRanges(String s) throws DateLimitParseException {
        try {
            List<List<LocalDate>> list = new ArrayList<>();
            for (String rangeStr : s.split(",")) {
                list.add(getRange(rangeStr));
            }
            return list;
        } catch (DateTimeParseException e) {
            throw new DateLimitParseException(e.getMessage());
        }
    }

    private List<LocalDate> getRange(String s) throws DateLimitParseException {
        List<LocalDate> list = new ArrayList<>();
        for (String limit : s.split("-")) {
            list.add(parseDate(limit.trim()));
        }
        return list;
    }
    
    private LocalDate parseDate(String s) throws DateLimitParseException {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e){
            throw new DateLimitParseException(e.getMessage());
        }
    }

    private void validateRangesOrThrow(List<List<LocalDate>> ranges) throws DateLimitParseException {
        for (var range : ranges) {
            validateRangeOrThrow(range);
        }
    }

    private void validateRangeOrThrow(List<LocalDate> range) throws DateLimitParseException {
        if (range.size() == 1 ||
            !range.get(0).isBefore(range.get(1)) &&
            !range.get(0).equals(range.get(1))) {
            throw new DateLimitParseException("validate error. Val 1 = %s, val 2 = %s".formatted(
                    range.get(0).toString(),
                    (range.size() == 1) ? null : range.get(1).toString()));
        }
    }

    public boolean validateDate(LocalDate date) {
        for (var range : includedLimits) {
            if (!(
                    date.equals(range.get(0)) ||
                    date.equals(range.get(1)) ||
                    date.isAfter(range.get(0)) &&
                    date.isBefore(range.get(1))
            )) {
                return false;
            }
        }
        for (var range : excludedLimits) {
            if (
                    date.equals(range.get(0)) ||
                    date.equals(range.get(1)) ||
                    date.isAfter(range.get(0)) &&
                    date.isBefore(range.get(1))
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        StringBuilder result = new StringBuilder();

        if (!includedLimits.isEmpty()) {
            includedLimits.forEach(range -> result
                    .append(range.get(0).format(formatter))
                    .append("-")
                    .append(range.get(1).format(formatter))
                    .append(",")
            );
            result.deleteCharAt(result.length() - 1);
        }

        if (!excludedLimits.isEmpty()) {
            result.append(" !");
            excludedLimits.forEach(range -> result
                    .append(range.get(0).format(formatter))
                    .append("-")
                    .append(range.get(1).format(formatter))
                    .append(",")
            );
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDateLimits that = (TaskDateLimits) o;
        return includedLimits.equals(that.includedLimits) && excludedLimits.equals(that.excludedLimits);
    }

    @Override
    public int hashCode() {
        int result = includedLimits.hashCode();
        result = 31 * result + excludedLimits.hashCode();
        return result;
    }
}
