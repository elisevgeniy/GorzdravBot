package ru.kusok_piroga.gorzdravbot.common.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kusok_piroga.gorzdravbot.common.models.PatientEntity;
import ru.kusok_piroga.gorzdravbot.common.models.PatientState;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<PatientEntity, Long> {
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
