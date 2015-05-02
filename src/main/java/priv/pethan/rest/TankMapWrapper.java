package priv.pethan.rest;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TankMapWrapper {
    private String status;
    private Map<Long,List<TankData>> data = new HashMap<>();
}
