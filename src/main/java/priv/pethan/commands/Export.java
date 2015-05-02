package priv.pethan.commands;

import priv.pethan.Main;
import priv.pethan.data.Player;
import priv.pethan.data.TimePoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Export extends CommandBase {

    @Override
    public String getCommandName() {
        return "export";
    }

    @Override
    public void execute(String[] args) {
        Long rowCountToExport = 20000L;

        LocalDate snapshotDateDate = null;

        for (int idx = 1; idx < args.length; idx++) {
            String arg = args[idx];

            try {
                snapshotDateDate = LocalDate.parse(arg);
            } catch (DateTimeParseException ex) {
                rowCountToExport = Long.valueOf(arg);
            }
        }

        loadFromFile();

        String snapshotDate = isNull(snapshotDateDate) ? getLastSnapshotDate() : snapshotDateDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<Long, Player> withSnapshots = playerBase.getPlayers().entrySet().stream()
                .filter(entry -> entry.getValue().getTimePoints().containsKey(snapshotDate))
                .collect(toMap(Entry::getKey, Entry::getValue));


        List<Entry<Long, Player>> sorted = withSnapshots.entrySet().stream()
                .sorted((e1, e2) -> {
                    Long aces1 = e1.getValue().getTimePoints().get(snapshotDate).getAceTankersRank();
                    Long aces2 = e2.getValue().getTimePoints().get(snapshotDate).getAceTankersRank();

                    if (aces1.equals(aces2)) return e1.getValue().getTimePoints().get(snapshotDate).getWotRank().compareTo(e2.getValue().getTimePoints().get(snapshotDate).getWotRank());
                    return aces1.compareTo(aces2);
                })
                .collect(toList());

        System.out.println("Exporting as html");
        exportAsHtml(sorted, snapshotDate, rowCountToExport);
        System.out.println("Done");
    }

    private String getLastSnapshotDate() {
        Set<String> snapshotDates = new TreeSet<>();
        playerBase.getPlayers().values().stream().forEach(player -> snapshotDates.addAll(player.getTimePoints().keySet()));
        return snapshotDates.stream().max(Comparator.<String>naturalOrder()).get();
    }

    private void exportAsHtml(List<Entry<Long, Player>> sorted, String snapshotDate, Long rowCountToExport) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Main.dataFileName + ".html")))) {
            bw.write(getHtmlPageStart(rowCountToExport, snapshotDate));
            bw.newLine();

            long counter = 1;
            for(Entry<Long, Player> entry : sorted) {
                Player player = entry.getValue();
                TimePoint timePoint = player.getTimePoints().get(snapshotDate);

                bw.write(String.format("<tr%7$s><td><a href=\"http://worldoftanks.eu/community/accounts/%3$d-%4$s/\">%4$s</a></td><td>%1$d</td><td>%2$d</td><td>%5$d</td><td>%6$d</td></tr>",
                        timePoint.getAceTankersRank(), timePoint.getAceTankers(), entry.getKey(), player.getName(), timePoint.getWotRank(), timePoint.getWotRating(), (counter % 2 == 0) ? " class=\"odd\"" : ""));
                bw.newLine();

                counter++;
                if (rowCountToExport.equals(counter)) {
                    break;
                }
            }

            bw.write(getHtmlPageEnd());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
        }
    }

    private String getHtmlPageStart(Long rowCountToExport, String snapshotDate) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<style>\n" +
                "\t\t\th3 {\n" +
                "\t\t\t\tfont-family: \"WarHeliosCondC\",\"Arial Narrow\",Arial,sans-serif;\n" +
                "\t\t\t\tfont-stretch: normal;\n" +
                "\t\t\t\tfont-size: 21px;\n" +
                "\t\t\t\tcolor: #fff;\n" +
                "\t\t\t\tfont-weight: normal;\n" +
                "\t\t\t\tmargin: 0px 0 0px;\n" +
                "\t\t\t\tpadding: 0;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\tth {\n" +
                "\t\t\t\tfont-family: Arial,\"Helvetica CY\",Helvetica,sans-serif;\n" +
                "\t\t\t\tcolor: #606061;\n" +
                "\t\t\t\tpadding-left: 15px;\n" +
                "\t\t\t\tpadding-right: 15px;\n" +
                "\t\t\t\ttext-align: right;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\ttd {\n" +
                "\t\t\t\tcolor: #fff;\n" +
                "\t\t\t\tpadding-right: 15px;\n" +
                "\t\t\t\ttext-align: right;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\t.odd {\n" +
                "\t\t\t\tbackground-color: #1A1A1A;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\ta:hover {\n" +
                "\t\t\t\ttext-decoration: underline;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t\ta {\n" +
                "\t\t\t\tcolor: #fff;\n" +
                "\t\t\t\ttext-decoration: none;\n" +
                "\t\t\t}\n" +
                "\t\t\t\n" +
                "\t\t</style>\n" +
                "\t</head>\n" +
                "\t\n" +
                "\t<body bgcolor=\"black\">\n" +
                "\t\t<div align=\"center\">\n" +
                "\n" +
                "\t\t<table><tr>\n" +
                "\t\t<td><img src=\"http://worldoftanks.eu/static/3.26.0.4/common/img/classes/class-ace.png\" align=\"left\"/></td>\n" +
                "\t\t<td><h3 align=\"center\">Ace Tankers Hall of Fame</h3></td>\n" +
                "\t\t<td><img src=\"http://worldoftanks.eu/static/3.26.0.4/common/img/classes/class-ace.png\"/></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td colspan=\"3\">First " + rowCountToExport + " players according to number of ace tankers from top 500,000 by personal rating " + snapshotDate + "</td>\n" +
                "\t\t</tr>\n" +
                "\t\t</table>\n" +
                "\t\t\n" +
                "\t\t<table>\n" +
                "\t\t\t<th>Player</th>\n" +
                "\t\t\t<th>Ace Rank</th>\n" +
                "\t\t\t<th>Ace Tankers</th>\n" +
                "\t\t\t<th>WOT Rank</th>\n" +
                "\t\t\t<th>WOT Rating</th>";
    }

    private String getHtmlPageEnd() {
        return "\t\t</table>\n" +
                "\t\t</div>\n" +
                "\t</body>\n" +
                "</html>\n";
    }
}
