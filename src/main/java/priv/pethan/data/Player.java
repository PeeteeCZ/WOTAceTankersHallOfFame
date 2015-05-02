package priv.pethan.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player {
    private String name;
    private Map<String, TimePoint> timePoints = new HashMap<>();
}
