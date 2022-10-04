package ODIN.base.common.aop;

import ODIN.base.domain.annotation.CostTime;
import ODIN.base.domain.enumeration.UnitType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * TODO
 * 2022/2/16 zhoutao
 */
@Aspect
@Component//使用spring容器进行管理
@Slf4j
public class CostTimeAspect {


    /**
     * 首先定义一个切点
     */
    @org.aspectj.lang.annotation.Pointcut("@annotation(ODIN.base.domain.annotation.CostTime)")
    public void countTime() {

    }

    @Around("countTime()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object obj = null;
        try {
            // 获取目标类名
            String targetName = joinPoint.getTarget().getClass().getName();
            // 获取方法名
            String methodName = joinPoint.getSignature().getName();

            // 生成类对象
            Class targetClass = Class.forName(targetName);
            // 获取该类中的方法
            Method method = targetClass.getMethod(methodName);
            // 获得注解
            CostTime costTime = method.getAnnotation(CostTime.class);

            long startTime = System.nanoTime();
            // 方法运行
            obj = joinPoint.proceed();
            long endTime = System.nanoTime();
            // 运行完成后
            UnitType timeType = costTime.timeType();

            log.info(costTime.msg() + "=" + String.format("%.2f",
                    (((float) (endTime - startTime)) / timeType.getQuantity())
            ) + timeType.getName());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }
}
