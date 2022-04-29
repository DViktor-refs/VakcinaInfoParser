package com.company.Interfaces;

import com.company.vinfo.Deceased;

import java.util.List;

public interface ExporterInterface {

    void deceasedModelListToTxt(List<Deceased> listOfDeceased);
    void deceasedModelListToXls(List<Deceased> listOfDeceased);
    void deceasedModelListToSqlite(List<Deceased> listOfDeceased);
    void serializeDeceasedList(List<Deceased> listOfDeceased);

}
