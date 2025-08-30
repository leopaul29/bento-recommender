package com.leopaul29.bento.config;

import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class TestDataBuilder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createTestUser(String username, String password, Role role) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
    }

    public User createAndSaveTestUser(String username, Role role) {
        User user = createTestUser(username, "password123", role);
        return userRepository.save(user);
    }

    public User createAndSaveDisabledUser(String username) {
        User user = createTestUser(username, "password123", Role.USER);
        user.setEnabled(false);
        return userRepository.save(user);
    }

//    public User createAndSaveLockedUser(String username, String password) {
//        User user = createTestUser(username, password, Role.USER);
//        user.set
//        return userRepository.save(user);
//    }

    public UserPrincipal createUserPrincipal(User user) {
        return new UserPrincipal(user);
    }

//    public UserPrincipal createUserPrincipal(User user, boolean enabled, boolean accountNonLocked) {
//        UserPrincipal userPrincipal = new UserPrincipal(user);
//        userPrincipal.setUser(user);
//        // Si UserPrincipal a des setters pour ces propriétés
//        // Sinon, tu peux avoir un constructeur avec ces paramètres
//        return userPrincipal;
//    }

    // Mock pour les tests de sécurité spécifiques
    public UserPrincipal createLockedUserPrincipal(User user) {
        // Si tu as besoin de tester un compte verrouillé avant d'implémenter la DB
        // tu peux créer un mock ou une version test de UserPrincipal
        return new UserPrincipal(user) {
            @Override
            public boolean isAccountNonLocked() {
                return false;
            }
        };
    }
}