package configuration;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class DbSessionConfig {

    @Bean
    public Session dbSession() {
        Configuration config = new Configuration();
        // comment out these two rows below for local debugging
        // config.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        // config.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        config.configure();
        // local SessionFactory bean created
        SessionFactory sessionFactory = config.buildSessionFactory();

        return sessionFactory.openSession();
    }

    @Bean
    public EntityManager entityManager(Session dbSession) {
        return dbSession().getEntityManagerFactory().createEntityManager();
    }
}
