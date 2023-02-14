package mgr.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app.config")
@Component
public class ConfigProperties {

    private boolean runInit = false;
    private boolean runScheduler = false;

    public boolean isRunInit() {
        return runInit;
    }

    public void setRunInit(boolean runInit) {
        this.runInit = runInit;
    }

    public boolean isRunScheduler() {
        return runScheduler;
    }

    public void setRunScheduler(boolean runScheduler) {
        this.runScheduler = runScheduler;
    }
}
