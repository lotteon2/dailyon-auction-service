package com.dailyon.auctionservice.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory
    implements ApplicationContextAware {

  @Autowired private AutowireCapableBeanFactory beanFactory;

  @Override
  public void setApplicationContext(final ApplicationContext context) {
    beanFactory = context.getAutowireCapableBeanFactory();
  }

  @Override
  protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
    final Object job =
        beanFactory.autowire(
            Class.forName(bundle.getJobDetail().getJobClass().getName()),
            AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR,
            true);
    return job;
  }
}
