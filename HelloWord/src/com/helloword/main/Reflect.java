package com.helloword.main;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 反射机制的应用场景：
 * 
 * 	逆向代码 ，例如反编译
 * 	与注解相结合的框架 例如Retrofit
 * 	单纯的反射机制应用框架 例如EventBus 2.x
 * 	动态生成类框架 例如Gson
 * 
 *  forName(String className) 						 根据class的名字生成一个Class对象，构造Class对象。
 *  getAnnotation(Class<A> annotationClass) 	 	 如果存在该元素的指定类型的注释，则返回这些注释，否则返回 null。 
 *  getAnnotations() 								 返回此元素上存在的所有注释。 这些元素可以说是变量，方法，或者类等
 *  getPackage() 									 获取此类的包。
 *  getName()   									返回此 Class 对象所表示的实体（类、接口、数组类、基本类型或 void）名称。
 *	getMethods() 									返回一个包含某些 Method 对象的数组
 *  getMethod(String name, Class<?>... parameterTypes)  	返回指定的 Method 对象
 * 	getField(String name) 							返回指定 Field （变量）对象。
 *	getFields() 									返回 Field （变量）对象的数组	
 * 	getDeclaredMethods()							获取所有的方法
 * 	getReturnType()									获得方法的放回类型
 * 	getParameterTypes()								获得方法的传入参数类型
 * 	getDeclaredMethod("方法名",参数类型.class,……)	获得特定的方法
 * 	getDeclaredConstructors()						获取所有的构造方法
 * 	getDeclaredConstructor(参数类型.class,……)		获取特定的构造方法
 * 	getSuperclass()									获取某类的父类
 * 	getInterfaces()									获取某类实现的接口
 * 	
 * @author MrRight
 */
public class Reflect {

	/**
	 * 反射机制获取类有三种方法，我们来获取Employee类型
	 * @throws ClassNotFoundException  抛异常
	 */
	public void getObjectClass() throws ClassNotFoundException {
		//第一种方式：  
		Class c1 = Class.forName("GsonTest");  
		//第二种方式：  
		//java中每个类型都有class 属性.  
		Class c2 = GsonTest.class;  
		   
		//第三种方式：  
		//java语言中任何一个java对象都有getClass 方法  
		GsonTest g = new GsonTest();  
		Class c3 = g.getClass(); //c3是运行时类 (e的运行时类是Employee) 
	}
	
	/**
	 * 创建对象：获取类以后我们来创建它的对象，利用newInstance：
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public void CreatnewInstanceByClass() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class c =Class.forName("GsonTest");  
		  
		//创建此Class 对象所表示的类的一个新实例  
		GsonTest o = (GsonTest) c.newInstance(); //调用了Employee的无参数构造方法.  
	}
	
	
	
	public static void getClassFields() throws ClassNotFoundException {
		//获取整个类  
        Class c = Class.forName("com.helloword.main.Crawler");  
        //获取所有的属性?  
        Field[] fs = c.getDeclaredFields();  
   
        //定义可变长的字符串，用来存储属性  
        StringBuffer sb = new StringBuffer();  
        //通过追加的方法，将每个属性拼接到此字符串中  
        //最外边的public定义  
        sb.append(Modifier.toString(c.getModifiers()) + " class " + c.getSimpleName() +"{\n");  
        //里边的每一个属性  
        for(Field field:fs){  
            sb.append("\t");//空格  
            sb.append(Modifier.toString(field.getModifiers())+" ");//获得属性的修饰符，例如public，static等等  
            sb.append(field.getType().getSimpleName() + " ");//属性的类型的名字  
            sb.append(field.getName()+";\n");//属性的名字+回车  
        }  
        sb.append("}");  
        System.out.println(sb);  
	}
	
	/**
	 * 获取特定的属性
	 * @throws Exception 
	 */
	public static void getAppointField() throws Exception {
		 //获取类  
	    Class c = Class.forName("com.helloword.main.Crawler");  
	    //获取id属性  
	    Field idF = c.getDeclaredField("aa");  
	    //实例化这个类赋给o  
	    Object o = c.newInstance();  
	    //打破封装  
	    idF.setAccessible(true); //使用反射机制可以打破封装性，导致了java对象的属性不安全。  
	    //给o对象的id属性赋值"110"  
	    idF.set(o, 2); //set  
	    //get  
	    System.out.println(idF.get(o)); 
	}
	
	
	
	public static void main(String[] args) {
		try {
			getAppointField();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
