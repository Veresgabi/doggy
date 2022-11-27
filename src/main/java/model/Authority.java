package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "authority")
public class Authority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq")
    private Long authorityId;

    @Column(length = 100, unique = true)
    @NotNull
    private String authorityName;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<User> users;

    public GrantedAuthority grantedAuthority() {
        return new SimpleGrantedAuthority(this.authorityName);
    }
}
