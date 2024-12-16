package com.ureca.streaming.infrastructure;

import com.ureca.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRepository extends JpaRepository<Request, Long> {}
