package com.admin.aspect;

import com.admin.annotation.Log;
import com.admin.common.util.SecurityUtils;
import com.admin.entity.OperLog;
import com.admin.service.OperLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final OperLogService operLogService;
    private final ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(logAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log logAnnotation, Object jsonResult) {
        handleLog(joinPoint, logAnnotation, null, jsonResult);
    }

    @AfterThrowing(pointcut = "@annotation(logAnnotation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log logAnnotation, Exception e) {
        handleLog(joinPoint, logAnnotation, e, null);
    }

    private void handleLog(JoinPoint joinPoint, Log logAnnotation, Exception e, Object jsonResult) {
        try {
            OperLog operLog = new OperLog();
            operLog.setStatus(1);
            operLog.setTitle(logAnnotation.title());
            operLog.setBusinessType(logAnnotation.businessType());

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setRequestMethod(request.getMethod());
                operLog.setOperUrl(request.getRequestURI());
                operLog.setOperIp(request.getRemoteAddr());
            }

            operLog.setOperName(SecurityUtils.getUsername());

            if (e != null) {
                operLog.setStatus(0);
                operLog.setErrorMsg(e.getMessage().substring(0, Math.min(e.getMessage().length(), 2000)));
            }

            if (logAnnotation.isSaveRequestData()) {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    StringBuilder params = new StringBuilder();
                    for (Object arg : args) {
                        if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                            continue;
                        }
                        params.append(objectMapper.writeValueAsString(arg)).append(" ");
                    }
                    operLog.setOperParam(params.length() > 2000 ? params.substring(0, 2000) : params.toString());
                }
            }

            if (logAnnotation.isSaveResponseData() && jsonResult != null) {
                String result = objectMapper.writeValueAsString(jsonResult);
                operLog.setJsonResult(result.length() > 2000 ? result.substring(0, 2000) : result);
            }

            operLog.setOperTime(LocalDateTime.now());
            operLogService.createOperLog(operLog);
        } catch (Exception ex) {
            log.error("记录操作日志异常: {}", ex.getMessage());
        }
    }
}
