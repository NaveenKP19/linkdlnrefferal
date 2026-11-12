package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts", uniqueConstraints = {@UniqueConstraint(name = "uk_user_account_email", columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private  String lastName;

    @Column(nullable = false, length = 200)
    private  String email;

    @Column(nullable = false, length = 250)
    private  String password;

    @Column(name = "phone_number", nullable = false, length = 20)
    private  String phoneNumber;

    @Column(nullable = false,length = 150)
    private String location;

    @Column(name = "linkedin_profile_url", nullable = false, length = 800)
    private  String linkedinProfileUrl;

    @Column(nullable = false)
    private  Boolean active=true;

    @Column(name = "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private  LocalDateTime updatedAt;

    @PrePersist
    protected  void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        createdAt =now;
        updatedAt =now;
    }

    @PrePersist
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
