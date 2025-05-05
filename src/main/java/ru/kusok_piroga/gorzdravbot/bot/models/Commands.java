package ru.kusok_piroga.gorzdravbot.bot.models;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Commands {
    public static final String COMMAND_START = "/start";
    public static final String COMMAND_ADD_PATIENT = "/addpatient";
    public static final String COMMAND_LIST_PATIENT = "/listpatient";
    public static final String COMMAND_ADD_TASK = "/addtask";
    public static final String COMMAND_LIST_TASK = "/listtask";
    public static final String COMMAND_DELETE_TASK = "/deletetask";
    public static final String COMMAND_CHANGE_TASK = "/changetask";
    public static final String COMMAND_ADD_REFERRAL = "/addreferral";
}
