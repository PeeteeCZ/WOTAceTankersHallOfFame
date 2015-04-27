package priv.pethan.rest;

import lombok.Data;

@Data
public class PlayerWithRank {
    private Rating global_rating;
    private Long account_id;
}
