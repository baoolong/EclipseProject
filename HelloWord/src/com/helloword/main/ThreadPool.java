package com.helloword.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService：	真正的线程池接口。
 * ScheduledExecutorService	能和Timer/TimerTask类似，解决那些需要任务重复执行的问题。
 * ThreadPoolExecutor	ExecutorService的默认实现。
 * ScheduledThreadPoolExecutor	继承ThreadPoolExecutor的ScheduledExecutorService接口实现，周期性任务调度的类实现。
 * @author MrRight
 */
public class ThreadPool {
	private static int count=0;
	private static ScheduledFuture<?> state;

	/**
	 * Executors 简介
	 * Java通过Executors提供四种线程池，它的子类ExecutorService，ScheduledExecutorService可以直接使用这些池，分别为：
	 * 
	 * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程，可复用已有的闲置的线程。
	 * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
	 * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行，ScheduledExecutorService比Timer更安全
	 * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
	 * 
	 */
	public static void main(String[] args) {
		executorServiceTest();
	}
	
	/**
	 * ExecutorService和ScheduledExecutorService
	 * 
	 * 调用以下三个静态方法都是返回一个 ExecutorService
	 * Executors.newCachedThreadPool(); 
	 * Executors.newFixedThreadPool(10);
	 * Executors.newSingleThreadExecutor()
	 * 
	 * ScheduledExecutorService 继承 ExecutorService
	 * 只有调用Executors.newScheduledThreadPool(1);方法才会返回ScheduledExecutorService
	 * ScheduledExecutorService（即：newScheduledThreadPool(int)）用于执行定时任务和循环任务
	 * scheduleAtFixedRate:按照上一次任务的发起时间计算下一次任务的开始时间
	 * scheduleWithFixedDelay: 以上一次任务的结束时间计算下一次任务的开始时间
	 * scheduledExecutorService.shutdown() 关闭线程池,不再接收提交的任务，scheduledExecutorService不可再使用； 
	 * ScheduledFuture<?>.cancel(true)只是关闭某一个线程任务，线程池不会受影响，线程池不会关闭，会等待Runnable来执行
	 */
	private static void executorServiceTest() {
		ExecutorService executorService;
		executorService=Executors.newCachedThreadPool();
		executorService=Executors.newFixedThreadPool(10);
		executorService=Executors.newSingleThreadExecutor();
		
		ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(1);
		if(state==null||state.isDone()) {
			state=scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					Thread th=Thread.currentThread();
					System.out.println("hello world******"+th.getName()+"***"+th.getId());
					count++;
					if(count>=3) {
						state.cancel(true);
						count=0;
						System.out.println("*********OVER*********");
					}
					executorServiceTest();
				}
			}, 0, 2, TimeUnit.SECONDS);
		}
	}
	
	
	
	/**
	 * 4种构造方法
	 * ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue) 
	 *
	 * ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory)
	 *
	 * ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue,RejectedExecutionHandler handler)
	 *
	 * ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory,RejectedExecutionHandler handler)
	 * 
	 * 构造方法的参数：
	 * corePoolSize：      核心线程数，默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。
	 * maximumPoolSize：线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。
	 * keepAliveTime：	非核心线程的闲置超时时间，超过这个时间就会被回收。
	 * unit：			指定keepAliveTime的单位，如TimeUnit.SECONDS。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。
	 * workQueue：		线程池中的任务队列.    常用的有三种队列，SynchronousQueue,LinkedBlockingDeque,ArrayBlockingQueue
	 * threadFactory：	线程工厂，提供创建新线程的功能。ThreadFactory是一个接口，只有一个方法，通过线程工厂可以对线程的一些属性进行定制。
	 * RejectedExecutionHandler:	RejectedExecutionHandler也是一个接口，只有一个方法,当线程池中的资源已经全部使用，添加新线程被拒绝时，会调用RejectedExecutionHandler的rejectedExecution方法。
	 *
	 * 线程池规则:
	 * 线程池的线程执行规则跟任务队列有很大的关系。
	 * 	下面都假设任务队列没有大小限制：
	 * 		1.如果线程数量<=核心线程数量，那么直接启动一个核心线程来执行任务，不会放入队列中
	 * 		2.如果线程数量>核心线程数，但<=最大线程数，并且任务队列是LinkedBlockingDeque的时候，超过核心线程数量的任务会放在任务队列中排队。
	 * 		3.如果线程数量>核心线程数，但<=最大线程数，并且任务队列是SynchronousQueue的时候，线程池会创建新线程执行任务，这些任务也不会被放在任务队列中。这些线程属于非核心线程，在任务完成后，闲置时间达到了超时时间就会被清除
	 * 		4.如果线程数量>核心线程数，并且>最大线程数，当任务队列是LinkedBlockingDeque，会将超过核心线程的任务放在任务队列中排队。也就是当任务队列是LinkedBlockingDeque并且没有大小限制时，线程池的最大线程数设置是无效的，他的线程数最多不会超过核心线程数。
	 * 		5.如果线程数量>核心线程数，并且>最大线程数，当任务队列是SynchronousQueue的时候，会因为线程池拒绝添加任务而抛出异常。
	 * 		6.ArrayBlockingQueue 阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程
	 * 	任务队列大小有限时
	 * 		1.当LinkedBlockingDeque塞满时，新增的任务会直接创建新线程来执行，当创建的线程数量超过最大线程数量时会抛异常。
	 * 		2.SynchronousQueue没有数量限制。因为他根本不保持这些任务，而是直接交给线程池去执行。当任务数量超过最大线程数时会直接抛异常。
	 *
	 */
	private void threadPoolExecutorTest() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 10, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		executor.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
			}
		});
	}
}
