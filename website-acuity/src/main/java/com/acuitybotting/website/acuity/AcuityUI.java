package com.acuitybotting.website.acuity;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by Zachary Herridge on 6/13/2018.
 */
@Theme("Valo")
@SpringUI
public class AcuityUI extends UI{

    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout mainLayout = new VerticalLayout(new Label("Hey my dudes"));
        setContent(mainLayout);
    }
}
