package com.pc.wx.base;

import com.pc.wx.po.BaseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by lenovo on 2014/12/6.
 */
@ControllerAdvice
public class BaseAction implements ServletContextAware {
    private Logger logger = LogManager.getLogger(BaseAction.class);
    protected ServletContext servletContext;
    protected String webPath;

    @ExceptionHandler
    @ResponseBody
    public BaseResult exp(HttpServletRequest request, HttpServletResponse response, Exception ex) {
//        BaseReturn baseReturn=new BaseReturn();
        ex.printStackTrace();
        if (ex instanceof HttpMessageNotReadableException){
        logger.error(ex.getMessage());
/*            baseReturn.setReturnCode(BaseReturn.Err_data_inValid);
            baseReturn.setMessageInfo(getMessage(request,"data.inValid",null));*/
            return new BaseResult(1,ex.getMessage());
        }else if(ex instanceof ServletRequestBindingException || ex instanceof IllegalArgumentException){
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=utf-8");
                PrintWriter pw = response.getWriter();
                pw.write("<script>alert('参数有误,请重试');window.history.back();</script>");
                pw.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }else
            return new BaseResult(301,"后台错误!");
/*            baseReturn.setReturnCode(BaseReturn.Err_system_error);
            baseReturn.setMessageInfo(ex.getMessage());*/
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
        this.webPath = servletContext.getRealPath("/");
    }

    public String getCookie(String key, HttpServletRequest request) {
        Cookie mycookies[] = request.getCookies();
        if (mycookies != null) {
            for (int i = 0; i < mycookies.length; i++) {
                if (key.equalsIgnoreCase(mycookies[i].getName())) {
                    try {
                        return URLDecoder.decode(mycookies[i].getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }

    public byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    @ModelAttribute("tenantId")
    public String setConstans(HttpServletRequest request,ModelMap model){
        String ddBB=request.getHeader("ddBB");
        model.addAttribute("ddBB", StringUtils.isEmpty(ddBB) ? "dems": ddBB);
        System.out.println("请求路径===>"+request.getRequestURI()+"\r\n请求参数====>");
        request.getParameterMap().forEach((k,v)-> System.out.println(k+"=====>"+v[0]));
        return request.getHeader("TENANT_ID");
    }
}
