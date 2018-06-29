package com.acuitybotting.path_finding.xtea.domain;

import lombok.Getter;

@Getter
public class RsLocation {
    private int id;
    private int type;
    private int orientation;
    private EntityLocation position;
}