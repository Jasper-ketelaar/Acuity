package com.acuitybotting.website.acuity.notification;

import kaesdingeling.hybridmenu.components.Notification;

/**
 * Created by Zachary Herridge on 6/15/2018.
 */
public class CustomNotification extends Notification {

    @Override
    public Notification clone() {
        Notification clone = super.clone();
        clone.addLayoutClickListener(layoutClickEvent -> Notifications.markAsRead(this, clone));
        return clone;
    }
}
