#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\')
package ${package}.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * JMS properties configuration.
 * 
 * https://github.com/dalifreire/jerimum
 * @since 11/2015
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Component
public class ApplicationConfigJms implements Serializable {

    private static final long serialVersionUID = 7896624401620243915L;

    @Value("${jms.connectionFactoryName}")
    private String connectionFactoryName;

    @Value("${jms.requestQueueName}")
    private String requestQueueName;

    @Value("${jms.responseQueueName}")
    private String responseQueueName;

    @Value("${jms.listenerQueueName}")
    private String listenerQueueName;

    @Value("${jms.timeout}")
    private Long timeout;

    @Value("${jms.concurrentConsumers}")
    private Long concurrentConsumers;

    @Value("${jms.maxConcurrentConsumers}")
    private Long maxConcurrentConsumers;

}
