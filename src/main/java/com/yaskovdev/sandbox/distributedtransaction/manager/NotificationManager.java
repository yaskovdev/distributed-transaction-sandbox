package com.yaskovdev.sandbox.distributedtransaction.manager;

import com.yaskovdev.sandbox.distributedtransaction.model.Notification;
import com.yaskovdev.sandbox.distributedtransaction.workflow.CreateNotificationWorkflow;
import io.nflow.engine.service.WorkflowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationManager {

    private final CreateNotificationWorkflow createNotificationWorkflow;

    private final WorkflowInstanceService workflowInstanceService;

    @Autowired
    public NotificationManager(final CreateNotificationWorkflow createNotificationWorkflow,
            final WorkflowInstanceService workflowInstanceService) {
        this.createNotificationWorkflow = createNotificationWorkflow;
        this.workflowInstanceService = workflowInstanceService;
    }

    public void createNotification(final Notification notification) {
        workflowInstanceService.insertWorkflowInstance(createNotificationWorkflow.newInstanceWith(notification));
    }
}
