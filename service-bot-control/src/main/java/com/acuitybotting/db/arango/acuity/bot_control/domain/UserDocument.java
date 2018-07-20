package com.acuitybotting.db.arango.acuity.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Document("UserDocument")
@Getter
@Setter
@ToString
public class UserDocument {

    @Id
    private String key;

    private String userId;
    private String subGroup;
    private String subKey;
    private String subDocument;
    private String subHash;
}
