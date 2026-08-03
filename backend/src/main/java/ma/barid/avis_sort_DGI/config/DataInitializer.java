package ma.barid.avis_sort_DGI.config;

import ma.barid.avis_sort_DGI.entity.Role;
import ma.barid.avis_sort_DGI.entity.User;
import ma.barid.avis_sort_DGI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@baridalmaghrib.ma");
            admin.setRole(Role.ADMIN_POSTE);
            userRepository.save(admin);
            System.out.println("Admin par defaut cree : admin / admin123");
        }
    }
}