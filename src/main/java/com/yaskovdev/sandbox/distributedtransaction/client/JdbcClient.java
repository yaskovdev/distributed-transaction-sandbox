package com.yaskovdev.sandbox.distributedtransaction.client;

import com.yaskovdev.sandbox.distributedtransaction.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcClient {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcClient(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createEvent(final Notification notification) {
        jdbcTemplate.update("INSERT INTO notifications(type, name) VALUES (?, ?)",
                notification.getType(), notification.getName());
    }
}
