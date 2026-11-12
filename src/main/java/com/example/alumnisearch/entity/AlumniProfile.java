package com.example.alumnisearch.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alumni_profiles", indexes = {
    @Index(name = "idx_alumni_univ_role", columnList = "university, job_role"),
    @Index(name = "idx_alumni_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "job_role")
    private String currentRole;

    @Column(nullable = false)
    private String university;

    private String location;

    @Column(name = "linkedin_headline", length = 1000)
    private String linkedinHeadline;

    @Column(name = "passout_year")
    private Integer passoutYear;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AlumniProfile(String name, String currentRole, String university, String location, String linkedinHeadline, Integer passoutYear) {
        this.name = name;
        this.currentRole = currentRole;
        this.university = university;
        this.location = location;
        this.linkedinHeadline = linkedinHeadline;
        this.passoutYear = passoutYear;
    }
}
