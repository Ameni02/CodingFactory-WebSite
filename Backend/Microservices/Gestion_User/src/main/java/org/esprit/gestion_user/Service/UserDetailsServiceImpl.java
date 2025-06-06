package org.esprit.gestion_user.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.esprit.gestion_user.Repositories.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
private final UserRepo userRepo;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
    }
}
