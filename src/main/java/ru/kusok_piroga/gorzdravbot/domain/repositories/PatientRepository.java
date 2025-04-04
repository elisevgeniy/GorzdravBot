package ru.kusok_piroga.gorzdravbot.domain.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.PatientState;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<PatientEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
    @Query("select p from PatientEntity p where p.id = ?1")
    Optional<PatientEntity> findByIdWithLock(@NonNull Long id);

    @Query("select p from PatientEntity p where p.dialogId = ?1 and p.state <> ?2")
    Optional<PatientEntity> findByDialogIdAndStateNot(Long dialogId, PatientState state);


    @Query("select p from PatientEntity p where p.dialogId = ?1 and p.state = ?2")
    List<PatientEntity> findByDialogIdAndState(Long dialogId, PatientState state);

    default List<PatientEntity> findCompletedByDialogId(Long dialogId){
        return findByDialogIdAndState(dialogId, PatientState.COMPLETED);
    }

    @Transactional
    @Modifying
    @Query("delete from PatientEntity t where t.dialogId = ?1 and not t.state = ?2 ")
        // todo: add limit 1 for PostgreSQL
    void deleteByDialogIdAndStateIsNot(Long dialogId, PatientState state);
}
