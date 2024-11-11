package bot.telegram.configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class RequestFilter implements Filter {

    @Value("${marketplace.website.token}")
    private String token;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!httpServletRequest.getRequestURI().startsWith("/api/order")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = httpServletRequest.getHeader("authorization");

        if (Objects.isNull(authorization) || !authorization.equals(token)) {
            log.info("Website call error: {}", Objects.isNull(authorization) ? "No authorization header" : "Wrong header token: " + "'" + authorization +"'");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.info("Website call success");
        filterChain.doFilter(request, response);

    }
}
