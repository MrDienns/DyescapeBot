package com.dyescape.dyescapebot.provider;

import javax.inject.Singleton;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.dyescape.dyescapebot.constant.Config;
import com.dyescape.dyescapebot.entity.discord.Punishment;
import com.dyescape.dyescapebot.entity.discord.Server;
import com.dyescape.dyescapebot.entity.discord.User;
import com.dyescape.dyescapebot.entity.discord.WarningAction;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

@Singleton
public class StatelessSessionProviderImpl implements StatelessSessionProvider {

    // -------------------------------------------- //
    // LOGGER
    // -------------------------------------------- //

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private StatelessSession cachedSession;

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void getStatelessSession(Vertx vertx, Handler<AsyncResult<StatelessSession>> resultHandler) {

        // Check cache
        if (this.cachedSession != null && this.cachedSession.isOpen()) {
            this.logger.debug("Using cached connection session");
            resultHandler.handle(Future.succeededFuture(this.cachedSession));
            return;
        }

        // TODO: Move config retrieving, as this is not SOLID (S)
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig(handler -> {

            JsonObject config = handler.result();

            vertx.executeBlocking(blockingHandler -> {
                logger.info("Building new connection session, this may take a moment...");

                Configuration configuration = new Configuration();

                // Connection details
                configuration.setProperty(Environment.DRIVER, com.mysql.jdbc.Driver.class.getCanonicalName());

                configuration.setProperty(Environment.URL, String.format("jdbc:mysql://%s:%s/%s",
                        config.getString(Config.MYSQL_HOST, Config.MYSQL_HOST_DEFAULT),
                        config.getInteger(Config.MYSQL_PORT, Config.MYSQL_PORT_DEFAULT),
                        config.getString(Config.MYSQL_DATABASE, Config.MYSQL_DATABASE_DEFAULT)));

                configuration.setProperty(Environment.USER,
                        config.getString(Config.MYSQL_USERNAME, Config.MYSQL_USERNAME_DEFAULT));

                configuration.setProperty(Environment.PASS,
                        config.getString(Config.MYSQL_PASSWORD, Config.MYSQL_PASSWORD_DEFAULT));

                configuration.setProperty(Environment.DIALECT, org.hibernate.dialect.MySQL57Dialect.class.getCanonicalName());

                // Behaviour
                configuration.setProperty(Environment.HBM2DDL_AUTO, "update");

                // Mapping entity classes
                configuration.addAnnotatedClass(Punishment.class);
                configuration.addAnnotatedClass(Server.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(WarningAction.class);

                // Create the factory
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());

                this.cachedSession = sessionFactory.openStatelessSession();

                blockingHandler.handle(Future.succeededFuture(this.cachedSession));
            }, blockingHandlerResult -> {
                resultHandler.handle(Future.succeededFuture((StatelessSession) blockingHandlerResult.result()));
            });
        });
    }
}
