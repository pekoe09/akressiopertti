package akressiopertti.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "AkressioperttiUser")
public class User extends BaseModel {
    
    @NotBlank(message = "Nimi ei saa olla tyhjä")
    private String name;
    @NotBlank(message = "Käyttäjänimi ei saa olla tyhjä")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "Salasana ei saa olla tyhjä")
    private String password;
    private String salt;
    private boolean isAdmin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    
}
