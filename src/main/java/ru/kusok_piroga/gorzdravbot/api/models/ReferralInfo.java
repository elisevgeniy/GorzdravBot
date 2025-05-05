package ru.kusok_piroga.gorzdravbot.api.models;

import java.time.LocalDateTime;
import java.util.List;

public record ReferralInfo(
        Integer lpuId,
        String lpuShortName,
        String lpuFullName,
        String lpuAddress,
        String lpuPhone,
        String patId,
        String lastName,
        String firstName,
        String middleName,
        String polisN,
        String polisS,
        LocalDateTime birthDate,
        String homePhoneNumber,
        String mobilePhoneNumber,
        String emai,
        List<Specialty> specialities
) {
    @Override
    public String toString() {
        return "ReferralInfo{" +
                "lpuId=" + lpuId +
                ", lpuShortName='" + lpuShortName + '\'' +
                ", lpuFullName='" + lpuFullName + '\'' +
                ", lpuAddress='" + lpuAddress + '\'' +
                ", lpuPhone='" + lpuPhone + '\'' +
                ", patId='" + patId + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", polisN='" + polisN + '\'' +
                ", polisS='" + polisS + '\'' +
                ", birthDate=" + birthDate +
                ", homePhoneNumber='" + homePhoneNumber + '\'' +
                ", mobilePhoneNumber='" + mobilePhoneNumber + '\'' +
                ", emai='" + emai + '\'' +
                ", specialities=" + specialities +
                '}';
    }
}
