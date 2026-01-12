package com.temenos.aibl3.nofile.enq;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.temenos.api.TField;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.ProductClass;
import com.temenos.t24.api.records.aaproduct.AaProductRecord;
import com.temenos.t24.api.records.aaproperty.AaPropertyRecord;
import com.temenos.t24.api.records.aasimulationrunner.AaSimulationRunnerRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * description: java class test unit for NOFILE ENQUIRY
 * 
 * 
 * @author 76346
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class AibGetAAConditionDetailsTest {

    @Test
    public <T> void setIds_caseAA() {
        AibGetAAConditionDetails<T> aibGetAAConditionDetails = new AibGetAAConditionDetails<T>();

        List<FilterCriteria> filterCriteria = new ArrayList<>();
        filterCriteria.add(getFilterfcID("AA12312312"));

        try (MockedConstruction<Session> mockedSession = Mockito.mockConstruction(Session.class, (mock, context) -> {
            when(mock.getCompanyRecord()).thenReturn(getCompanyIE2());
        })) {

            try (MockedConstruction<DataAccess> mockedDataAccess = Mockito.mockConstruction(DataAccess.class,
                    (da, context) -> {
                        when(da.getRecord("AA.PROPERTY", "ACCOUNT")).thenReturn(getAaPropertyRecord().toStructure());
                        when(da.selectRecords("IE2", "AA.ARR.ACCOUNT", "", "WITH @ID LIKE AA12312312...ACCOUNT..."))
                                .thenReturn(getSelectIds());

                    })) {
                try (MockedConstruction<Contract> contractRecord = Mockito.mockConstruction(Contract.class,
                        (contract, context) -> {
                            when(contract.getPropertyIds()).thenReturn(getAAPropertyIds());

                        })) {

                    List<String> returnList = aibGetAAConditionDetails.setIds(filterCriteria, null);

                    assertEquals(1, returnList.size());
                }
            }
        }
    }

    @Test
    public <T> void setIds_caseAASIM() {
        AibGetAAConditionDetails<T> aibGetAAConditionDetails = new AibGetAAConditionDetails<T>();

        List<FilterCriteria> filterCriteria = new ArrayList<>();
        filterCriteria.add(getFilterfcID("AASIM12312312"));

        try (MockedConstruction<Session> mockedSession = Mockito.mockConstruction(Session.class, (mock, context) -> {
            when(mock.getCompanyRecord()).thenReturn(getCompanyIE2());
        })) {

            try (MockedConstruction<DataAccess> mockedDataAccess = Mockito.mockConstruction(DataAccess.class,
                    (da, context) -> {
                        when(da.getRecord("AA.PROPERTY", "ACCOUNT")).thenReturn(getAaPropertyRecord().toStructure());
                        when(da.getRecord("AA.SIMULATION.RUNNER", "AASIM12312312"))
                                .thenReturn(getAaSimulationRunnerRecord().toStructure());
                        when(da.getRecord("AA.ARRANGEMENT.SIM", "AA12312312"))
                                .thenReturn(getArrangementRecord().toStructure());
                        when(da.selectRecords("IE2", "AA.ARR.ACCOUNT", "", "WITH @ID LIKE AASIM12312312...ACCOUNT..."))
                                .thenReturn(getSelectIds());

                    })) {

                List<String> returnList = aibGetAAConditionDetails.setIds(filterCriteria, null);

                assertEquals(1, returnList.size());

            }
        }
    }

    @Test
    public <T> void setIds_caseAAProduct() {
        AibGetAAConditionDetails<T> aibGetAAConditionDetails = new AibGetAAConditionDetails<T>();

        List<FilterCriteria> filterCriteria = new ArrayList<>();
        filterCriteria.add(getFilterfcID("54101"));

        try (MockedConstruction<Session> mockedSession = Mockito.mockConstruction(Session.class, (mock, context) -> {
            when(mock.getCompanyRecord()).thenReturn(getCompanyIE2());
        })) {

            try (MockedConstruction<DataAccess> mockedDataAccess = Mockito.mockConstruction(DataAccess.class,
                    (da, context) -> {
                        when(da.getRecord("AA.PROPERTY", "ACCOUNT")).thenReturn(getAaPropertyRecord().toStructure());
                        when(da.getRecord("AA.PRODUCT", "54101")).thenReturn(getAaProductRecord().toStructure());
                        when(da.selectRecords("IE2", "AA.PRD.CAT.ACCOUNT", "", "WITH @ID LIKE 54101...ACCOUNT..."))
                                .thenReturn(getSelectIds());

                    })) {

                List<String> returnList = aibGetAAConditionDetails.setIds(filterCriteria, null);

                assertEquals(1, returnList.size());

            }
        }
    }

    public FilterCriteria getFilterfcID(String id) {
        FilterCriteria fcID = new FilterCriteria();

        fcID.setFieldname("ID");
        fcID.setOperand("1");
        fcID.setValue(id);

        return fcID;
    }

    public List<String> getAAPropertyIds() {
        List<String> aaPropertyIds = new ArrayList<>();
        aaPropertyIds.add("ACCOUNT");

        return aaPropertyIds;
    }

    public List<String> getSelectIds() {
        List<String> aaSelectIds = new ArrayList<>();
        aaSelectIds.add("AA-ACCOUNT");

        return aaSelectIds;
    }

    public AaPropertyRecord getAaPropertyRecord() {
        AaPropertyRecord aaPropertyRecord = new AaPropertyRecord();

        TField propertyClass = new TField("ACCOUNT");
        aaPropertyRecord.setPropertyClass(propertyClass);

        return aaPropertyRecord;
    }

    public AaProductRecord getAaProductRecord() {
        AaProductRecord aaProductRecord = new AaProductRecord();

        aaProductRecord.setCatProperty(new TField("ACCOUNT"), 0);

        return aaProductRecord;
    }

    public AaArrangementRecord getArrangementRecord() {
        AaArrangementRecord aaArrangementRecord = new AaArrangementRecord();

        ProductClass productClass = new ProductClass();
        productClass.setProduct("54103");
        productClass.setProperty(new TField("ACCOUNT"), 0);
        aaArrangementRecord.setProduct(productClass, 0);

        return aaArrangementRecord;
    }

    public AaSimulationRunnerRecord getAaSimulationRunnerRecord() {
        AaSimulationRunnerRecord aaSimulationRunnerRecord = new AaSimulationRunnerRecord();

        aaSimulationRunnerRecord.setArrangementRef(new TField("AA12312312"));

        return aaSimulationRunnerRecord;
    }

    public CompanyRecord getCompanyIE2() {
        TField compMne = new TField();
        compMne.setValue("IE2");

        CompanyRecord compRec = new CompanyRecord();
        compRec.setMnemonic(compMne);

        return compRec;
    }
}