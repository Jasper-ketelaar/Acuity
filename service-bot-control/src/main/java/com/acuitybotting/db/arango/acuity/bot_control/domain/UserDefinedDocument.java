package com.acuitybotting.db.arango.acuity.bot_control.domain;

import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Created by Zachary Herridge on 7/19/2018.
 */
@Document("UserDefinedDocument")
@Getter
@Setter
@ToString
public class UserDefinedDocument {

    @Id
    private String _key;

    private String userId;
    private String subGroup;
    private String subKey;
    private String subDocument;
}
