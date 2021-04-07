package cn.yiidii.pigeon.common.core.filter;

import cn.yiidii.pigeon.common.core.properties.PigeonTraceProperties;
import cn.yiidii.pigeon.common.core.util.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 日志链路追踪过滤器
 *
 * @author: YiiDii Wang
 * @create: 2021-04-06 22:49
 */
@Slf4j
@Configuration
@ConditionalOnClass(Filter.class)
public class TraceFilter extends OncePerRequestFilter {

    @PostConstruct
    public void init() {
        logger.info("===== TraceFilter init...");
    }

    @Autowired
    private PigeonTraceProperties traceProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !traceProperties.getEnabled();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = TraceUtil.getTraceId(request);
            log.error("traceId: {}", traceId);
            TraceUtil.mdcTraceId(traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }
}