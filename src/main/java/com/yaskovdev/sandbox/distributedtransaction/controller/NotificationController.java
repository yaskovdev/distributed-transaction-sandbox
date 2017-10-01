package com.yaskovdev.sandbox.distributedtransaction.controller;

import com.yaskovdev.sandbox.distributedtransaction.manager.NotificationManager;
import com.yaskovdev.sandbox.distributedtransaction.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class NotificationController {

    private final NotificationManager notificationManager;

    @Autowired
    public NotificationController(final NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @RequestMapping(method = POST, path = "/notifications")
    public void createNotification(@RequestBody final Notification notification) {
        notificationManager.createNotification(notification);
    }
}
