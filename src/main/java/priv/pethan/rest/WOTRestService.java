package priv.pethan.rest;

import com.jayway.jsonpath.JsonPath;
import priv.pethan.data.Player;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class WOTRestService {
    private String WOT_BASE_URI = "http://api.worldoftanks.eu/wot";
    private Client client;
    private Random random;

    public WOTRestService() {
        client = ClientBuilder.newClient();
        random = new Random(System.nanoTime());
    }

    public Player getFirstPlayer(String snapshotDate) {
        //http://api.worldoftanks.eu/wot/ratings/top/?application_id=demo&fields=account_id,global_rating.rank&type=all&date=2015-03-01T00:00:00&rank_field=global_rating&limit=1
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("ratings/top/")
                .queryParam("application_id", randomApplicationId())
                .queryParam("fields", "account_id,global_rating.rank")
                .queryParam("type", "all")
                .queryParam("date", snapshotDate)
                .queryParam("rank_field", "global_rating")
                .queryParam("limit", "1")
                .build();

        Response response = client.target(uri).request().get();
        if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
            PlayerListWrapper wrapper = response.readEntity(PlayerListWrapper.class);

            if (wrapper.getData().size() == 0) {
                throw new RuntimeException("REST query for first player failed");
            }

            return getPlayerFromWrapped(wrapper.getData().get(0));
        } else {
            throw new RuntimeException("REST query failed\r\n" + response.readEntity(String.class));
        }
    }

    private Player getPlayerFromWrapped(PlayerWithRank playerWithRank) {
        Player player = new Player();
        player.setRank(playerWithRank.getGlobal_rating().getRank());
        player.setId(playerWithRank.getAccount_id());

        return player;
    }

    public Long getPlayerAceTankers(Long playerId) {
        //http://api.worldoftanks.eu/wot/tanks/stats/?application_id=demo&fields=mark_of_mastery&account_id=500867464
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("tanks/stats/")
                .queryParam("application_id", randomApplicationId())
                .queryParam("fields", "mark_of_mastery")
                .queryParam("account_id", playerId)
                .build();

        Response response = client.target(uri).request().get();
        if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);

            List<String> aceTankers = JsonPath.read(responseJson, "$.data.*[?(@.mark_of_mastery == 4)]");
            return (long) aceTankers.size();
        } else {
            throw new RuntimeException("REST query failed\r\n" + response.readEntity(String.class));
        }
    }

    public String getPlayerName(Long playerId) {
        //http://api.worldoftanks.eu/wot/account/info/?application_id=demo&fields=nickname&account_id=500867464
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("account/info/")
                .queryParam("application_id", randomApplicationId())
                .queryParam("fields", "nickname")
                .queryParam("account_id", playerId)
                .build();

        Response response = client.target(uri).request().get();
        if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);

            List<String> nicknames = JsonPath.read(responseJson, "$..nickname");
            return nicknames.get(0);
        } else {
            throw new RuntimeException("REST query failed\r\n" + response.readEntity(String.class));
        }
    }

    public List<Player> getNextPlayers(String snapshotDate, Player afterPlayer, Long count) {
        //http://api.worldoftanks.eu/wot/ratings/neighbors/?application_id=demo&fields=account_id,global_rating.rank&type=all&date=2015-03-01T00:00:00&account_id=502595931&rank_field=global_rating&limit=50
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("ratings/neighbors/")
                .queryParam("application_id", randomApplicationId())
                .queryParam("fields", "account_id,global_rating.rank")
                .queryParam("type", "all")
                .queryParam("date", snapshotDate)
                .queryParam("account_id", afterPlayer.getId())
                .queryParam("rank_field", "global_rating")
                .queryParam("limit", count)
                .build();

        Response response = client.target(uri).request().get();
        if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
            PlayerListWrapper wrapper = response.readEntity(PlayerListWrapper.class);

            if (wrapper.getData().size() == 0) {
                throw new RuntimeException("REST query for first player failed");
            }

            return wrapper.getData().stream().filter(p -> p.getGlobal_rating().getRank() > afterPlayer.getRank()).map(this::getPlayerFromWrapped).collect(toList());
        } else {
            throw new RuntimeException("REST query failed\r\n" + response.readEntity(String.class));
        }
    }

    private String randomApplicationId() {
        return "ddeemmoo" + random.nextInt(100);
    }
}
