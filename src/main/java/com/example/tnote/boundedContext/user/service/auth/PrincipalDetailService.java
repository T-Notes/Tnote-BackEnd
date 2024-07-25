package com.example.tnote.boundedContext.user.service.auth;



import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        return new PrincipalDetails(user);
    }
}
