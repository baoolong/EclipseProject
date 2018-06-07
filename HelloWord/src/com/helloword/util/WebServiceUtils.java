package com.helloword.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.helloword.confige.Constants;

/**
 * WedService接口访问工具�?(待完�?,可根据用途再次增加�?�用方法)
 *
 * @author hzq
 */
public class WebServiceUtils {

    private static WebServiceUtils mServiceUtils = null;
    //声明传�?�消息的信使
    private SoapSerializationEnvelope envelope = null;
    //获得WebService通信的桥�?
    HttpTransportSE ht = null;

    public WebServiceUtils() {
        envelope = getEnvelope();
    }

    public static WebServiceUtils getInstance() {
        if (mServiceUtils == null)
            mServiceUtils = new WebServiceUtils();
        return mServiceUtils;
    }


    //防止外面通过反射创建新对�?
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }


    /**
     * 创建�?个SoapSerializationEnvelope对象
     *
     * @return SoapSerializationEnvelope对象
     */
    private SoapSerializationEnvelope getEnvelope() {
        if (envelope == null)
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        return envelope;
    }





    /**
     * 资源的请求Json数据
     *
     * @param parameters �?要传递的参数
     * @param methodName 方法名字
     */
    public String requestResouceWeb(LinkedHashMap<String, Object> parameters, String methodName) {
        String result = null;
        try {
            //得到Soap对象
            SoapObject soap = new SoapObject(Constants.RESOURCE_NAMESPACE, methodName);
            if (parameters != null && !parameters.isEmpty()) {
                Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator();
                while (iterator.hasNext()) {
                    //遍历map并且将参数和参数名添加进SoapObject对象
                    Entry<String, Object> entry = iterator.next();
                    soap.addProperty(entry.getKey(), entry.getValue());
                }
            }
            //初始化传递消息的信封
            SoapSerializationEnvelope envelope = getEnvelope();
            envelope.bodyOut = soap;
            //获得WebService通信的桥�?
            HttpTransportSE ht = new HttpTransportSE(Constants.RESOURCE_URL);
            System.out.println("requestResouceWeb:"+Constants.RESOURCE_URL);
            ht.call(null, envelope);
            result = envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null)
            System.out.println("result:"+result);
        if (result != null && result.equals("anyType{}"))
            result = null;
        return result;
    }
    
    
    /**
     * 突发事件请求Json数据
     *
     * @param parameters �?要传递的参数
     * @param methodName 方法名字
     */
    public String requestEventWeb(LinkedHashMap<String, Object> parameters, String methodName) {
        //得到Soap对象
        SoapObject soap = new SoapObject(Constants.EVENT_NAMESPACE, methodName);
        if (parameters != null && !parameters.isEmpty()) {
            Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator();
            while (iterator.hasNext()) {
                //遍历map并且将参数和参数名添加进SoapObject对象
                Entry<String, Object> entry = iterator.next();
                soap.addProperty(entry.getKey(), entry.getValue());
            }
        }
        //初始化传递消息的信封
        SoapSerializationEnvelope envelope = getEnvelope();
        envelope.bodyOut = soap;
        //获得WebService通信的桥�?
        HttpTransportSE ht = new HttpTransportSE(Constants.EVENT_URL);
        System.out.println(Constants.EVENT_URL);
        String result = null;
        try {
            ht.call(null, envelope);
            result = envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null && (result.equals("anyType{}")||result.equals("true")))
            result = null;
        return result;
    }
}
