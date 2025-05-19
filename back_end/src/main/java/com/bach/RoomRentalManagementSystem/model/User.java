package com.bach.RoomRentalManagementSystem.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name", length = 50, nullable = false)
    private String fullName;
    @Column(name = "email", length = 100, nullable = false)
    private String email;
    @Column(name = "phone_number", length = 10)
    private String phone;
    @Column(name = "password", nullable = false)
    private String passwordHash;
    @Column(name = "identity_number", length = 12)
    private String identityNumber;
    @Column(name = "is_password_changed",  nullable = false)
    private Boolean IsPasswordChanged = true;



    private String address;
    private Date dateOfBirth;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_role", referencedColumnName = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<RentalContract> contracts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    public User(String email, String pass, Object role) {
        this.email = email;
        this.passwordHash = pass;
        this.role = (Role) role;
        this.fullName = "User";
        this.createdAt = LocalDateTime.now();
    }
}
