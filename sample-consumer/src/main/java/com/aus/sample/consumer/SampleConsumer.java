package com.aus.sample.consumer;

import cn.hutool.core.util.RandomUtil;
import com.aus.sample.common.model.Task;
import com.aus.sample.common.service.TaskService;
import com.aus.sample.consumer.proxy.ServiceProxyFactory;

public class SampleConsumer {

    public static void main(String[] args) {

        TaskService taskService = ServiceProxyFactory.getProxy(TaskService.class);
        Task task = new Task();
        task.setTaskId(RandomUtil.randomLong());
        task.setName(RandomUtil.randomString(4));
        task.setNote(RandomUtil.randomString(4));
        task.setCronExp(RandomUtil.randomString(4));
        task.setCommand(RandomUtil.randomString(4));

        Task newTask = taskService.getTask(task);
        if (newTask != null){
            System.out.println(newTask.getName());
        }else{
            System.out.println("Something went wrong");
        }
    }

}