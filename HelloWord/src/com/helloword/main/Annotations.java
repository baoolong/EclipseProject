package com.helloword.main;

import com.helloword.interfaces.IAnnotation;

/**
 * 注解测试
 * @author MrRight
 *
 */
public class Annotations {

	@IAnnotation(author="abc", comments = "666", date = "888" ,revision=000)
	private String author;
}
