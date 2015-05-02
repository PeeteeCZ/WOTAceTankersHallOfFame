package priv.pethan.rest;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerListWrapper {
    private String status;
    private List<PlayerWithRank> data = new ArrayList<>();
}
