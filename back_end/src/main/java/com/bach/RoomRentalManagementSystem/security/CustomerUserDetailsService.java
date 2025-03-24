package com.bach.RoomRentalManagementSystem.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import com.bach.RoomRentalManagementSystem.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService{

	private final IUserRepository userRepository ;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found !"));
        return  user ;

    }
	
}
