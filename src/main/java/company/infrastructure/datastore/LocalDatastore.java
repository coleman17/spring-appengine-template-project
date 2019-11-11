package company.infrastructure.datastore;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the local emulator for cloud datastore.
 * <p>
 * The appengine datastore emulator is a separate OS process, and as such
 * does not die when the local appengine development server shuts down.
 * This leads to orphaned processes that would pile up and slow a system.
 * Normal use of this class will create one datastore emulator for the jvm, and shut id down as the jvm
 * exits. This graceful shutdown is only going to work for a normal sigterm style shutdown. A sigkill will leave
 * the emulator running.
 */
@Configuration
@Profile({"test", "local"})
@Slf4j
public class LocalDatastore implements InitializingBean {

    public static final String PROJECT_ENV_NAME = "GOOGLE_CLOUD_PROJECT";
    public static final String DEFAULT_LOCAL_PROJECT_ID = "app-local";

    private static final Lock lock = new ReentrantLock();
    private static LocalDatastoreHelper helper = LocalDatastoreHelper.create(1.0);
    private static Datastore datastore;

    //we're in the shutdown routine of the jvm, there's nothing more we can do with an interrupted exception
    @SuppressWarnings("squid:S2142")
    @VisibleForTesting
    static void stop() {

        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                tryToStopDatastore();
            } else {
                throw new IllegalMonitorStateException("Can't acquire lock. Shutdown of datastore failed.");
            }
        } catch (InterruptedException e) {
            log.error("Attempt to shutdown datastore failed", e);
        } finally {
            lock.unlock();
        }
    }

    private static void tryToStopDatastore() {
        if (datastore != null) {
            try {
                log.info("STOPPING LOCAL DATASTORE");
                //note this stop method removes the /tmp/gcd<whatever> directory that the emulator uses
                //but if the helper does not detect the gcloud sdk in your PATH, then it will download and
                //leave it in /tmp/cloud-datastore-emulator- every time you run tests
                helper.stop();
                datastore = null;
            } catch (Exception e) {
                log.error("Attempt to shutdown datastore failed", e);
            }
        }

    }

    private static void startDatastore() throws IOException, InterruptedException {
        DatastoreOptions datastoreOptions = helper.getOptions().toBuilder()
                // helper doesn't even try to resolve project-id, so we set up the most correct for us
                .setProjectId(getProjectId()).build();
        datastore = datastoreOptions.getService();
        helper.start();
        log.info("Started datastore emulator using project-id: {} on port: {}",
                datastore.getOptions().getProjectId(), helper.getPort());
    }

    /**
     * @return the Google Appengine current "project id". This will be
     * something like "lims-us-oe-tf4089bb-exp". Or something like
     * "lims-order-entry-local" if running locally.
     */
    private static String getProjectId() {
        // the current project id was historically called the application Id
        String appId = SystemProperty.applicationId.get();
        // appId is forced to be 'no_app_id' when run locally https://github.com/GoogleCloudPlatform/appengine-plugins-core/issues/439
        if (StringUtils.hasText(appId) && !"no_app_id".equals(appId)) {
            return appId;
        } else {
            String genvName = System.getProperty(PROJECT_ENV_NAME, System.getenv(PROJECT_ENV_NAME));
            if (StringUtils.hasText(genvName)) {
                return genvName;
            }
        }
        return DEFAULT_LOCAL_PROJECT_ID;
    }

    @Bean("datastore")
    @Profile("test")
    public Datastore datastore() throws IOException, InterruptedException {
        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                if (datastore == null) {
                    startDatastore();
                    registerShutdownHook();
                }
                return datastore;
            } else {
                throw new IllegalMonitorStateException("Can't acquire lock. Creation of datastore failed.");
            }
        } finally {
            lock.unlock();
        }
    }

    @Bean("datastore")
    @Profile({"local & !test"})
    public Datastore datastoreEmulator() {
        return DatastoreOptions.getDefaultInstance().toBuilder()
                .setHost("http://localhost:8081")
                .build()
                .getService();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(LocalDatastore::stop));
    }

    @Override
    public void afterPropertiesSet() {
        // resides here because exceptions about missing project-id usually throws datastore
        SystemProperty.applicationId.set(getProjectId());
    }
}
