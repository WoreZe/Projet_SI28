package fr.utc.multeract.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import fr.utc.multeract.server.converter.HashMapConverter;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "_users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String username;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Column(length = 25)
    private String firstName;

    @JsonIgnore
    @Column(length = 25)
    private String lastName;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> avatar;

    @Column
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    private List<UserRole> roles;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = List.of(UserRole.USER);
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    @JsonValue
    public Map<String, Object> toJson() {
        return new HashMap<>() {{
            put("id", id);
            put("username", username);
            put("email", email);
            put("firstName", firstName);
            put("lastName", lastName);
            put("avatar", avatar);
        }};
    }
}
