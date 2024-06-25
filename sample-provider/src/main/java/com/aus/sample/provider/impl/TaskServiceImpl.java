package com.aus.sample.provider.impl;

import cn.hutool.core.util.RandomUtil;
import com.aus.sample.common.model.Task;
import com.aus.sample.common.service.TaskService;

public class TaskServiceImpl implements TaskService {

    @Override
    public Task getTask(Task task) {
        task.setName("handled_task_" + RandomUtil.randomNumbers(4));
        return task;
    }

}
