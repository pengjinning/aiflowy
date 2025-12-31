package tech.aiflowy.starter;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import tech.aiflowy.common.spring.BaseApp;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFileStorage
public class MainApplication extends BaseApp {

    public static void main(String[] args) {
        run(MainApplication.class, args);
    }

}
