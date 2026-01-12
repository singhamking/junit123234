package com.temenos.aibl3.nofile.enq;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaproduct.AaProductRecord;
import com.temenos.t24.api.records.aaproperty.AaPropertyRecord;
import com.temenos.t24.api.records.aasimulationrunner.AaSimulationRunnerRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * description: java class for NOFILE ENQUIRY return a list with records API -
 * To fetch the AA condition details a Loan(s)
 * 
 * * ENQUIRY>NOFILE.AIB.GET.AA.CONDITION.DETAILS *
 * STANDARD.SELECTION>NOFILE.AIB.GET.AA.CONDITION.DETAILS *
 * EB.API>AIB.GET.AA.CONDITION.DETAILS
 * 
 * @author 76346
 * @param <T>
 *
 */

public class AibGetAAConditionDetails<T> extends Enquiry {

    private String idRec = "";

    @Override 
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        List<String> myList = new ArrayList<>();
        String id = "";
        String propertyFilter = "";

        Session ses = new Session(this);
        CompanyRecord comp = ses.getCompanyRecord();
        String companyMne = comp.getMnemonic().toString();

        DataAccess da = new DataAccess(this);

        for (int i = 0; i < filterCriteria.size(); i++) {
            if (filterCriteria.get(i).getFieldname().equals("ID"))
                id = filterCriteria.get(i).getValue();
            if (filterCriteria.get(i).getFieldname().equals("PROPERTY"))
                propertyFilter = filterCriteria.get(i).getValue();
        }

        try {
            if (id.substring(0, 2).equals("AA")) {
                if (id.substring(0, 5).equals("AASIM")) {
                    AaSimulationRunnerRecord aaSimulationRunnerRecord = new AaSimulationRunnerRecord(
                            da.getRecord("AA.SIMULATION.RUNNER", id));
                    AaArrangementRecord aaArrangementRecord = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT.SIM",
                            aaSimulationRunnerRecord.getArrangementRef().getValue()));

                    for (TField property : aaArrangementRecord.getProduct().get(0).getProperty()) {
                        String selCond = "WITH @ID LIKE " + aaSimulationRunnerRecord.getArrangementRef().getValue()
                                + "..." + property + "..." + id + "...";
                        if (propertyFilter.isEmpty() || propertyFilter.equals(property.getValue())) {
                            String dataRec = getAAPropertyRecord(selCond, property.getValue(), da, companyMne, "$SIM");
                            dataRec = dataRec.replaceAll("\\r|\\n", "").replaceAll("\\s+", "");
                            myList.add(property + "*" + idRec + "*" + dataRec);
                        }
                    }
                } else {
                    Contract contract = new Contract(this);
                    contract.setContractId(id);

                    for (String property : contract.getPropertyIds()) {
                        String selCond = "WITH @ID LIKE " + id + "..." + property + "...";
                        if (propertyFilter.isEmpty() || propertyFilter.equals(property)) {
                            String dataRec = getAAPropertyRecord(selCond, property, da, companyMne, "");
                            dataRec = dataRec.replaceAll("\\r|\\n", "").replaceAll("\\s+", "");
                            myList.add(property + "*" + idRec + "*" + dataRec);
                        }
                    }
                }
            } else {
                AaProductRecord aaProductRecord = new AaProductRecord(da.getRecord("AA.PRODUCT", id));
                for (TField property : aaProductRecord.getCatProperty()) {
                    String selCond = "WITH @ID LIKE " + id + "..." + property + "...";
                    if (propertyFilter.isEmpty() || propertyFilter.equals(property.getValue())) {
                        String dataRec = getAAProductPropertyRecord(selCond, property.getValue(), da, companyMne);
                        dataRec = dataRec.replaceAll("\\r|\\n", "").replaceAll("\\s+", "");
                        myList.add(property + "*" + idRec + "*" + dataRec);
                    } 
                }
            }

        } catch (Exception ignore) {

        }

        return myList;
    }

    private String getAAPropertyRecord(String selCond, String property, DataAccess da, String companyMne, String sim) {

        AaPropertyRecord aaPropertyRecord = new AaPropertyRecord(da.getRecord("AA.PROPERTY", property));
        String tableName = "AA.ARR." + aaPropertyRecord.getPropertyClass().getValue() + sim;

        try {
            List<String> aaArr = da.selectRecords(companyMne, tableName, "", selCond);

            if (!aaArr.isEmpty()) {
                idRec = aaArr.get(aaArr.size() - 1);

                String[] tableNameNoSim = tableName.split("\\$");
                return createObjectRecord(getT24TableName(tableNameNoSim[0]), da.getRecord(tableName, idRec))
                        .toString();
            }
        } catch (Exception ignore) {
        }

        idRec = "";
        return "";
    }

    private String getAAProductPropertyRecord(String selCond, String property, DataAccess da, String companyMne) {

        AaPropertyRecord aaPropertyRecord = new AaPropertyRecord(da.getRecord("AA.PROPERTY", property));
        String tableName = "AA.PRD.CAT." + aaPropertyRecord.getPropertyClass().getValue();

        try {
            List<String> aaArr = da.selectRecords("IE2", tableName, "", selCond);

            if (!aaArr.isEmpty()) {
                idRec = aaArr.get(aaArr.size() - 1);

                return createObjectRecord(getT24TableNameCat(tableName), da.getRecord(tableName, idRec)).toString();
            }
        } catch (Exception ignore) {
        }

        idRec = "";
        return "";
    }

    public String getT24TableNameCat(String tableName) {
        String tableNameObject = "com.temenos.t24.api.records.";
        String tableNameCamelCase = "";

        String[] tokens = tableName.split("\\.");
        int i = 0;
        for (String part : tokens) {
            if (i > 2) {
                tableNameCamelCase = tableNameCamelCase + part.charAt(0) + part.substring(1).toLowerCase();
            } else {
                if (i == 0)
                    tableNameCamelCase = tableNameCamelCase + "AaPrdDes";
            }
            i++;
        }

        return tableNameObject + tableNameCamelCase.toLowerCase() + "." + tableNameCamelCase + "Record";
    }

    public String getT24TableName(String tableName) {
        String tableNameObject = "com.temenos.t24.api.records.";
        String tableNameCamelCase = "";

        String[] tokens = tableName.split("\\.");
        int i = 0;
        for (String part : tokens) {
            if (i != 1) {
                tableNameCamelCase = tableNameCamelCase + part.charAt(0) + part.substring(1).toLowerCase();
            } else {
                tableNameCamelCase = tableNameCamelCase + "PrdDes";
            }
            i++;
        }

        return tableNameObject + tableNameCamelCase.toLowerCase() + "." + tableNameCamelCase + "Record";
    }

    static Object createObjectRecord(String className, TStructure record) {

        Object genericRecord = null;
        try {
            Class<?> genericClassDefinition = Class.forName(className);

            Class<?>[] constructArg = { TStructure.class };
            Constructor<?> ctor = genericClassDefinition.getConstructor(constructArg);

            Object[] obj = { record };
            genericRecord = ctor.newInstance(obj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return genericRecord;
    }
}
