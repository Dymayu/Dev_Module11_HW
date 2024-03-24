package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.*;

@WebFilter(value="/*")
public class TimeZoneFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        TimeUtils timeUtils = new TimeUtils();
        HttpServletResponse httpResponse = (HttpServletResponse) res;

        String timezoneReq = req.getParameter("timezone");
        if(timezoneReq == null){
            chain.doFilter(req, res);
        }else {
            try {
                if(timezoneReq.contains("UTC ")){
                    String timezoneReqPlus = timezoneReq.replace("UTC ", "UTC+");
                    timeUtils.timeZoneHandler(timezoneReqPlus);
                    chain.doFilter(req, res);
                }else {
                    timeUtils.timeZoneHandler(timezoneReq);
                    chain.doFilter(req, res);
                }

            } catch (DateTimeException e) {
                // Handle invalid timezone
                httpResponse.setStatus(400);
                httpResponse.setContentType("text/html");
                httpResponse.getWriter().write("Invalid timezone: " + timezoneReq);
            }
        }

    }

}
