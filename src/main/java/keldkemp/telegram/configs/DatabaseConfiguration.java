package keldkemp.telegram.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import keldkemp.telegram.logging.SimpleProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.Closeable;
import java.util.Arrays;

@SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "keldkemp.telegram")
public class DatabaseConfiguration {

    private final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Autowired
    private Environment env;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean(destroyMethod = "close")
    @Primary
    public DataSource dataSource() {
        DataSource defaultDataSource = getDataSource(dataSourceProperties, "default");
        DataSource dataSource;
        dataSource = defaultDataSource;

        logger.debug("Datasource(s) configured. The next step can be slow: Hibernate initializes the metadata " +
                "according to the scheme...(especially on remote databases)");

        return dataSource;
    }

    private DataSource getDataSource(DataSourceProperties dataSourceProperties, String name) {
        String jndi = dataSourceProperties.getJndiName();
        String url = dataSourceProperties.getUrl();
        logger.debug("Configuring Datasource: {}. Url: {}. JNDI-name: {}.", name, url, jndi);
        if (url == null && jndi == null) {
            logger.error("Your database connection pool configuration is incorrect! The application" +
                            " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly. " +
                    "Datasource.url and Datasource.jndi-name is null");
        }

        HikariConfig config = new HikariConfig();

        if (!StringUtils.hasText(jndi)) {
            //smart driver class name detection
            config.setDataSourceClassName(org.postgresql.ds.PGSimpleDataSource.class.getName());
            config.addDataSourceProperty("url", url);
            config.setUsername(dataSourceProperties.getUsername());
            config.setPassword(dataSourceProperties.getPassword());
            config.addDataSourceProperty("tcpKeepAlive", true);
            config.setConnectionTestQuery("SELECT 1");
        } else {
            config.setDataSourceJNDI(dataSourceProperties.getJndiName());
        }

        HikariDataSource hikariDataSource;
        try {
            hikariDataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Could not configure data source \"" + name + "\"", e);
        }

        return wrapDataSourceLogging(hikariDataSource);
    }

    /**
     * wrapDataSourceLogging.
     */
    private <T extends DataSource & Closeable> DataSource wrapDataSourceLogging(T baseDataSource) {
        SimpleProxyDataSourceBuilder builder = SimpleProxyDataSourceBuilder
                .create(baseDataSource)
                .name("dataSourceName")
                .logQueryBySlf4j();

        //allowing methods tracing
        //builder.traceMethods();

        return builder.build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransaction = new JpaTransactionManager();
        jpaTransaction.setEntityManagerFactory(entityManagerFactory);

        return jpaTransaction;
    }
}
