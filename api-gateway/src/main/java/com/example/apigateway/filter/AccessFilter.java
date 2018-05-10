package com.example.apigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class AccessFilter extends ZuulFilter{

    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);
    @Override
    public String filterType() {
        //pre, routing(在路由请求时候被调用), post(在routing和error过滤器之后被调用), error(处理请求时发生错误时被调用)
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        //启动该服务网关后，访问http://localhost:5555/api-a/add?a=1&b=2：返回401错误
        //http://localhost:5555/api-a/add?a=1&b=2&accessToken=token：正确路由到compute-service，并返回计算内容
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        Object accessToken = request.getParameter("accessTocken");
        if(accessToken == null) {
            log.warn("access tocken is empty");
            //ctx.setSendZuulResponse(false)令zuul过滤该请求，不对其进行路由
            ctx.setSendZuulResponse(false);
            //设置了其返回的错误码
            ctx.setResponseStatusCode(401);
            return null;
        }
        log.info("access tocken ok");
        return null;
    }
}