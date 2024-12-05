package com.ureca.alarm.infrastructure;

import com.ureca.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByReceiverIdAndAlarmStatus(Long receiverId, Boolean unread);
}
