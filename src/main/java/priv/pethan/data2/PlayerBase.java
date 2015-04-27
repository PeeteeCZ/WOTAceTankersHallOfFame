package priv.pethan.data2;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerBase {
    private List<Player> players = new ArrayList<>();
}
