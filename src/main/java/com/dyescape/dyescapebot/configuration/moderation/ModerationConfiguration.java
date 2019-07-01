package com.dyescape.dyescapebot.configuration.moderation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class used for moderation specific configuration settings.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Configuration
@ConfigurationProperties(prefix = "moderation")
public class ModerationConfiguration {

    private long warningperiod;

    /**
     * Return a long, representing seconds, indicating how long a given warning should remain active.
     * @return Warning time
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public long getWarningperiod() {
        return this.warningperiod * 1000;
    }

    /**
     * Function used internally by Spring to load the configuration. Should not be called manually.
     * @deprecated Used internally by Spring
     * @param warningperiod
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public void setWarningperiod(long warningperiod) {
        this.warningperiod = warningperiod;
    }
}
