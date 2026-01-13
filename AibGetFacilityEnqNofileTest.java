
package com.temenos.aibl3.nofile.enq;

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
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.aaarrangement.ProductClass;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AltIdTypeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddesofficers.OtherOfficerClass;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.AaPrdDesSubArrangementConditionRecord;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.ProductLineClass;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.AttributeClass;

@RunWith(MockitoJUnitRunner.class)
public class AibGetFacilityEnqNofileTest {

    @Test
    public void setIds_fixed() {

        List<FilterCriteria> filterCriteria = new ArrayList<>();
        FilterCriteria fc = new FilterCriteria();
        fc.setFieldname("ARRANGEMENT.ID");
        fc.setOperand("1");
        fc.setValue("AA25209DMQLD");
        filterCriteria.add(fc);

        try (MockedConstruction<DataAccess> mockedDataAccess =
                     Mockito.mockConstruction(DataAccess.class, (da, context) -> {
                         AaArrangementRecord arrRecord = new AaArrangementRecord();
                         arrRecord.setStartDate("20250602");
                         arrRecord.setChannel("ncino", 0);
                         arrRecord.setOrigContractDate("20250602");

                         LinkedApplClass linkedApplClass = new LinkedApplClass();
                         linkedApplClass.setLinkedApplId("9965512304");
                         arrRecord.setLinkedAppl(linkedApplClass, 0);

                         CustomerClass cusclass = new CustomerClass();
                         cusclass.setCustomer("1234");
                         cusclass.setCustomerRole("APP");
                         arrRecord.setCustomer(cusclass, 0);

                         ProductClass prodclass = new ProductClass();
                         prodclass.setProduct("54002");
                         arrRecord.setProduct(prodclass, 0);

                         when(da.getRecord("AA.ARRANGEMENT", "AA25209DMQLD"))
                             .thenReturn(arrRecord.toStructure());
                     })) {

            try (MockedConstruction<Contract> mockedContractAccess =
                         Mockito.mockConstruction(Contract.class, (contract, context) -> {
                             AaArrTermAmountRecord arrtermamount = new AaArrTermAmountRecord();
                             arrtermamount.setAmount("10000");
                             arrtermamount.setMaturityDate("20550101");
                             arrtermamount.setFacilityType("COMMITTED");

                             when(contract.getConditionForProperty("FACOMMITMENT"))
                                 .thenReturn(arrtermamount.toStructure());
                         })) {

                try (MockedConstruction<Contract> mockedContractAccess1 =
                             Mockito.mockConstruction(Contract.class, (contract, context) -> {
                                 AaPrdDesAccountRecord aaPrdDesAccountRecord = new AaPrdDesAccountRecord();

                                 TField aibLocalRef = new TField();
                                 aibLocalRef.set("AIB.REGION.CODE");
                                 aibLocalRef.setValue("ROI");
                                 when(aaPrdDesAccountRecord.getLocalRefField("AIB.REGION.CODE"))
                                     .thenReturn(aibLocalRef);

                                 TField aibLocalRef1 = new TField();
                                 aibLocalRef1.set("AIB.ACCT.SECTOR");
                                 aibLocalRef1.setValue("162");
                                 when(aaPrdDesAccountRecord.getLocalRefField("AIB.ACCT.SECTOR"))
                                     .thenReturn(aibLocalRef1);

                                 List<AltIdTypeClass> altIdTypeClassList = new ArrayList<>();
                                 AltIdTypeClass altIdTypeClass = new AltIdTypeClass();
                                 TField altId = new TField("RO12BCREEERREEREAD21321");
                                 TField altIdType = new TField("IBAN");
                                 altIdTypeClass.setAltId(altId);
                                 altIdTypeClass.setAltIdType(altIdType);

                                 aaPrdDesAccountRecord.setAltIdType(altIdTypeClass, 0);
                                 altIdTypeClassList.add(altIdTypeClass);

                                 when(contract.getConditionForProperty("ACCOUNT"))
                                     .thenReturn(aaPrdDesAccountRecord.toStructure());
                             })) {

                    try (MockedConstruction<Contract> mockedContractAccess2 =
                                 Mockito.mockConstruction(Contract.class, (contract, context) -> {
                                     AaPrdDesOfficersRecord officerec = new AaPrdDesOfficersRecord();
                                     officerec.setPrimaryOfficer("1213");

                                     
                                     OtherOfficerClass officerclass = new OtherOfficerClass();
                                     officerclass.setOtherOfficer("324");
                                     officerclass.setOfficerRole("APP");

                                     officerec.setOtherOfficer(officerclass, 0);

                                     when(contract.getConditionForProperty("OFFICERS"))
                                         .thenReturn(officerec.toStructure());
                                 })) {

                        try (MockedConstruction<Contract> mockedContractAccess3 =
                                     Mockito.mockConstruction(Contract.class, (contract, context) -> {
                                         AaPrdDesSubArrangementConditionRecord drawingproprec =
                                             new AaPrdDesSubArrangementConditionRecord();

                                         ProductLineClass prodlineclass = new ProductLineClass();
                                         prodlineclass.setProductLine("LENDING");
                                         prodlineclass.setProperty("PENALINTEREST");
                                         AttributeClass Attrclass = new AttributeClass();
                                         Attrclass.setAttribute("PENALTY.RATE");
                                         Attrclass.setValue("5.6");
                                         prodlineclass.setAttribute(Attrclass, 0);
                                         // Stubbing getter
                                         when(drawingproprec.getProductLine(0)).thenReturn(prodlineclass);

                                         ProductLineClass prodlineclass1 = new ProductLineClass();
                                         prodlineclass1.setProductLine("");
                                         prodlineclass1.setProperty("PRINCIPALINT");
                                         AttributeClass Attrclass1 = new AttributeClass();
                                         Attrclass1.setAttribute("MARGIN.RATE");
                                         Attrclass1.setValue("5.6");
                                         prodlineclass1.setAttribute(Attrclass1, 0);
                                         when(drawingproprec.getProductLine(1)).thenReturn(prodlineclass1);

                                         ProductLineClass prodlineclass2 = new ProductLineClass();
                                         prodlineclass2.setProductLine("");
                                         prodlineclass2.setProperty("PRINCIPALINT");
                                         AttributeClass Attrclass2 = new AttributeClass();
                                         Attrclass2.setAttribute("INTEREST.FLOORING");
                                         Attrclass2.setValue("5.6");
                                         prodlineclass2.setAttribute(Attrclass2, 0);
                                         when(drawingproprec.getProductLine(2)).thenReturn(prodlineclass2);

                                         ProductLineClass prodlineclass3 = new ProductLineClass();
                                         prodlineclass3.setProductLine("");
                                         prodlineclass3.setProperty("ARREARSINTEREST");
                                         AttributeClass Attrclass3 = new AttributeClass();
                                         Attrclass3.setAttribute("MARGIN.RATE");
                                         Attrclass3.setValue("5.6");
                                         prodlineclass3.setAttribute(Attrclass3, 0);
                                         when(drawingproprec.getProductLine(3)).thenReturn(prodlineclass3);

                                         ProductLineClass prodlineclass4 = new ProductLineClass();
                                         prodlineclass4.setProductLine("");
                                         prodlineclass4.setProperty("ARREARSINTEREST");
                                         AttributeClass Attrclass4 = new AttributeClass();
                                         Attrclass4.setAttribute("INTEREST.FLOORING");
                                         Attrclass4.setValue("5.6");
                                         prodlineclass4.setAttribute(Attrclass4, 0);
                                         when(drawingproprec.getProductLine(4)).thenReturn(prodlineclass4);

                                         ProductLineClass prodlineclass5 = new ProductLineClass();
                                         prodlineclass5.setProductLine("");
                                         prodlineclass5.setProperty("ACCOUNT");
                                         AttributeClass Attrclass5 = new AttributeClass();
                                         Attrclass5.setAttribute("SECTOR.CODE");
                                         Attrclass5.setValue("1111");
                                         prodlineclass5.setAttribute(Attrclass5, 0);
                                         when(drawingproprec.getProductLine(5)).thenReturn(prodlineclass5);
                                     })) {
                            // At this point all mocked constructions are active within their scopes.
                            // If you need to invoke code under test, do it here.
                        }
                    }
                }
            }
        }
    }
}
