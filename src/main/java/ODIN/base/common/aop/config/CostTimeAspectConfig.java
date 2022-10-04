package ODIN.base.common.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOC配置类
 * 2022/3/13 zhoutao
 */
@Configuration
@ComponentScan("ODIN.base.common.aop")
@EnableAspectJAutoProxy
public class CostTimeAspectConfig {
}
