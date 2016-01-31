package akressiopertti.service;

import akressiopertti.domain.User;
import akressiopertti.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findOne(Long id){
        return userRepository.findOne(id);
    }
    
    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User save(User user){
        return userRepository.save(user);
    }
    
    public User getAuthenticatedUser(){
        User user = null;
        try {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
            String username = authToken.getPrincipal().toString();
            user = userRepository.findByUsername(username);
        }
        catch (Exception exc) {
        }

        return user;
    }
}
