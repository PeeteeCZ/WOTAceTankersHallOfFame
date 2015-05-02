package priv.pethan.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class WOTRestService {
    private String WOT_BASE_URI = "http://api.worldoftanks.eu/wot";
    private Client client;
    private ObjectMapper objectMapper;

    public WOTRestService() {
        client = ClientBuilder.newClient();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Optional<PlayerWithRank> getFirstPlayer(String snapshotDate) {
        //http://api.worldoftanks.eu/wot/ratings/top/?application_id=demo&fields=account_id,global_rating&type=all&date=2015-03-01T00:00:00&rank_field=global_rating&limit=1
        try {
            URI uri = UriBuilder
                    .fromUri(WOT_BASE_URI).path("ratings/top/")
                    .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
                    .queryParam("fields", "account_id,global_rating")
                    .queryParam("type", "all")
                    .queryParam("date", snapshotDate)
                    .queryParam("rank_field", "global_rating")
                    .queryParam("limit", "1")
                    .build();

            Response response = client.target(uri).request().get();
            if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
                String responseJson = response.readEntity(String.class);
                PlayerListWrapper listWrapper = objectMapper.readValue(responseJson, PlayerListWrapper.class);

                if (listWrapper.getData().size() == 0) {
                    log.error("getFirstPlayer did not receive any data from " + uri.toString() + "\r\nJSON: " + responseJson);
                    return Optional.empty();
                } else {
                    return Optional.of(listWrapper.getData().get(0));
                }
            } else {
                log.error("getFirstPlayer response status " + response.getStatusInfo().getStatusCode() + " for request " + uri.toString());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("getFirstPlayer exception: ", e);
        }

        return Optional.empty();
    }

    public Optional<List<PlayerWithRank>> getNextPlayers(String snapshotDate, Long afterPlayerId, Long count) {
        //http://api.worldoftanks.eu/wot/ratings/neighbors/?application_id=demo&fields=account_id,global_rating&type=all&date=2015-03-01T00:00:00&account_id=502595931&rank_field=global_rating&limit=50
        try {
            URI uri = UriBuilder
                    .fromUri(WOT_BASE_URI).path("ratings/neighbors/")
                    .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
                    .queryParam("fields", "account_id,global_rating")
                    .queryParam("type", "all")
                    .queryParam("date", snapshotDate)
                    .queryParam("account_id", afterPlayerId)
                    .queryParam("rank_field", "global_rating")
                    .queryParam("limit", count)
                    .build();

            Response response = client.target(uri).request().get();
            if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
                String responseJson = response.readEntity(String.class);
                PlayerListWrapper listWrapper = objectMapper.readValue(responseJson, PlayerListWrapper.class);

                if (listWrapper.getData().size() == 0) {
                    log.error("getNextPlayers did not receive any data from " + uri.toString() + "\r\nJSON: " + responseJson);
                    return Optional.empty();
                } else {
                    List<PlayerWithRank> followingPlayers = new ArrayList<>();
                    boolean foundAfterPlayer = false;

                    for(PlayerWithRank playerWithRank : listWrapper.getData()) {
                        if (foundAfterPlayer) {
                            followingPlayers.add(playerWithRank);
                        } else {
                            if (afterPlayerId.equals(playerWithRank.getAccount_id())) {
                                foundAfterPlayer = true;
                            }
                        }
                    }

                    return Optional.of(followingPlayers);
                }
            } else {
                log.error("getNextPlayers response status " + response.getStatusInfo().getStatusCode() + " for request " + uri.toString());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("getNextPlayers exception: ", e);
        }

        return Optional.empty();
    }

    public Optional<Map<Long, String>> getPlayerNames(List<Long> playerIds) {
        //https://api.worldoftanks.eu/wot/account/info/?application_id=c2ed12673deddc2ea2bf97d9a7e5edab&fields=nickname&account_id=501816808,515776182
        try {
            URI uri = UriBuilder
                    .fromUri(WOT_BASE_URI).path("account/info/")
                    .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
                    .queryParam("fields", "nickname")
                    .queryParam("type", "all")
                    .queryParam("account_id", playerIds.stream().map(Object::toString).collect(joining(",")))
                    .build();

            Response response = client.target(uri).request().get();
            if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
                String responseJson = response.readEntity(String.class);
                PlayerMapWrapper mapWrapper = objectMapper.readValue(responseJson, PlayerMapWrapper.class);

                if (mapWrapper.getData().size() == 0) {
                    log.error("getPlayerNames did not receive any data from " + uri.toString() + "\r\nJSON: " + responseJson);
                    return Optional.empty();
                } else {
                    Map<Long, String> result = mapWrapper.getData().entrySet().stream()
                            .collect(toMap(Entry::getKey, entry -> entry.getValue().getNickname()));
                    return Optional.of(result);
                }
            } else {
                log.error("getPlayerNames response status " + response.getStatusInfo().getStatusCode() + " for request " + uri.toString());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("getPlayerNames exception: ", e);
        }

        return Optional.empty();
    }

    public Optional<Map<Long, PlayerWithRank>> getWOTRankings(List<Long> playerIds) {
        //https://api.worldoftanks.eu/wot/ratings/accounts/?application_id=c2ed12673deddc2ea2bf97d9a7e5edab&fields=global_rating&type=all&account_id=501816808,515776182
        try {
            URI uri = UriBuilder
                    .fromUri(WOT_BASE_URI).path("ratings/accounts/")
                    .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
                    .queryParam("fields", "global_rating")
                    .queryParam("type", "all")
                    .queryParam("account_id", playerIds.stream().map(Object::toString).collect(joining(",")))
                    .build();

            Response response = client.target(uri).request().get();
            if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
                String responseJson = response.readEntity(String.class);
                PlayerMapWrapper mapWrapper = objectMapper.readValue(responseJson, PlayerMapWrapper.class);

                if (mapWrapper.getData().size() == 0) {
                    log.error("getWOTRankings did not receive any data from " + uri.toString() + "\r\nJSON: " + responseJson);
                    return Optional.empty();
                } else {
                    return Optional.of(mapWrapper.getData());
                }
            } else {
                log.error("getWOTRankings response status " + response.getStatusInfo().getStatusCode() + " for request " + uri.toString());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("getWOTRankings exception: ", e);
        }

        return Optional.empty();
    }

    public Optional<Map<Long, Long>> getAceTankers(List<Long> playerIds) {
        //https://api.worldoftanks.eu/wot/account/tanks/?application_id=c2ed12673deddc2ea2bf97d9a7e5edab&fields=mark_of_mastery&account_id=501816808,515776182
        try {
            URI uri = UriBuilder
                    .fromUri(WOT_BASE_URI).path("account/tanks/")
                    .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
                    .queryParam("fields", "mark_of_mastery")
                    .queryParam("account_id", playerIds.stream().map(Object::toString).collect(joining(",")))
                    .build();

            Response response = client.target(uri).request().get();
            if (response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
                String responseJson = response.readEntity(String.class);
                TankMapWrapper mapWrapper = objectMapper.readValue(responseJson, TankMapWrapper.class);

                if (mapWrapper.getData().size() == 0) {
                    log.error("getAceTankers did not receive any data from " + uri.toString() + "\r\nJSON: " + responseJson);
                    return Optional.empty();
                } else {
                    Map<Long, Long> result = new HashMap<>();

                    for(Entry<Long,List<TankData>> player : mapWrapper.getData().entrySet()) {
                        if (isNull(player.getValue())) {
                            result.put(player.getKey(), null);
                        } else {
                            result.put(player.getKey(), player.getValue().stream().filter(tank -> tank.getMark_of_mastery() == 4).count());
                        }
                    }

                    return Optional.of(result);
                }
            } else {
                log.error("getAceTankers response status " + response.getStatusInfo().getStatusCode() + " for request " + uri.toString());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("getAceTankers exception: ", e);
        }

        return Optional.empty();
    }

    @Deprecated
    public Long getPlayerAceTankers(Long playerId) {
        //http://api.worldoftanks.eu/wot/tanks/stats/?application_id=demo&fields=mark_of_mastery&account_id=500867464
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("tanks/stats/")
                .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
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

    @Deprecated
    public String getPlayerName(Long playerId) {
        //http://api.worldoftanks.eu/wot/account/info/?application_id=demo&fields=nickname&account_id=500867464
        URI uri = UriBuilder
                .fromUri(WOT_BASE_URI).path("account/info/")
                .queryParam("application_id", "c2ed12673deddc2ea2bf97d9a7e5edab")
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

}
