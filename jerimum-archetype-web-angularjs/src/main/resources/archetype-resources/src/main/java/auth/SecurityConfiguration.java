#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.jerimum.fw.auth.JerimumDefaultSecurityConfiguration;
import ${package}.util.Constants;
import java.util.Map;

/**
 * Security configuration for the application.
 * 
 * @author https://github.com/dalifreire/jerimum
 * @since 11/2015
 */
@EnableWebSecurity
public class SecurityConfiguration extends JerimumDefaultSecurityConfiguration {

	@Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }

    @Override
    protected String getLoginPage() {
        return Constants.LOGIN_PAGE;
    }

    @Override
    protected String[] getUnsecuredResources() {
        return new String[] { "/pages/open/**", "/resources/**", "/wro/**", "/" };
    }
    
    @Override
	protected String getAccessDeniedPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, String[]> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDefaultLoggedPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PasswordEncoder getPasswordEncoder() {
		// TODO Auto-generated method stub
		return null;
	}

}
