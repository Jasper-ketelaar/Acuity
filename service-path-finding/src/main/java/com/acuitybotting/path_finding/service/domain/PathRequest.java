package com.acuitybotting.path_finding.service.domain;

import com.acuitybotting.path_finding.rs.domain.location.Location;
import com.acuitybotting.path_finding.service.domain.abstractions.player.RSPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PathRequest {

    private Location start, end;
    private RSPlayer rsPlayer;

}
