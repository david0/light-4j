package com.networknt.router.middleware;

import com.networknt.config.Config;
import com.networknt.handler.Handler;
import com.networknt.httpstring.HttpStringConstants;
import com.networknt.router.SidecarConfig;
import com.networknt.url.HttpURL;
import com.networknt.utility.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

public class SidecarTokenHandler extends TokenHandler{

    public static SidecarConfig sidecarConfig = (SidecarConfig)Config.getInstance().getJsonObjectConfig(SidecarConfig.CONFIG_NAME, SidecarConfig.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (Constants.HEADER.equalsIgnoreCase(sidecarConfig.getEgressIngressIndicator())) {
            HeaderValues serviceIdHeader = exchange.getRequestHeaders().get(HttpStringConstants.SERVICE_ID);
            String serviceId = serviceIdHeader != null ? serviceIdHeader.peekFirst() : null;
            String serviceUrl = exchange.getRequestHeaders().getFirst(HttpStringConstants.SERVICE_URL);
            if (serviceId != null || serviceUrl!=null) {
                super.handleRequest(exchange);
            } else {
                Handler.next(exchange, next);
            }
        } else if (Constants.PROTOCOL.equalsIgnoreCase(sidecarConfig.getEgressIngressIndicator()) && HttpURL.PROTOCOL_HTTP.equalsIgnoreCase(exchange.getRequestScheme())){
            super.handleRequest(exchange);
        } else {
            Handler.next(exchange, next);
        }
    }

}