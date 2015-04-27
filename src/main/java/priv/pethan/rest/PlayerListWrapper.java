package priv.pethan.rest;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerListWrapper {
    private List<PlayerWithRank> data = new ArrayList<>();
}
