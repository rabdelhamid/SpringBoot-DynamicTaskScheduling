package com.dynamic.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;


//Using a Trigger you can calculate the next execution time on the fly.
@Component
public class DynamicScheduling implements SchedulingConfigurer {

	
	@Autowired
    private IRetrieveDataService retrieveDataService;
	
	 private boolean limitReached = false;

	 private int nextExecutionTimeMilliSeconds = 0;
	 
	 
	 @Override
	    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
	        taskRegistrar.addTriggerTask(new Runnable() {
	            @Override
	            public void run() {
	                System.out.print("Dynamic shedule is up with time ="+ nextExecutionTimeMilliSeconds+"\n");	               
	                int fetchedRecords=retrieveDataService.findAllowedRequests();
	                if(fetchedRecords==0)
	                	limitReached = true;
	               
	            }
	        }, new Trigger() {
	            @Override
	            public Date nextExecutionTime(TriggerContext triggerContext) {
	                Calendar nextExecutionTime = new GregorianCalendar();
	                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
	                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
	                nextExecutionTime.add(Calendar.MILLISECOND, getNewExecutionTime());
	                return nextExecutionTime.getTime();
	            }
	        });
	    }
	 //you can retrieve Your execution time from database or property file
	    private int getNewExecutionTime() {
	        if (limitReached)
	            nextExecutionTimeMilliSeconds = 2*1000; //2 sec
	        else
	            nextExecutionTimeMilliSeconds = 1*1000;//1 sec
	       
	        return nextExecutionTimeMilliSeconds;
	    }
}
