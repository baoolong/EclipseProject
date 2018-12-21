package com.helloword.confige;

public class Constants {

//	public static final String WEB_ROOT_RUL= "10.192.3.208:9090";
	public static final String WEB_ROOT_RUL= "191.0.4.187:8800";
	/** 资源的URL */
	public static final String RESOURCE_URL = "http://"+WEB_ROOT_RUL+"/Resource/services/MaterialWebService?wsdl";
	/** 资源的的NAMESPACE */
	public static final String RESOURCE_NAMESPACE = "http://com.lgyw.emg.webserver/scheme/1.0";
	
	/** 突发事件的URL */
	public static final String EVENT_URL = "http://"+WEB_ROOT_RUL+"/ywbase/services/eventwebservices?wsdl";
	/** 突发事件的NAMESPACE */
	public static final String EVENT_NAMESPACE = "http://com.lgyw.futian.webserver/scheme/1.0";
	
	/** 指挥调度的URL 208 9090 */
	public static final String DISPATHC_URL = "http://"+WEB_ROOT_RUL+"/ywbase/services/mywebServices?wsdl";
	/** 指挥调度的NAMESPACE */
	public static final String DISPATHC_NAMESPACE = "http://com.lgyw.futian.webserver/scheme/1.0";
	/**文件审批上传文件的servlet接口*/
    public static final String RELEDOCUMENTURL = "http://" + WEB_ROOT_RUL + "/ywbase/servlet/ClearPaperAction";
    
    /**文件审批的URL 208 9090*/
    public static final String APPROVAL_URL = "http://" + WEB_ROOT_RUL + "/ywbase/services/ClearPaperWebService?wsdl";
    /**文件审批的NAMESPACE*/
    public static final String APPROVAL_NAMESPACE = "http://com.lgyw.futian.webserver/scheme/1.0";
}
