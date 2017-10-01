package com.yaskovdev.sandbox.distributedtransaction.workflow;

import com.yaskovdev.sandbox.distributedtransaction.client.JdbcClient;
import com.yaskovdev.sandbox.distributedtransaction.client.JmsClient;
import com.yaskovdev.sandbox.distributedtransaction.model.Notification;
import io.nflow.engine.workflow.definition.NextAction;
import io.nflow.engine.workflow.definition.StateExecution;
import io.nflow.engine.workflow.definition.WorkflowDefinition;
import io.nflow.engine.workflow.definition.WorkflowSettings;
import io.nflow.engine.workflow.definition.WorkflowState;
import io.nflow.engine.workflow.definition.WorkflowStateType;
import io.nflow.engine.workflow.instance.WorkflowInstance;
import io.nflow.engine.workflow.instance.WorkflowInstanceFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yaskovdev.sandbox.distributedtransaction.workflow.CreateNotificationWorkflow.State.createEvent;
import static com.yaskovdev.sandbox.distributedtransaction.workflow.CreateNotificationWorkflow.State.error;
import static com.yaskovdev.sandbox.distributedtransaction.workflow.CreateNotificationWorkflow.State.sendNotification;
import static com.yaskovdev.sandbox.distributedtransaction.workflow.CreateNotificationWorkflow.State.success;
import static io.nflow.engine.workflow.definition.NextAction.moveToState;
import static io.nflow.engine.workflow.definition.WorkflowStateType.end;
import static io.nflow.engine.workflow.definition.WorkflowStateType.normal;
import static io.nflow.engine.workflow.definition.WorkflowStateType.start;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class CreateNotificationWorkflow extends WorkflowDefinition<CreateNotificationWorkflow.State> {

    private static final Logger logger = getLogger(CreateNotificationWorkflow.class);

    private static final String TYPE = "createNotificationWorkflow";
    private static final String VAR_NOTIFICATION = "VAR_NOTIFICATION";

    private final WorkflowInstanceFactory workflowInstanceFactory;
    private final JdbcClient jdbcClient;
    private final JmsClient jmsClient;

    enum State implements WorkflowState {

        createEvent(start, "Event is persisted to a database"),
        sendNotification(normal, "Notification is sent to a message queue"),
        success(end, "Notification creation is finished successfully"),
        error(end, "Error state");

        private final WorkflowStateType type;
        private final String description;

        State(final WorkflowStateType type, final String description) {
            this.type = type;
            this.description = description;
        }

        @Override
        public WorkflowStateType getType() {
            return type;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    @Autowired
    public CreateNotificationWorkflow(final WorkflowInstanceFactory workflowInstanceFactory,
            final JdbcClient jdbcClient, final JmsClient jmsClient) {
        super(TYPE, createEvent, error, new WorkflowSettings.Builder()
                .setMinErrorTransitionDelay(10000)
                .setMaxRetries(5)
                .build());
        this.workflowInstanceFactory = workflowInstanceFactory;
        this.jdbcClient = jdbcClient;
        this.jmsClient = jmsClient;
        permit(createEvent, sendNotification);
        permit(sendNotification, success);
    }

    public WorkflowInstance newInstanceWith(final Notification notification) {
        return workflowInstanceFactory.newWorkflowInstanceBuilder()
                .setType(TYPE).putStateVariable(VAR_NOTIFICATION, notification).build();
    }

    //
    // States Handlers (note that they are public and have the same names as the states in the enum above)
    //

    @SuppressWarnings("unused")
    public NextAction createEvent(final StateExecution execution) {
        final Notification notification = execution.getVariable(VAR_NOTIFICATION, Notification.class);
        logger.info("Going to create event for " + notification);
        jdbcClient.createEvent(notification);
        return moveToState(sendNotification, "Event created, going to send notification");
    }

    @SuppressWarnings("unused")
    public NextAction sendNotification(final StateExecution execution) {
        final Notification notification = execution.getVariable(VAR_NOTIFICATION, Notification.class);
        logger.info("Going to send " + notification + " to a message queue");
        jmsClient.sendNotification(notification);
        return moveToState(success, "Notification sent, going to success");
    }

    @SuppressWarnings("unused")
    public void success(final StateExecution execution) {
        final Notification notification = execution.getVariable(VAR_NOTIFICATION, Notification.class);
        logger.info("Notification " + notification + " processing success");
    }

    @SuppressWarnings("unused")
    public void error(final StateExecution execution) {
        final Notification notification = execution.getVariable(VAR_NOTIFICATION, Notification.class);
        logger.error("Notification " + notification + " processing error");
    }
}
