package priv.pethan.commands;

import priv.pethan.Main;
import priv.pethan.data.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class Export extends CommandBase {

    @Override
    public String getCommandName() {
        return "export";
    }

    @Override
    public void execute(String[] args) {
        Long rowCountToExport = (args.length == 1) ? 10000 : Long.valueOf(args[1]);

        loadFromFile();

        Collections.sort(playerList.getPlayers(), (p, q) -> q.getAceTankers().compareTo(p.getAceTankers()));

        long counter = 0;
        long lastAceTankers = 0;
        for (Player player : playerList.getPlayers()) {
            if (!player.getAceTankers().equals(lastAceTankers)) {
                counter++;
                lastAceTankers = player.getAceTankers();
            }
            player.setRankInAceTankers(counter);
        }

        System.out.println("Exporting as html");
        exportAsHtml(rowCountToExport);
    }

    private void exportAsHtml(Long rowCountToExport) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Main.dataFileName + ".html")))) {
            bw.write(getHtmlPageStart());
            bw.newLine();

            long counter = 1;
            for(Player player : playerList.getPlayers()) {
                bw.write(String.format("<tr%6$s><td>%1$d</td><td>%2$d</td><td><a href=\"http://worldoftanks.eu/community/accounts/%3$d-%4$s/\">%4$s</a></td><td>%5$d</td></tr>",
                        player.getRankInAceTankers(), player.getRank(), player.getId(), player.getName(), player.getAceTankers(), (counter % 2 == 0) ? " class=\"odd\"" : ""));
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

    private String getHtmlPageStart() {
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
                "\t\t\t<td colspan=\"3\">First 20,000 players according to number of ace tankers from top 500,000 by personal rating</td>\n" +
                "\t\t</tr>\n" +
                "\t\t</table>\n" +
                "\t\t\n" +
                "\t\t<table>\n" +
                "\t\t\t<th>Ace Rank</th>\n" +
                "\t\t\t<th>PR Rank</th>\n" +
                "\t\t\t<th>Player</th>\n" +
                "\t\t\t<th>Ace Tankers</th>";
    }

    private String getHtmlPageEnd() {
        return "\t\t</table>\n" +
                "\t\t</div>\n" +
                "\t</body>\n" +
                "</html>\n";
    }
}
