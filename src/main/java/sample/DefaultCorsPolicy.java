package sample;

import java.util.Set;
import java.util.TreeSet;

import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * The default {@link CorsPolicy} implementation.
 */
public class DefaultCorsPolicy implements CorsPolicy {

    private Set<String> allowOrigin;
    private Boolean allowCredentials = false;
    private Set<String> exposeHeaders;
    private int maxAge;
    private Set<String> allowMethods;
    private Set<String> allowHeaders;

    /**
     * Creates a new CorsPolicy.
     */
    public DefaultCorsPolicy(Set<String> allowOrigin,
                             Boolean allowCredentials,
                             Set<String> exposeHeaders,
                             int maxAge,
                             Set<String> allowMethods,
                             Set<String> allowHeaders) {
        this.allowOrigin = allowOrigin;
        this.allowCredentials = allowCredentials;
        this.exposeHeaders = exposeHeaders;
        this.maxAge = maxAge;
        setAllowMethods(allowMethods);
        this.allowHeaders = allowHeaders;
    }

    public Set<String> getAllowOrigin(){
        return this.allowOrigin;
    }

    public void setAllowOrigin(Set<String> allowOrigin){
        this.allowOrigin = allowOrigin;
    }

    public Boolean getAllowCredentials(){
        return this.allowCredentials;
    }

    public void setAllowCredentials(Boolean allowCredentials){
        this.allowCredentials = allowCredentials;
    }

    public Set<String> getExposeHeaders(){
        return this.exposeHeaders;
    }

    public void setExposeHeaders(String... exposeHeaders){
        Set<String> newHaders = new TreeSet<String>();
        for (String h: exposeHeaders) {
            newHaders.add(h);
        }
        this.exposeHeaders = newHaders;
    }

    public void setExposeHeaders(Set<String> exposeHeaders){
        this.exposeHeaders = exposeHeaders;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Set<String> getAllowMethods(){
        return this.allowMethods;
    }

    public void setAllowMethods(String... allowMethods){
        Set<String> newMethods = new TreeSet<String>();
        for (String method: allowMethods) {
            validateMethodName(method);
            newMethods.add(method);
        }
        this.allowMethods = newMethods;
    }

    public void setAllowMethods(Set<String> allowMethods){
        for (String method : allowMethods) {
            validateMethodName(method);
        }
        this.allowMethods = allowMethods;
    }

    public Set<String> getAllowHeaders(){
        return this.allowHeaders;
    }

    public void setAllowHeaders(String... allowHeaders){
        Set<String> newHaders = new TreeSet<String>();
        for (String h: allowHeaders) {
            newHaders.add(h);
        }
        this.allowHeaders = newHaders;
    }

    public void setAllowHeaders(Set<String> allowHeaders){
        this.allowHeaders = allowHeaders;
    }

    private void validateMethodName(String method){
        if (!HttpMethod.OPTIONS.getName().equals(method) &&
            !HttpMethod.GET.getName().equals(method) &&
            !HttpMethod.HEAD.getName().equals(method) &&
            !HttpMethod.POST.getName().equals(method) &&
            !HttpMethod.PUT.getName().equals(method) &&
            !HttpMethod.PATCH.getName().equals(method) &&
            !HttpMethod.DELETE.getName().equals(method) &&
            !HttpMethod.TRACE.getName().equals(method) &&
            !HttpMethod.CONNECT.getName().equals(method)
            ) {
            throw new IllegalArgumentException("invalid http method : " + method);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (getAllowOrigin() != null) {
            buf.append("allowOrigin=");
            buf.append(getAllowOrigin().toString());
        }
        if (getAllowCredentials() != null) {
            buf.append(", AllowCredentials=");
            buf.append(getAllowCredentials());
        }
        if (getExposeHeaders() != null) {
            buf.append(", exposeHeaders=");
            buf.append(getExposeHeaders().toString());
        }
        if (getMaxAge() >= 0) {
            buf.append(", maxAge=");
            buf.append(getMaxAge());
            buf.append('s');
        }
        if (getAllowMethods() != null) {
            buf.append(", allowMethods=");
            buf.append(getAllowMethods().toString());
        }
        if (getAllowHeaders() != null) {
            buf.append(", allowHeaders=");
            buf.append(getAllowHeaders().toString());
        }
        return buf.toString();
    }
}

