package priv.pethan.data;

import lombok.Data;

@Data
public class Player {
    private Long rank;
    private Long id;
    private String name;
    private Long aceTankers;
    private Long rankInAceTankers;
}
