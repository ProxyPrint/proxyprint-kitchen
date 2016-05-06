/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.controllers.notifications;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.utils.NotificationManager;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author jose
 */
@RestController
public class NotificationsController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private NotificationManager notificationManager;

    @RequestMapping(value = "/consumer/{id}/notify", method = RequestMethod.POST)
    public void greeting(@PathVariable(value = "id") long id, WebRequest request, Principal principal) throws Exception {
        String message = request.getParameter("message");
        String name = consumers.findOne(id).getUsername();
        notificationManager.sendNotification(name, new Notification(message));
    }

    @RequestMapping(value = "/consumer/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(WebRequest request) throws Exception {
        String username = request.getParameter("username");
        return notificationManager.subscribe(username);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/notifications", method = RequestMethod.GET)
    public List<Notification> getConsumerNotifications(Principal principal) {
        Consumer consumer = this.consumers.findByUsername(principal.getName());
        return consumer.getNotifications();
    }
}
