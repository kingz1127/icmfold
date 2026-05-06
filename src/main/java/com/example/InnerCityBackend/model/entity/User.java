
package com.example.InnerCityBackend.model.entity;

import com.example.InnerCityBackend.model.enums.Gender;
import com.example.InnerCityBackend.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    private String phone;

    private String country;

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Set to true to allow access
    }

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Override
    public boolean isEnabled() {
        return true; 
    }

    @Column(name = "kingschat_id", unique = true)
    private String kingschatId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "email_verified")
    private boolean emailVerified;

    private String address;
    private String city;
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

}