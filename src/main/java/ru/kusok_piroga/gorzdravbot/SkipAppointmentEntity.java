package ru.kusok_piroga.gorzdravbot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;

@Getter
@Setter
@Entity
@Table(name = "skip_appointment")
@AllArgsConstructor
@NoArgsConstructor
public class SkipAppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(name = "appointment_id", nullable = false)
    private String appointmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkipAppointmentEntity that = (SkipAppointmentEntity) o;
        return getTask().equals(that.getTask()) && getAppointmentId().equals(that.getAppointmentId());
    }

    @Override
    public int hashCode() {
        int result = getTask().hashCode();
        result = 31 * result + getAppointmentId().hashCode();
        return result;
    }
}