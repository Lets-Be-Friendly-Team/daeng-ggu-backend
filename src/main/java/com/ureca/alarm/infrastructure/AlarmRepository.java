package com.ureca.alarm.infrastructure;

import com.ureca.alarm.domain.Alarm;
import com.ureca.review.domain.Enum.AuthorType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByReceiverIdAndAlarmStatus(Long receiverId, Boolean unread);

    Page<Alarm> findByReceiverIdAndReceiverType(
            Long receiverId, AuthorType receiverType, Pageable pageable);

    @Query(
            "SELECT a.objectId FROM Alarm a WHERE a.receiverId = :receiverId AND a.alarmType = :alarmType")
    List<Long> findObjectIdByReceiverIdAndAlarmType(
            @Param("receiverId") Long receiverId, @Param("alarmType") String alarmType);

    @Query(
            "SELECT a FROM Alarm a WHERE a.receiverId = :receiverId AND a.receiverType = :receiverType AND a.alarmStatus = :alarmStatus")
    List<Alarm> findByReceiverIdAndReceiverTypeAndAlarmStatus(
            @Param("receiverId") Long receiverId,
            @Param("receiverType") AuthorType receiverType,
            @Param("alarmStatus") boolean alarmStatus);
}
