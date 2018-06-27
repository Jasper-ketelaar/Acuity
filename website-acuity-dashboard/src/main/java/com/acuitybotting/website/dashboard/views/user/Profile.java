package com.acuitybotting.website.dashboard.views.user;

import com.acuitybotting.website.dashboard.security.view.interfaces.UsersOnly;
import com.acuitybotting.website.dashboard.views.navigation.NavigationLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

/**
 * Created by Zachary Herridge on 6/27/2018.
 */
@Route(value = "profile", layout = NavigationLayout.class)
public class Profile extends Div implements UsersOnly {
}
