package com.company.vinfo;

import com.company.Interfaces.VinfoParser;
import com.company.utils.NetworkTools;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.C.INDEXFLAGS.*;
import static com.company.C.URL.*;

public class VakcinaInfoParser implements VinfoParser {

    private final NetworkTools networkTools = new NetworkTools();
    private final List<String> dataListFromVirusinfoPage = new ArrayList<>();
    private final ArrayList<Deceased> deceasedList = new ArrayList<>();

    /**
     * It reads every data from Vakcinainfo site. On Vakcinainfo site
     * you can add two parameters what sets the intervall of read (from page - to page).
     * You can use <Strong>findLastPage()</Strong> as the <Strong>to</Strong> parameter.
     */
    @Override
    public void readFromVakcinainfo(int from, int to) {
        fillData(from, to);  // to page can be findLastPage();
        fillDeceasedList();
    }

    /**
     * @return if deceasedList is not empty, it returns deceasedList. If deceasedList is empty, it returns the empty list and shows an error message.
     */
    public ArrayList<Deceased> getDeceasedList() {
        if (deceasedList.isEmpty()) {
            log("getDeceasedList()", "deceasedList empty.");
        }
        return deceasedList;
    }

    /**It finds number of the last page.
     * @return Number of the last page in <Strong>INTEGER</Strong>.
     */
    public int findlastpage() {

        int lastPage=0;
        networkTools.setHTTPSconnectionEnviroment();

        try {
            URL url = new URL(DEFAULT_URL +1);
            URLConnection connection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null)
                if(inputLine.contains(LASTLINE)) {
                    String temp = inputLine;
                    temp = temp.replaceAll("[^\\d.]", "");
                    lastPage = Integer.valueOf(temp);
                }
            br.close();

        } catch (IOException e) {
            log("findlastpage()", "deceasedList empty. + " + e);
        }
        return lastPage;
    }



    /**
      *  Read data from vakcinainfo. You have to add an intervall with index of fromPage and index of toPage. toPage can be <Strong>findLastPage(); </Strong><br>
      * @param fromPage set the index of the first page on <Strong>https://koronavirus.gov.hu/elhunytak</Strong>. The dataListFromVirusinfoPage list will be uploaded from this page.<br>
      * <Strong>!fromPage < 0 && !fromPage >= lastPage</Strong>
      * <br><br>
      * @param toPage set the index of the last page is on <Strong>https://koronavirus.gov.hu/elhunytak</Strong>. The dataListFromVirusinfoPage list will be uploaded from this page.
     *               <br><Strong>!toPage > lastPage && !toPage <= fromPage</Strong>
     */
    private void fillData(int fromPage, int toPage) {
        int lastPage = findlastpage();
        if (lastPage!=0) {
            if (isPageIntervallValid(fromPage, toPage, lastPage)) {
                readAndFillVirusinfoDataList(fromPage, toPage);
            }
            else {
                log("fillData()", "out of interval error.");
            }
        }
        else {
            log("fillData()", "Cant find last page.");
        }
    }

    private void readAndFillVirusinfoDataList(int fromPage, int toPage) {
        networkTools.setHTTPSconnectionEnviroment();
        for (int i = fromPage; i < toPage+1; i++) {
            System.out.println(DEFAULT_URL + i);
            fillDataListVirusinfoFromURL(i);
        }
    }

    /** This method fill the <Strong>dataListFromVirusinfoPage</Strong> list from virusinfo pages.
     * @param indexOfCycle index of read method's for cycle.
     */
    private void fillDataListVirusinfoFromURL(int indexOfCycle) {
        URL url;
        try {
            url = new URL(DEFAULT_URL +indexOfCycle);
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                dataListFromVirusinfoPage.add(inputLine);
            in.close();
        } catch (MalformedURLException e) {
            log("fillDataListVirusinfoFromURL()", "URL error. + " + e);
        }  catch (IOException e) {
            log("fillDataListVirusinfoFromURL()", "IOException. + " + e);
        }
    }

    /** This method checks that the <Strong>lastpage</Strong> parameter is definitely not outside the <Strong>frompage</Strong> and <Strong>topage</Strong> intervals.
     * @param fromPage index of first page what you want to read.<br><br>
     * @param toPage index of last page what you want to read.<br><br>
     * @param lastPage index of last page. It's the maximum value of toPage.
     */
    private boolean isPageIntervallValid(int fromPage, int toPage, int lastPage) {
        return !(fromPage <0 ) && (toPage > lastPage) ? false : true;
    }

     /**It cleans any duplications in a string list.
     * @param list Any String list.
     * @return a list cleared of duplications
     */
    private ArrayList<String> removeDuplicatesInStringList(List<String> list) {
        Set<String> stringsSet = new LinkedHashSet<>(list);
        return new ArrayList<>(stringsSet);
    }

    /**
     * It fills the decaesedList from dataListFromVirusinfoPage. It collects no of deceased, age, sex, underlying diseases
     */
    private void fillDeceasedList() {

        List<Integer> firstAndLastLine = findFirstAndLastLine();
        List<String> args = new ArrayList<>();
        deceasedList.clear();
        String[] underlyingDiseases;

        for (int i = 0; i < firstAndLastLine.size(); i=i+2) {

            int firstLine = firstAndLastLine.get(i);
            int lastLine = firstAndLastLine.get(i+1);

            for (int j = firstLine; j < lastLine; j++) {
                args.clear();
                if(dataListFromVirusinfoPage.get(j).contains(NO_OF_DECEASED)) {
                    args.add(cleanDeceasedObjectParameters(NO_OF_DECEASED, j));
                    args.add(cleanDeceasedObjectParameters(AGE_OF_DECEASED, j));
                    args.add(cleanDeceasedObjectParameters(SEX_OF_DECEASED, j));
                    underlyingDiseases = (cleanDeceasedObjectParameters(UNDERLYING_DISEASES_OF_DECEASED, j).split(", "));
                    underlyingDiseases = fillEmptySpacesInUnderlyingDisease(underlyingDiseases);
                    Collections.addAll(args, underlyingDiseases);
                    deceasedList.add(putArgsInDeceasedModel(args));
                    j=j+9;
                }
            }
        }
    }

    /**
     * The sql database stores six type of illnesses. If there are less than six underlying disease in a record it put an empty space in the DeceasedModel affected lines.
     * @param underlyingDiseases A string array with the deceased patient's underlying diseases.
     * @return Original array supplemented with blank fields.
     */
    private String[] fillEmptySpacesInUnderlyingDisease(String[] underlyingDiseases) {
        String[] result = new String[6];

        System.arraycopy(underlyingDiseases, 0, result, 0, Math.min(underlyingDiseases.length, 6));

        for (int i = underlyingDiseases.length; i < 6; i++) {
            result[i] = "";
        }
        return result;
    }

    /**
     * Adds the uploaded Deceased Model objects to the deceased List.
     * @param args The needed fields for the deceasedList.
     */
    private Deceased putArgsInDeceasedModel(List<String> args) {

        return new Deceased.DeceasedBuilder().
                setNo(Integer.valueOf(args.get(0))).
                setSex(args.get(1)).
                setAge(Integer.valueOf(args.get(2))).
                setI1(args.get(3)).
                setI2(args.get(4)).
                setI3(args.get(5)).
                setI4(args.get(6)).
                setI5(args.get(7)).
                setI6(args.get(8)).
                build();
    }

    private String cleanDeceasedObjectParameters(String type, int indexOfForCycle) {
        String temp="null";
        switch (type) {
            case NO_OF_DECEASED -> {
                temp = dataListFromVirusinfoPage.get(indexOfForCycle + 1);
                temp = temp.substring(0, temp.indexOf("<"));
                temp = temp.replaceAll(" ", "");
            }
            case AGE_OF_DECEASED -> {
                temp = dataListFromVirusinfoPage.get(indexOfForCycle + 3);
                temp = temp.substring(0, temp.indexOf("<"));
                temp = temp.replaceAll(" ", "");
            }
            case SEX_OF_DECEASED -> {
                temp = dataListFromVirusinfoPage.get(indexOfForCycle + 5);
                temp = temp.substring(0, temp.indexOf("<"));
                temp = temp.replaceAll(" ", "");
            }
            case UNDERLYING_DISEASES_OF_DECEASED -> {
                int firstCharPos;
                temp = dataListFromVirusinfoPage.get(indexOfForCycle + 7);
                char[] chars = temp.toCharArray();
                firstCharPos = findFirstLetterWhatsNotWhitespace(chars);
                temp = temp.substring(firstCharPos);
                temp = temp.replaceAll(TDLINE, "");
            }
        }
        return temp;
    }

    /**
     * @param charArray input charArray
     * @return It shows the location of the first whitespace in a line.
     */
    private int findFirstLetterWhatsNotWhitespace(char[] charArray) {
        int result=-1;
        for (int k = 0; k < charArray.length; k++) {
            if (!Character.isWhitespace(charArray[k])) {
                result = k;
                break;
            }
        }
        return result;
    }

    /**Every page in raw data list contains one "row-first" line and one "row-last" line. It finds start-end rows in the raw data list and put in a list.
     * @return A list with start and end positions.
     */
    private List<Integer> findFirstAndLastLine() {

        List<Integer> result = new ArrayList<>();
        if(dataListFromVirusinfoPage.size() != 0) {
            for (int i = 0; i < dataListFromVirusinfoPage.size(); i++) {
                if (dataListFromVirusinfoPage.get(i).contains("row-first")) {
                    result.add(i);
                }
                else if(dataListFromVirusinfoPage.get(i).contains("row-last")) {
                    result.add(i+8);
                }
            }
            result.add(dataListFromVirusinfoPage.size());
        }
        else {

            result = null;
        }
        return result;
    }

    private void log(String methodName, String message) {
        Logger.getLogger(VakcinaInfoParser.class.getName())
                .log(Level.INFO, "VakcinaInfoParser/" + methodName + " - " + message);
    }



}


