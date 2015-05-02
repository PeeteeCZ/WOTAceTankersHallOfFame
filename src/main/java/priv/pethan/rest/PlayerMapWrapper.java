package priv.pethan.rest;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerMapWrapper {
    private String status;
    private Map<Long,PlayerWithRank> data = new HashMap<>();
}
