package tech.aiflowy.job.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("tech.aiflowy.job.mapper")
public class JobModuleConfig {

    public JobModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-job");
    }
}
