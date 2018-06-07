package com.helloword.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解方法不能带有参数；
 * 注解方法返回值类型限定为：基本类型、String、Enums、Annotation或者是这些类型的数组；
 * 注解方法可以有默认值；
 * 注解本身能够包含元注解，元注解被用来注解其它注解。
 * @author MrRight
 */
@Target(ElementType.FIELD)	//@Target——指明该类型的注解可以注解的程序元素的范围,该元注解的取值可以为TYPE,METHOD,CONSTRUCTOR,FIELD等
@Documented  				//@Documented —— 指明拥有这个注解的元素可以被javadoc此类的工具文档化
@Inherited					// @Inherited——指明该注解类型被自动继承。
@Retention(RetentionPolicy.RUNTIME)		//@Retention——指明了该Annotation被保留的时间长短。RetentionPolicy取值为SOURCE,CLASS,RUNTIME。
public @interface IAnnotation {
	String author() default "Pankaj";
    String date();
    int revision() default 1;
    String comments();
}
