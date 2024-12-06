package com.ureca.alarm.infrastructure;

import com.ureca.alarm.domain.Alarm;
import com.ureca.review.domain.Enum.AuthorType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByReceiverIdAndAlarmStatus(Long receiverId, Boolean unread);

    Page<Alarm> findByReceiverIdAndReceiverType(
            Long receiverId, AuthorType receiverType, Pageable pageable);
    List<Long> findObjectIdByReceiverIdAndAlarmType(Long designerId, String a1);
}
