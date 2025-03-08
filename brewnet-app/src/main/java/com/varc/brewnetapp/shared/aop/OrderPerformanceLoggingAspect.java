package com.varc.brewnetapp.shared.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OrderPerformanceLoggingAspect {

    private final MeterRegistry meterRegistry;

    @Autowired
    public OrderPerformanceLoggingAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* com.varc.brewnetapp.domain.order.query.service..*(..))")
    public Object logExecutionTimeOnOrderQueryServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        long startTime = System.currentTimeMillis();  // 시작 시간 기록
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;  // 경과 시간 계산
            sample.stop(meterRegistry.timer("orderQueryMethod.execution.time",
                    "method", joinPoint.getSignature().toShortString()));
            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(), duration);
        }
    }

    @Around("execution(* com.varc.brewnetapp.domain.order.command.application.service..*(..))")
    public Object logExecutionTimeOnOrderCommandServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        long startTime = System.currentTimeMillis();  // 시작 시간 기록
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;  // 경과 시간 계산
            sample.stop(meterRegistry.timer("orderCommandMethod.execution.time",
                    "method", joinPoint.getSignature().toShortString()));
            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(), duration);
        }
    }
}
