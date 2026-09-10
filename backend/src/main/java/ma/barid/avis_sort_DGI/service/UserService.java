package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.dto.ChangePasswordRequest;
import ma.barid.avis_sort_DGI.dto.UserResponse;
import ma.barid.avis_sort_DGI.entity.User;
import ma.barid.avis_sort_DGI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.isActif()))
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse toggleActif(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve : " + id));
        user.setActif(!user.isActif());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActif());
    }
    public Optional<UserResponse> getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.isActif()));
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changerMotDePasse(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getAncienMotDePasse(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        if (request.getNouveauMotDePasse() == null || request.getNouveauMotDePasse().length() < 6) {
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        user.setPassword(passwordEncoder.encode(request.getNouveauMotDePasse()));
        userRepository.save(user);
    }
}