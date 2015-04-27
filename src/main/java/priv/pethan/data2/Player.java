package priv.pethan.data2;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private Long id;
    private String name;
    private List<TimePoint> timePoints = new ArrayList<>();
}
