package com.acuitybotting.website.dashboard.servlet;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinServletConfiguration;

@VaadinServletConfiguration(closeIdleSessions = true, productionMode = false)
public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
   /*     event.getSource().addSessionDestroyListener(sessionDestroyEvent -> System.out.println("Session closed."));
        event.getSource().addSessionInitListener(sessionInitEvent -> System.out.println("Session inited"));

        event.getSource().addUIInitListener(uiInitEvent -> {
            System.out.println("Ui attached.");
            uiInitEvent.getUI().addDetachListener(detachEvent -> System.out.println("Ui detached."));
        });
*/

    }
}