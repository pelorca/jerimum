#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package ${package}.auth;

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
