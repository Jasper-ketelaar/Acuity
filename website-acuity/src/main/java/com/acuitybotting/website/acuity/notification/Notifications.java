package com.acuitybotting.website.acuity.notification;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CssLayout;
import kaesdingeling.hybridmenu.components.Notification;
import kaesdingeling.hybridmenu.components.NotificationCenter;

import java.lang.reflect.Field;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public class Notifications {

    private static Notification buildDefault() {
        return new CustomNotification()
                .withCloseable()
                .withDisplayTime(3000);
    }

    public static void markAsRead(CustomNotification notification){
        markAsRead(notification, null);
    }

    public static void markAsRead(CustomNotification notification, Notification popup){
        NotificationCenter center = VaadinSession.getCurrent().getAttribute(NotificationCenter.class);
        if (center != null) {
            notification.makeAsReaded();
            if (popup != null){
                popup.makeAsReaded();
                center.getUI().access(() -> {
                    try {
                        Field lastNotification = center.getClass().getDeclaredField("lastNotification");
                        lastNotification.setAccessible(true);
                        CssLayout o = (CssLayout) lastNotification.get(center);
                        o.removeComponent(popup);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            }
            center.updateToolTip();
        }
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
