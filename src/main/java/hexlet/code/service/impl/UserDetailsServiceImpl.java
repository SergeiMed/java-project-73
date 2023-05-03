package hexlet.code.service.impl;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));
        User user = userRepository.findByEmail(email).get();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
}
