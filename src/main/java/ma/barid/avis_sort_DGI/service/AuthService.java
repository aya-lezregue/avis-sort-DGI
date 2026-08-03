package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.config.JwtService;
import ma.barid.avis_sort_DGI.dto.*;
import ma.barid.avis_sort_DGI.entity.User;
import ma.barid.avis_sort_DGI.repository.UserRepository;
import ma.barid.avis_sort_DGI.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtService.generateToken(
                user.getUsername(), user.getRole().name());

        return new LoginResponse(token, user.getRole().name(), user.getUsername());
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username déjà utilisé");
        }

        String motDePasseGenere = PasswordGenerator.generate(10);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(motDePasseGenere));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        userRepository.save(user);

        System.out.println("Utilisateur enregistré");

        emailService.envoyerIdentifiants(
                request.getEmail(),
                request.getUsername(),
                motDePasseGenere,
                request.getRole().name()
        );
        System.out.println("Méthode email appelée");

        return "Utilisateur créé avec succès. Envoi des identifiants en cours.";
    }
}