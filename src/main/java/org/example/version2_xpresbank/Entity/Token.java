package org.example.version2_xpresbank.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @SuppressWarnings("unused")
    @Column(name = "access_token")
    private String accessToken;

    @SuppressWarnings("unused")
    @Column(name = "refresh_token")
    private String refreshToken;

    @SuppressWarnings("unused")
    @Column(name = "is_logged_out")
    private boolean loggedOut;

    @SuppressWarnings("unused")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
