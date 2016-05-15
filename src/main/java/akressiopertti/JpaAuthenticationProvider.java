
package akressiopertti;

import java.util.ArrayList;
import java.util.List;
import akressiopertti.domain.User;
import akressiopertti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken)a;
        String username = a.getPrincipal().toString();
        String password = a.getCredentials().toString();

        User user = userService.findByUsername(username);

        if (user == null) {
            throw new AuthenticationException("Unable to authenticate user " + username) {
            };
        }

        if(!password.equals(user.getPassword())){
            throw new AuthenticationException("Unable to authenticate user " + username){};
        }
        // salasana-tarkistus vaihdettava kun salasanat kryptataan
//        if (!BCrypt.hashpw(password, user.getSalt()).equals(user.getPassword())) {
//            throw new AuthenticationException("Unable to authenticate user " + username) {
//            };
//        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        if(user.getIsAdmin()){
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new UsernamePasswordAuthenticationToken(user.getUsername(), password, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }
}