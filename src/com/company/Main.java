package com.company;

import com.company.utils.Exporters;
import com.company.utils.NetworkTools;
import com.company.vinfo.VakcinaInfoParser;
import com.company.worldmeters.WorldmetersParser;

public class Main {

    public static void main(String[] args) {
        VakcinaInfoParser vakcinaParser = new VakcinaInfoParser();
        WorldmetersParser worldParser = new WorldmetersParser();
        Exporters exporter = new Exporters();
        NetworkTools networkTools = new NetworkTools();
        networkTools.showNetworkState();
        worldParser.readFromWorldometers();
        vakcinaParser.readFromVakcinainfo(0, vakcinaParser.findlastpage());

        //exporters
        exporter.deceasedModelListToTxt(vakcinaParser.getDeceasedList());
        exporter.deceasedModelListToXls(vakcinaParser.getDeceasedList());
        exporter.deceasedModelListToSqlite(vakcinaParser.getDeceasedList());
        exporter.serializeDeceasedList(vakcinaParser.getDeceasedList());

    }
}

