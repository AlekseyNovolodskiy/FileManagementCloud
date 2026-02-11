package learn.Cloud.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Getter
public class JwtAuthenticationToken implements Authentication {
    private final String token;
    private final UserDetails principal;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> claims;
    private boolean authenticated;


    public JwtAuthenticationToken(UserDetails principal, String token,
                                  Collection<? extends GrantedAuthority> authorities,
                                  Map<String, Object> claims) {
        this.principal = principal;
        this.token = token;
        this.authorities = authorities;
        this.claims = claims;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal != null ? principal.getUsername() : null;
    }

    public void setDetails(WebAuthenticationDetails webAuthenticationDetails) {
    }
}