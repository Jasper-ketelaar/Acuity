package com.acuitybotting.website.acuity.security;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBasedAccessControl implements ViewInstanceAccessControl {

    @Override
    public boolean isAccessGranted(UI ui, String s, View view) {
        ViewAccess annotation = view.getClass().getAnnotation(ViewAccess.class);
        if (annotation != null){
            if (SecurityContextHolder.getContext().getAuthentication() == null) return false;
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(annotation.value());
            return ExpressionUtils.evaluateAsBoolean(exp, new StandardEvaluationContext(new SecurityExpressionRoot(SecurityContextHolder.getContext().getAuthentication()) {}));
        }
        return true;
    }
}