
package com.acuitybotting.security.rabbitmq.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginResult {

    private boolean success;
    private String[] tags;

    public String toResult(){
        if (success) {
            String r = "";
            for (int i = 0; i < tags.length; i++) {
                r += tags[i] + ",";
            }
            return r;
        }
        else {
            return "refused";
        }
    }
}