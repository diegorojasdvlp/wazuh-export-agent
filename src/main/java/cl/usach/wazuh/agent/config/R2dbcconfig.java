package cl.usach.wazuh.agent.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class R2dbcconfig extends AbstractR2dbcConfiguration {

    @Value("${DB_HOST}")
    private String dbHost;
    @Value("${DB_PORT}")
    private Integer dbPort;
    @Value("${DB_NAME}")
    private String dbName;
    @Value("${DB_USER}")
    private String dbUsername;
    @Value("${DB_PASSWORD}")
    private String dbPassword;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, dbHost)
                .option(PORT, dbPort)
                .option(DATABASE, dbName)
                .option(USER, dbUsername)
                .option(PASSWORD, dbPassword)
                .build()
        );
    }
}
