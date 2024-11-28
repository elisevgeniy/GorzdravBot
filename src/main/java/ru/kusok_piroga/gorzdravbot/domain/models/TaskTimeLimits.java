package ru.kusok_piroga.gorzdravbot.domain.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class TaskTimeLimits implements Serializable {
    private List<List<LocalTime>> includedLimits = Collections.emptyList();
    private List<List<LocalTime>> excludedLimits = Collections.emptyList();

    /**
     * @param rawLimitString string with type "(чч:мм - чч:мм, чч:мм - чч:мм)?(\n| )?(!чч:мм - чч:мм, чч:мм - чч:мм)?"
     */
    public TaskTimeLimits(String rawLimitString) {
        int indexOfNegativeSign = rawLimitString.indexOf("!");
        switch (indexOfNegativeSign) {
            case 0 -> parseExcludedLimits(rawLimitString.substring(1));
            case -1 -> parseIncludedLimits(rawLimitString);
            default -> {
                parseIncludedLimits(rawLimitString.substring(0, indexOfNegativeSign));
                parseExcludedLimits(rawLimitString.substring(indexOfNegativeSign+1));
            }
        }
    }

    private void parseIncludedLimits(String s) {
        includedLimits = getRanges(s);
    }

    private void parseExcludedLimits(String s) {
        excludedLimits = getRanges(s);
    }

    private List<List<LocalTime>> getRanges(String s){
        return Arrays.stream(s.split(","))
                .map(this::getRange)
                .toList();
    }

    private List<LocalTime> getRange(String s){
        List<LocalTime> range = Arrays.stream(s.split("-"))
                .map(String::trim)
                .map(LocalTime::parse)
                .toList();
        // todo: validate range
        return range;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
