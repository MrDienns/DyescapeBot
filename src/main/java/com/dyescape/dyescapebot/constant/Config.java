package com.dyescape.dyescapebot.constant;

/**
 * Class containing constants of all base configuration options
 * that this application makes use of. Note that different platform
 * implementations of this bot may have their own set of configuration
 * options. Those platform specific options are not included in this
 * class.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public final class Config {

    private Config() {  }

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    public static final String API_TOKEN = "API_TOKEN";

    /**
     * Configuration path used to configure what the MySQL username is.
     * See {@link #MYSQL_USERNAME_DEFAULT} on what the default value is.
     * @since 0.1.0
     */
    public static final String MYSQL_USERNAME = "MYSQL_USERNAME";
    public static final String MYSQL_USERNAME_DEFAULT = "root";

    /**
     * Configuration path used to configure what the MySQL password is.
     * See {@link #MYSQL_PASSWORD_DEFAULT} on what the default value is.
     * @since 0.1.0
     */
    public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
    public static final String MYSQL_PASSWORD_DEFAULT = "password";

    /**
     * Configuration path used to configure what the MySQL host is.
     * See {@link #MYSQL_HOST_DEFAULT} on what the default value is.
     * @since 0.1.0
     */
    public static final String MYSQL_HOST = "MYSQL_HOST";
    public static final String MYSQL_HOST_DEFAULT = "localhost";

    /**
     * Configuration path used to configure what the MySQL host is.
     * See {@link #MYSQL_HOST_DEFAULT} on what the default value is.
     * @since 0.1.0
     */
    public static final String MYSQL_PORT = "MYSQL_PORT";
    public static final Integer MYSQL_PORT_DEFAULT = 3306;

    /**
     * Configuration path used to configure what the MySQL database is.
     * See {@link #MYSQL_HOST_DEFAULT} on what the default value is.
     * @since 0.1.0
     */
    public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
    public static final String MYSQL_DATABASE_DEFAULT = "dyescapebot";
}
