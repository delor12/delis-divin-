package com.delisdivin.service;

import com.delisdivin.entity.SystemBackup;
import java.util.List;

public interface BackupService {
    SystemBackup backupDatabase();
    List<SystemBackup> getBackups();
    void deleteBackup(Long id);
}
