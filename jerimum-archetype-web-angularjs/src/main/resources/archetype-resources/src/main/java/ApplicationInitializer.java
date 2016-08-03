#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.springframework.core.annotation.Order;

import br.com.jerimum.fw.config.JerimumWebApplicationInitializer;

/**
 * Startup spring application class.
 * 
 * @author https://github.com/dalifreire/jerimum
 * @since 11/2015
 */
@Order(1)
public class ApplicationInitializer extends JerimumWebApplicationInitializer {

    @Override
    public Class<?> getConfigurationClass() {
        return Application.class;
    }

	@Override
	public String getEnvironmentJVMParam() {
		// TODO Auto-generated method stub
		return null;
	}

    
}
