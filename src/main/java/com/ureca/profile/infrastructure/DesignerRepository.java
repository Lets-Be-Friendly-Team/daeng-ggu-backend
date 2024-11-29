package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Designer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignerRepository extends JpaRepository<Designer, Long> {
    Optional<Designer> findByDesignerId(Long designerId);
}
