package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_backups")
@Getter
@Setter
public class SystemBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "filepath", nullable = false)
    private String filepath;

    @Column(nullable = false)
    private Long size; // In bytes

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
