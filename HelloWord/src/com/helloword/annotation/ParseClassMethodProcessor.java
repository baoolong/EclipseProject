package com.helloword.annotation;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8) // 源码级别, 这里的环境是 jdk 1.8
@SupportedAnnotationTypes("com.helloword.annotation.PraseClassMethod") // 处理的注解类型, 这里需要处理的是 com.helloword.annotation 包下的 PraseClassMethod 注解(这里也可以不用注解, 改成重写父类中对应的两个方法)
public class ParseClassMethodProcessor extends AbstractProcessor{
	/*
	 * 2. 运行
	 * 此时项目目录如下, 这里 out 目录为手动创建
	 * 
	 * out
	 * 	  production
	 * 		 apt
	 * src
	 * 	  apt
	 * 	
	 * 在命令行中进入项目根目录, 即 src 文件夹的上一层.
	 * 首先编译注解处理器: javac -encoding UTF-8 -d out\production\ src\com\helloword\annotation\ParseClassMethodProcessor.java src\com\helloword\annotation\PraseClassMethod.java
	 * 接着执行注解处理器: javac -encoding UTF-8 -cp out\production\ -processor com.helloword.annotation.ParseClassMethodProcessor -d out\production -s src\ src\com\helloword\bin\*.java
	 */

	// 计数器, 用于计算 process() 方法运行了几次
    private int count = 1;
    // 用于写文件
    private Filer filer;
    private Types typeUtils;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
    }

    // 处理编译时注解的方法
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("start process, count = " + count++);
        // 获得所有类
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        System.out.print("this class file contain all class:");

        for (Element rootElement : rootElements) {
            System.out.println("  " + rootElement.getSimpleName());
        }

        // 获得有注解的元素, 这里 PraseClassMethod 只能修饰类, 所以只有类
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(PraseClassMethod.class);
        System.out.print("this class file contain all annotated class:");
        for (Element element : elementsAnnotatedWith) {
        	PackageElement pkg = elementUtils.getPackageOf(element);
            
            String className = element.getSimpleName().toString();
            System.out.println("------------"+pkg+"."+className);
            try {
				Class<?> class1 = Class.forName(pkg+"."+className);
				Method [] methods=class1.getMethods();
				for(Method method:methods) {
					Parameter[] parameters=method.getParameters();
					for(Parameter parameter:parameters) {
						System.out.println(method.getName()+"***"+parameter.getName());
					}
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
            System.out.println("  " + className);
            String output = element.getAnnotation(PraseClassMethod.class).name();
            // 产生的动态类的名字
            String newClassName = className + "_New";
            // 写 java 文件
            createFile(newClassName, output);
        }
        return true;
    }

    private void createFile(String className, String output) {
        StringBuilder cls = new StringBuilder();
        cls.append("package com.helloword.annotation;\n\npublic class ")
                .append(className)
                .append(" {\n  public static void main(String[] args) {\n")
                .append("    System.out.println(\"")
                .append(output)
                .append("\");\n  }\n}");
        try {
            JavaFileObject sourceFile = filer.createSourceFile("com.helloword.annotation." + className);
            Writer writer = sourceFile.openWriter();
            writer.write(cls.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
