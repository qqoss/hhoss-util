
/**
http://my.oschina.net/bayuanqian/blog/205315
在项目中充斥着多种日志框架，使用混乱，要求整理统一一种日志框架。
二、现状
1、项目组内的代码大多直接使用的Log4j的API，有的也使用slf4j的API调用。     
2、使用maven的包依赖检查可以看到整个项目依赖包中包含三种日志框架，分别是log4j、logback、common-logging.
三、解决方案
在这种情况下，要想统一日志框架，需解决两个问题：
1、旧有代码兼容：如果逐个修改直到使用统一的日志框架是非常耗时的，也不可能做到（因为有依赖包的存在）
2、统一日志框架选择：如果不考虑性能等因素，任何一个日志框架均可。
综合以上考虑，使用slf4j+logback组合
slf4j框架：可以让我们与具体日志框架分离，且其自带的桥接包模块可以帮助我们从其他日志框架平稳切换。
logback：与slf4j良好兼容，完美的性能、支持、功能等，更重要的是我曾经也在log4j上有过血的教训。
切换步骤如下：
1、引入slf4j包和logback包，
去掉原有的日志框架log4j、common-logging，切换成桥接包
项目包依赖是使用maven管理，所以在maven的pom.xml文件查找这两个日志框架的依赖包将其去掉。
 如果是间接依赖，在其依赖引入配置中添加如下代码将其去掉：
<exclusions><exclusion><groupId>log4j</groupId><artifactId>log4j</artifactId> </exclusion></exclusions>
<exclusions><exclusion><groupId>commons-logging</groupId><artifactId>commons-logging</artifactId></exclusion></exclusions>
添加slf4j中关于这两个日志框架的桥接包 
<artifactId>jul-to-slf4j</artifactId>
artifactId>log4j-over-slf4j</artifactId>
  删除log4j.properties文件，添加logback.xml配置文件并配置
  
四、参考文档
slf4j官网：http://www.slf4j.org/
slf4j用户手册：http://www.slf4j.org/manual.html
slf4j桥接包：http://www.slf4j.org/legacy.html
logback官网：http://logback.qos.ch/
logback介绍及使用方法：http://blog.csdn.net/zgmzyr/article/details/8267072
logback相比较于log4j的优势：http://www.oschina.net/translate/reasons-to-prefer-logbak-over-log4j
 
每天的日志按照一天四份的分割，即每天的6点、12点、18点、24点分割日志
 
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>${filePrefix}/demo-error.%d{yyyyMMddHHmm}.log</fileNamePattern>
    <timeBasedFileNamingAndTriggeringPolicy class="com.demo.utils.MlsPaySizeAndTimeBasedFNATP">
         <multiple>10</multiple>
    </timeBasedFileNamingAndTriggeringPolicy>
</rollingPolicy>

 */
package com.hhoss.jour;

import java.util.Calendar;
import java.util.Date;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;

@NoAutoStart
public class SizeAndTimesBasedFNATP<E> extends SizeAndTimeBasedFNATP<E> {
	private Integer multiple = 1;

	protected void computeNextCheck() {
		super.computeNextCheck();
		nextCheck = rc.getEndOfNextNthPeriod(dateInCurrentPeriod, multiple).getTime();
	}
	
	public Integer getMultiple() {
		return multiple;
	}

	public void setMultiple(Integer multiple) {
		if (multiple > 1) {
			this.multiple = multiple;
		}
	}

	@Override
	public void start() {
		super.start();
		initCurrentPeriod(multiple);
		initNextCheck(multiple);
	}

	private void initCurrentPeriod(Integer multiple) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		switch (rc.getPeriodicityType()) {
		case TOP_OF_MINUTE:
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.MINUTE, (calendar.get(Calendar.HOUR_OF_DAY) / multiple) * multiple);
			break;
		case TOP_OF_HOUR:
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.HOUR_OF_DAY, (calendar.get(Calendar.HOUR_OF_DAY) / multiple) * multiple);
			break;

		case TOP_OF_DAY:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.DATE, (calendar.get(Calendar.HOUR_OF_DAY) / multiple) * multiple);
			break;
		default:
			throw new IllegalStateException("不支持倍数增加的单位");
		}

		dateInCurrentPeriod = calendar.getTime();
	}

	private void initNextCheck(Integer multiple) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		switch (rc.getPeriodicityType()) {
		case TOP_OF_MINUTE:
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.MINUTE, (calendar.get(Calendar.HOUR_OF_DAY) / multiple + 1) * multiple);
			break;
		case TOP_OF_HOUR:
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.HOUR_OF_DAY, (calendar.get(Calendar.HOUR_OF_DAY) / multiple + 1) * multiple);
			break;

		case TOP_OF_DAY:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.DATE, (calendar.get(Calendar.HOUR_OF_DAY) / multiple + 1) * multiple);
			break;
		default:
			throw new IllegalStateException("不支持倍数增加的单位");
		}

		nextCheck = calendar.getTime().getTime();
	}
}