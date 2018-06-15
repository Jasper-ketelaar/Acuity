package com.acuitybotting.website.acuity.notification;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import kaesdingeling.hybridmenu.components.Notification;
import kaesdingeling.hybridmenu.components.NotificationCenter;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public class Notifications {

    private static Notification buildDefault() {
        return Notification.get().withDisplayTime(3000);
    }

    private static void display(Notification notification) {
        NotificationCenter center = VaadinSession.getCurrent().getAttribute(NotificationCenter.class);
        if (center != null) center.add(notification);
    }

    public static void displayWarning(Exception exception) {
        displayWarning(exception.getMessage());
    }

    public static void displayWarning(String warning) {
        display(buildDefault()
                .withTitle("Warning")
                .withContent(warning)
                .withIcon(VaadinIcons.WARNING));
    }

    public static void displayInfo(String info) {
        display(buildDefault()
                .withTitle("Info")
                .withContent(info)
                .withIcon(VaadinIcons.INFO));
    }
}
