package com.acuitybotting.db.arango.path_finding.domain.xtea;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Arrays;

/**
 * Created by Zachary Herridge on 6/22/2018.
 */
@Getter
@Setter
@Document("RegionXtea")
public class Xtea {

    @Id
    private String id;

    private int revision;
    private int region;
    private int[] keys;

    @Override
    public String toString() {
        return "Xtea{" +
                "id='" + id + '\'' +
                ", revision=" + revision +
                ", region=" + region +
                ", keys=" + Arrays.toString(keys) +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Xtea)) return false;

        Xtea xtea = (Xtea) object;

        if (getRevision() != xtea.getRevision()) return false;
        if (getRegion() != xtea.getRegion()) return false;
        return Arrays.equals(getKeys(), xtea.getKeys());
    }

    @Override
    public int hashCode() {
        int result = getRevision();
        result = 31 * result + getRegion();
        result = 31 * result + Arrays.hashCode(getKeys());
        return result;
    }
}
