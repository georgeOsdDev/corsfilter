package sample;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

/**
 *
 */
public class CorsHandler extends SimpleChannelHandler {

    private Map<String, CorsPolicy> corsPolicies;

    /** A queue that is used for correlating a request and a response. */
    final Queue<Map<String, Object>> queue = new ConcurrentLinkedQueue<Map<String, Object>>();

    final private static String ALLOWORIGINALL = "*";
    final private static String COMMASPACE = ", ";

    /**
     * Creates a new instance.
     */
    public CorsHandler(Map<String, CorsPolicy> corsPolicies) {
        this.corsPolicies = corsPolicies;
    }

    public Map<String, CorsPolicy> getCorsPolicy(){
        return this.corsPolicies;
    }

    // Map keys are regex of uri
    public void setCorsPolicy(Map<String, CorsPolicy> corsPolicies) {
        this.corsPolicies = corsPolicies;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        if (this.corsPolicies == null) {
            ctx.sendUpstream(e);
            return;
        }
        Object msg = e.getMessage();
        if (!(msg instanceof HttpRequest)) {
            ctx.sendUpstream(e);
            return;
        }

        CorsPolicy policy = null;
        HttpRequest request = (HttpRequest) e.getMessage();
        HttpHeaders headers = request.headers();

        // get policy that corresponding to the uri
        String uri = request.getUri();
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String path = decoder.getPath();
        Iterator<String> iterator = this.corsPolicies.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(path);
            if (m.matches()) {
                policy = (CorsPolicy) this.corsPolicies.get(key);
                break;
            }
        }

        if (policy == null) {
            ctx.sendUpstream(e);
            return;
        }
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("policy", policy);
        m.put("headers", headers);
        queue.offer(m);
        ctx.sendUpstream(e);
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        if (this.corsPolicies == null) {
            ctx.sendDownstream(e);
            return;
        }
        Object msg = e.getMessage();
        if (!(msg instanceof HttpResponse)) {
            ctx.sendDownstream(e);
            return;
        }

        HttpResponse response = (HttpResponse) msg;
        Map<String, Object> m = queue.poll();
        if (m == null){
            ctx.sendDownstream(e);
            return;
        }
        HttpHeaders requestHeaders = (HttpHeaders) m.get("headers");
        String requestOrigin = requestHeaders.get(HttpHeaders.Names.ORIGIN);
        CorsPolicy corsPolicy = (CorsPolicy) m.get("policy");

        setAllowOrigin(response, requestOrigin, corsPolicy);
        setAllowCredentials(response, corsPolicy);
        setExposeHeaders(response, corsPolicy);
        setMaxAge(response, corsPolicy);
        setAllowMethods(response, corsPolicy);
        setAllowHeaders(response, corsPolicy);
        queue.clear();
        ctx.sendDownstream(e);
    }

    private void setAllowOrigin(HttpResponse response, String requestOrigin, CorsPolicy corsPolicy) {
        Set<String> allowOrigin = corsPolicy.getAllowOrigin();
        if (allowOrigin != null
                && allowOrigin.size() != 0
                && !response.headers().contains(
                        HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN)) {
            if (allowOrigin.size() == 1 && allowOrigin.contains(ALLOWORIGINALL)) {
                if (requestOrigin == null || requestOrigin == "null") {
                    HttpHeaders.setHeader(response,
                            HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN,
                            ALLOWORIGINALL);
                } else {
                    HttpHeaders.setHeader(response,
                            HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN,
                            requestOrigin);
                }
            } else {
                if (allowOrigin.contains(requestOrigin)) {
                    HttpHeaders.setHeader(response,
                            HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN,
                            requestOrigin);
                } else {
                    HttpHeaders.setHeader(response,
                            HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN,
                            implode(COMMASPACE, allowOrigin));
                }
            }
        }
    }

    private void setAllowCredentials(HttpResponse response, CorsPolicy corsPolicy) {
        Boolean allowCredentials = corsPolicy.getAllowCredentials();
        if (allowCredentials
                && !response.headers().contains(
                        HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS)
                && !ALLOWORIGINALL.equals(HttpHeaders.getHeader(response,
                        HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN))) {
            HttpHeaders.setHeader(response,
                    HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                    "true");
        }
    }

    private void setExposeHeaders(HttpResponse response, CorsPolicy corsPolicy) {
        Set<String> exposeHeaders = corsPolicy.getExposeHeaders();
        if (exposeHeaders != null
                && !response.headers().contains(
                        HttpHeaders.Names.ACCESS_CONTROL_EXPOSE_HEADERS)) {
            HttpHeaders.setHeader(response,
                    HttpHeaders.Names.ACCESS_CONTROL_EXPOSE_HEADERS,
                    implode(COMMASPACE, exposeHeaders));
        }
    }

    private void setMaxAge(HttpResponse response, CorsPolicy corsPolicy) {
        int maxAge = corsPolicy.getMaxAge();
        if (!response.headers().contains(
                HttpHeaders.Names.ACCESS_CONTROL_MAX_AGE)) {
            HttpHeaders.setHeader(response,
                    HttpHeaders.Names.ACCESS_CONTROL_MAX_AGE, maxAge);
        }
    }

    private void setAllowMethods(HttpResponse response, CorsPolicy corsPolicy) {
        Set<String> allowMethods = corsPolicy.getAllowMethods();
        if (allowMethods != null
                && !response.headers().contains(
                        HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS)) {
            HttpHeaders.setHeader(response,
                    HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS,
                    implode(COMMASPACE, allowMethods));
        }
    }

    private void setAllowHeaders(HttpResponse response, CorsPolicy corsPolicy) {
        Set<String> allowHeaders = corsPolicy.getAllowHeaders();
        if (allowHeaders != null
                && !response.headers().contains(
                        HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS)) {
            HttpHeaders.setHeader(response,
                    HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS,
                    implode(COMMASPACE, allowHeaders));
        }
    }

    private String implode(String sepalator, Set<String> data) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = data.iterator();
        if (iter.hasNext()) {
              sb.append(iter.next());
              while (iter.hasNext()) {
                sb.append(sepalator).append(iter.next());
              }
        }
        return sb.toString();
    }
}
