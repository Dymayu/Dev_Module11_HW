package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {

        engine = new TemplateEngine();

        JakartaServletWebApplication jswa = JakartaServletWebApplication.buildApplication(this.getServletContext());

        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(jswa);

        resolver.setPrefix("/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String timezoneReq = req.getParameter("timezone");

        if (timezoneReq == null) { // parameter empty
            if (readCookies(req).isEmpty()) { // NO cookie
                // TODO: Step 1
                String formattedDateTime = timezoneParameterFormatter(timezoneReq);
                processDataToTemplate(req, formattedDateTime, out);
            } else {
                // TODO: Step 3
                Map<String, String> readCookies = readCookies(req);
                String formattedDateTime = timezoneParameterFormatter(readCookies.get("lastTimezone"));
                processDataToTemplate(req, formattedDateTime, out);
            }
        } else {                   // parameter NOT empty - timezone = <timezone>
            if (readCookies(req).isEmpty()){ // NO cookie
                // TODO: Step 2
                cookieHandler(req, resp, timezoneReq, out);
            }else {                            // Yes cookie
                // TODO: Step 4
                cookieHandler(req, resp, timezoneReq, out);
            }
        }

    }

    private void cookieHandler(HttpServletRequest req, HttpServletResponse resp, String timezoneReq, PrintWriter out) {
        if (timezoneReq.contains("UTC ")) {
            String timezoneReqPlus = timezoneReq.replace("UTC ", "UTC+");
            resp.addCookie(new Cookie("lastTimezone", timezoneReqPlus));
        } else {
            resp.addCookie(new Cookie("lastTimezone", timezoneReq));
        }
        String formattedDateTime = timezoneParameterFormatter(timezoneReq);
        processDataToTemplate(req, formattedDateTime, out);
    }

    private void processDataToTemplate(HttpServletRequest req, String formattedDateTime, PrintWriter out) {
        Context data = new Context(req.getLocale(), Map.of("currentTime", formattedDateTime));
        engine.process("timeTemplate", data, out);
        out.close();
    }

    public String timezoneParameterFormatter(String timezoneReq) {
        TimeUtils timeUtils = new TimeUtils();

        if (timezoneReq == null) {
            String formattedDateTime = timeUtils.timeZoneHandler("UTC");
            return formattedDateTime;
        } else {
            if (timezoneReq.contains("UTC ")) {
                String timezoneReqPlus = timezoneReq.replace("UTC ", "UTC+");
                String formattedDateTime = timeUtils.timeZoneHandler(timezoneReqPlus);
                return formattedDateTime;
            } else {
                String formattedDateTime = timeUtils.timeZoneHandler(timezoneReq);
                return formattedDateTime;
            }
        }
    }

    private Map<String, String> readCookies(HttpServletRequest req) {
        String cookies = req.getHeader("Cookie");

        if (cookies == null) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();

        String[] separateCookies = cookies.split(";");
        for (String pair : separateCookies) {
            String[] keyValue = pair.split("=");

            result.put(keyValue[0], keyValue[1]);
        }

        return result;
    }


}
