package com.ureca.alarm.infrastructure;

import com.ureca.alarm.domain.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {
    List<AlarmHistory> findByReceiverIdAndAlarmStatus(Long receiverId, Boolean unread);
}
