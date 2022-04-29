package com.company.worldmeters;

import com.company.Interfaces.WorldoMetersParser;
import com.company.utils.NetworkTools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.company.C.INDEXFLAGS.*;
import static com.company.C.URL.WORLDMETERS_URL;

public class WorldmetersParser implements WorldoMetersParser {

    private final NetworkTools networkTools = new NetworkTools();
    private final List<String> dataListWorld = new ArrayList<>();
    private final List<String> date = new ArrayList<>();
    private final List<Integer> dailyNewDeaths = new ArrayList<>();

    /**
     * Read data from <Strong>https://www.worldometers.info/coronavirus/</Strong>
     */
    @Override
    public void readFromWorldometers() {
        networkTools.setHTTPSconnectionEnviroment();
        fillDataListWorldFromURL();
        fillDailyNewDeathsList();
        fillDateList();
    }

    /**
     *  This method fill the <Strong>dataListWorld</Strong> list from worldometers pages.
     */
    private void fillDataListWorldFromURL() {

        try {
            URL url = new URL(WORLDMETERS_URL);
            URLConnection connection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null)
                dataListWorld.add(inputLine);
            br.close();
        }

        catch (MalformedURLException e) {
            log("fillDataListWorldFromURL()", "URL error. + " + e);
        }

        catch (IOException e) {
            log("fillDataListWorldFromURL()", "IOException error. + " + e);
        }
    }

    /**
     * This method fill the <Strong>dailyNewDeath</Strong> list from worldometers pages.
     */
    private List<Integer> fillDailyNewDeathsList() {
        dailyNewDeaths.clear();

        String[] nos = null;
        for (int i = 0; i < dataListWorld.size(); i++) {
            if (dataListWorld.get(i).contains(INDEX_OF_DAILY_NEW_DEATHS_LINE_IN_WORLDMETERSLIST)) {
                String temp = dataListWorld.get(i + 4);
                temp = temp.split("\\[")[1];
                nos = temp.split(",");
                break;
            }
        }

        for (int j = 0; j < nos.length; j++) {
            String substring = nos[j];
            if (substring.equals("null")) {
                substring = "0";
            }
            substring = substring.replaceAll("[^\\d]", "");
            dailyNewDeaths.add(Integer.parseInt(substring));
        }
        return dailyNewDeaths;
    }

    /**
     * This method fill the <Strong>dateList</Strong> list from worldometers pages.
     */
    private List<String> fillDateList() {
        date.clear();
        String temp = "";
        if (dataListWorld != null) {
            String[] nos;
            for (int i = 0; i < dataListWorld.size(); i++) {
                if (dataListWorld.get(i).contains(FIRST_LINE_IN_WORLDMETERS_DATALIST)) {
                    for (int j = i; j < dataListWorld.size(); j++) {
                        if (dataListWorld.get(j).contains(INDEX_OF_DATE_LINE_IN_WORLDMETERSLIST)) {
                            temp = dataListWorld.get(j);
                            temp = temp.replace(START_LINE_PART, "");
                            temp = temp.replace(CLOSE_LINE_PART, "");
                        }
                    }
                    break;
                }
            }
            nos = temp.split(",\"");

            for (int i = 0; i < nos.length; i++) {
                temp = nos[i].replaceAll("\",","");
                temp = temp.replaceAll("\"","");
                date.add(temp);
            }
        }
        else {
            log("fillDateList()", "dataListWorld List is empty.");
        }
        return date;
    }

    /**
     * This method is the getter of <Strong>date</Strong> list.
     */
    public List<String> getDateList() {
        if (!date.isEmpty()) {
            return date;
        }
        else {
            return null;
        }
    }

    /**
     * This method is the getter of <Strong>dailyNewDeaths</Strong> list.
     */
    public List<Integer> getDailyNewDeathsList() {
        if (!dailyNewDeaths.isEmpty()) {
            return dailyNewDeaths;
        }
        else {
            return null;
        }
    }

    private void log(String methodName, String message) {
        Logger.getLogger(WorldoMetersParser.class.getName())
                .log(Level.INFO, "Worldmeters/" + methodName + " - " + message);
    }
}
