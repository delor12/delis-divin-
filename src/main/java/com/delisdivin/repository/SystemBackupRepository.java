package com.delisdivin.repository;

import com.delisdivin.entity.SystemBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SystemBackupRepository extends JpaRepository<SystemBackup, Long> {
    List<SystemBackup> findTop10ByOrderByCreatedAtDesc();
}
