package model;

import dto.UserSummary;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(length = 50, unique = true)
    @NotNull
    private String username;

    @Column(length = 100, unique = true)
    @NotNull
    @Email
    private String email;

    @Column(length = 200)
    private String password;

    @NotNull
    private Boolean enabled;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "userId", referencedColumnName = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "authorityId", referencedColumnName = "authorityId")})
    private Set<Authority> authorities;

    public UserSummary toUserSummary() {
        UserSummary userSummary = new UserSummary();
        userSummary.setEmail(this.email);
        userSummary.setUserId(this.userId);
        return userSummary;
    }
}
