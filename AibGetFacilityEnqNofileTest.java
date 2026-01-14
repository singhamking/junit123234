
package com.temenos.aibl3.nofile.enq;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.temenos.aibl3.de.mapping.TDateMatcher;
import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TNumber;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.aaarrangement.ProductClass;
import com.temenos.t24.api.records.aaarrsettlement.AaArrSettlementRecord;
import com.temenos.t24.api.records.aaarrsettlement.DdMandateRefClass;
import com.temenos.t24.api.records.aaarrsettlement.PayinCurrencyClass;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AltIdTypeClass;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddesofficers.OtherOfficerClass;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.AaPrdDesSubArrangementConditionRecord;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.AttributeClass;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.ProductLineClass;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ddddi.DdDdiRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.records.aaprddespaymentschedule.AaPrdDesPaymentScheduleRecord;
import com.temenos.t24.api.records.aaprddespaymentschedule.PaymentTypeClass;
import com.temenos.t24.api.records.aaprddespaymentschedule.PercentageClass;
import com.temenos.t24.api.records.aaprddespaymentschedule.PropertyClass;
import static org.mockito.ArgumentMatchers.argThat;

@RunWith(MockitoJUnitRunner.class)
public class AibGetFacilityEnqNofileTest {

    @Test
    public void testSetIds_TemenosStyle() throws Exception {

        // -----------------------------
        // Filter Criteria
        // -----------------------------
        FilterCriteria fc = new FilterCriteria();
        fc.setFieldname("ARRANGEMENT.ID");
        fc.setOperand("EQ");
        fc.setValue("AA25209DMQLD");

        List<FilterCriteria> filterCriteria = new ArrayList<>();
        filterCriteria.add(fc);

        EnquiryContext ctx = new EnquiryContext();

        // -----------------------------
        // Mocked constructions (Session, DataAccess, Contract)
        // -----------------------------
        try (
            MockedConstruction<Session> sessionMock =
                Mockito.mockConstruction(Session.class, (s, c) -> {
                    when(s.getCompanyId()).thenReturn("GB0010001");
                    when(s.getCompanyRecord()).thenReturn(new CompanyRecord());
                });

            // IMPORTANT: a single DataAccess construction for ALL getRecord stubs
            MockedConstruction<DataAccess> daMock =
                Mockito.mockConstruction(DataAccess.class, (da, c) -> {
                    // DATES
                    DatesRecord dates = new DatesRecord();
                    dates.setToday("20240101");
                    when(da.getRecord("DATES", "GB0010001")).thenReturn(dates.toStructure());

                    // AA.ARRANGEMENT
                    AaArrangementRecord arr = new AaArrangementRecord();
                    arr.setStartDate("20240101");
                    arr.setOrigContractDate("20240101");
                    arr.setCurrency("EUR");

                    LinkedApplClass lap = new LinkedApplClass();
                    lap.setLinkedApplId("123456789");
                    arr.setLinkedAppl(lap, 0);

                    CustomerClass cus = new CustomerClass();
                    cus.setCustomer("10001");
                    cus.setCustomerRole("APP");
                    arr.setCustomer(cus, 0);

                    ProductClass prod = new ProductClass();
                    prod.setProduct("54002");
                    arr.setProduct(prod, 0);

                    arr.setChannel("NCINO", 0);

                    when(da.getRecord("AA.ARRANGEMENT", "AA25209DMQLD"))
                        .thenReturn(arr.toStructure());

                    // DD.DDI
                    DdDdiRecord ddDdiRecord = new DdDdiRecord();
                    ddDdiRecord.setDescription("SDFDFGFDGDFG", 0);
                    ddDdiRecord.setDirection("34234");
                    ddDdiRecord.setParamId("9032");
                    ddDdiRecord.setClearSysRef("DFSFDSF");
                    ddDdiRecord.setCurrency("EUR");
                    ddDdiRecord.setCombineDdItems("Y");
                    ddDdiRecord.setStatus("NEW.ITEM");
                    ddDdiRecord.setCreateDate("20250606");
                    ddDdiRecord.setCurrencyMarket("1");
                    ddDdiRecord.setRequestType("DFDF");
                    ddDdiRecord.setIbanDestAcct("IE023423434536535");
                    ddDdiRecord.setCreditorId("234234234");
                    ddDdiRecord.setMandateScheme("DFDFD");
                    ddDdiRecord.setDebBankBic("BOFIIE2");
                    ddDdiRecord.setDebName("DFDFGDFGFDG");
                    ddDdiRecord.setDebAddress("DFGDFGFDG", 0);
                    ddDdiRecord.setDebCountry("IE");
                    ddDdiRecord.setCredName("SDFGDFGFDG");
                    ddDdiRecord.setCredCountry("IE");
                    ddDdiRecord.setCredAddress("DFDGFDG", 0);
                    ddDdiRecord.setTypeOfPayment("FDFG");
                    ddDdiRecord.setDateOfSigning("20241223");

                    when(da.getRecord("DD.DDI", "9876543.1"))
                        .thenReturn(ddDdiRecord.toStructure());
                });

            MockedConstruction<Contract> contractmock =
                Mockito.mockConstruction(Contract.class, (contract, c) -> {

                    // FACOMMITMENT (Term Amount)
                    AaArrTermAmountRecord term = new AaArrTermAmountRecord();
                    term.setAmount("10000");
                    term.setMaturityDate("20550101");
                    term.setFacilityType("COMMITTED");

                    when(contract.getConditionForPropertyEffectiveDate(
                            eq("FACOMMITMENT"), any(TDate.class)))
                        .thenReturn(term.toStructure());

                    // ACCOUNT
                    AaPrdDesAccountRecord acc = new AaPrdDesAccountRecord();

                    TField region = new TField();
                    region.set("ROI");
                    when(acc.getLocalRefField("AIB.REGION.CODE")).thenReturn(region);

                    TField sector = new TField();
                    sector.set("162");
                    when(acc.getLocalRefField("AIB.ACCT.SECTOR")).thenReturn(sector);

                    AltIdTypeClass altIdTypeClass = new AltIdTypeClass();
                    TField altId = new TField();
                    altId.set("RO12BCREEERREEREAD21321");
                    TField altIdType = new TField();
                    altIdType.set("IBAN");
                    altIdTypeClass.setAltId(altId);
                    altIdTypeClass.setAltIdType(altIdType);
                    acc.setAltIdType(altIdTypeClass, 0);

                    acc.setBusDayCentres("IE00", 0);
                    acc.setBusDayCentres("GB00", 1); // use next index

                    when(contract.getAccountCondition("ACCOUNT")).thenReturn(acc);

                    // OFFICERS
                    AaPrdDesOfficersRecord aaPrdDesOfficersRecord = new AaPrdDesOfficersRecord();
                    TField primaryOfficer = new TField();
                    primaryOfficer.setValue("935756");
                    aaPrdDesOfficersRecord.setPrimaryOfficer(primaryOfficer);

                    OtherOfficerClass otherOfficerClass = new OtherOfficerClass();
                    TField otherOfficer = new TField();
                    otherOfficer.setValue("12345");
                    otherOfficerClass.setOtherOfficer(otherOfficer);

                    TField otherOfficerRole = new TField();
                    otherOfficerRole.setValue("APPLICATION");
                    otherOfficerClass.setOfficerRole(otherOfficerRole);

                    aaPrdDesOfficersRecord.setOtherOfficer(otherOfficerClass, 0);

                    when(contract.getConditionForPropertyEffectiveDate(eq("OFFICERS"), any()))
                        .thenReturn(aaPrdDesOfficersRecord.toStructure());

                    // SUB ARRANGEMENT / DRAWING PROPERTIES (populate via setters; do not stub the POJO)
                    AaPrdDesSubArrangementConditionRecord subCond =
                        new AaPrdDesSubArrangementConditionRecord();

                    ProductLineClass plc0 = new ProductLineClass();
                    plc0.setProductLine("LENDING");
                    plc0.setProperty("PENALINTEREST");
                    AttributeClass attr0 = new AttributeClass();
                    attr0.setAttribute("PENALTY.RATE");
                    attr0.setValue("5.6");
                    plc0.setAttribute(attr0, 0);
                    subCond.setProductLine(plc0, 0);

                    ProductLineClass plc1 = new ProductLineClass();
                    plc1.setProductLine("");
                    plc1.setProperty("PRINCIPALINT");
                    AttributeClass attr1 = new AttributeClass();
                    attr1.setAttribute("MARGIN.RATE");
                    attr1.setValue("5.6");
                    plc1.setAttribute(attr1, 0);
                    subCond.setProductLine(plc1, 1);

                    ProductLineClass plc2 = new ProductLineClass();
                    plc2.setProductLine("");
                    plc2.setProperty("PRINCIPALINT");
                    AttributeClass attr2 = new AttributeClass();
                    attr2.setAttribute("INTEREST.FLOORING");
                    attr2.setValue("5.6");
                    plc2.setAttribute(attr2, 0);
                    subCond.setProductLine(plc2, 2);

                    ProductLineClass plc3 = new ProductLineClass();
                    plc3.setProductLine("");
                    plc3.setProperty("ARREARSINTEREST");
                    AttributeClass attr3 = new AttributeClass();
                    attr3.setAttribute("MARGIN.RATE");
                    attr3.setValue("5.6");
                    plc3.setAttribute(attr3, 0);
                    subCond.setProductLine(plc3, 3);

                    ProductLineClass plc4 = new ProductLineClass();
                    plc4.setProductLine("");
                    plc4.setProperty("ARREARSINTEREST");
                    AttributeClass attr4 = new AttributeClass();
                    attr4.setAttribute("INTEREST.FLOORING");
                    attr4.setValue("5.6");
                    plc4.setAttribute(attr4, 0);
                    subCond.setProductLine(plc4, 4);

                    ProductLineClass plc5 = new ProductLineClass();
                    plc5.setProductLine("");
                    plc5.setProperty("ACCOUNT");
                    AttributeClass attr5 = new AttributeClass();
                    attr5.setAttribute("SECTOR.CODE");
                    attr5.setValue("1111");
                    plc5.setAttribute(attr5, 0);
                    subCond.setProductLine(plc5, 5);

                    // If your code calls Contract for this property, stub it here:
                    // Adjust the property key to the exact one used by AibGetFacilityEnqNofile
                    when(contract.getConditionForPropertyEffectiveDate(eq("DRAWING.PROPERTIES"), any(TDate.class)))
                        .thenReturn(subCond.toStructure());

                    // SETTLEMENT
                    AaArrSettlementRecord aaArrSettlementRecord = new AaArrSettlementRecord();
                    PayinCurrencyClass payinCurrencyClass = new PayinCurrencyClass();
                    DdMandateRefClass ddMandateRefClass = new DdMandateRefClass();
                    TField dDMandateRef = new TField();
                    dDMandateRef.set("9876543.1");
                    ddMandateRefClass.setDdMandateRef(dDMandateRef);
                    payinCurrencyClass.setDdMandateRef(ddMandateRefClass, 0);
                    payinCurrencyClass.setPayinSettlement("YES");
                    aaArrSettlementRecord.setPayinCurrency(payinCurrencyClass, 0);

                    when(contract.getConditionForProperty("SETTLEMENT"))
                        .thenReturn(aaArrSettlementRecord.toStructure());
                    
                    
                  
                    AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord = new AaPrdDesPaymentScheduleRecord();

                    PaymentTypeClass pmtypeclass =  new PaymentTypeClass();
                    pmtypeclass.setPaymentType("COMMITFEES");
                    pmtypeclass.setPaymentMethod("DUE");
                    pmtypeclass.setPaymentFreq("e0Y e1M e0W e0D e0F");
                    pmtypeclass.setBillType("INSTALLMENT");
                    pmtypeclass.setDeferPeriod("7D");
                    
                    PropertyClass propclass = new PropertyClass();
                    propclass.setProperty("ACCOUNT");
                    pmtypeclass.setProperty(propclass, 0);
                    
                    PercentageClass prcntclass =  new PercentageClass();
                    prcntclass.setStartDate("D_20220202");
                    pmtypeclass.setPercentage(prcntclass, 0);
                    
                    aaPrdDesPaymentScheduleRecord.setPaymentType(pmtypeclass, 0);
                    when(contract.getConditionForPropertyEffectiveDate(eq("COMMITFEES"), any(TDate.class)))
                    .thenReturn(aaPrdDesPaymentScheduleRecord.toStructure());

                    AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord1 = new AaPrdDesPaymentScheduleRecord();

                    PaymentTypeClass pmtypeclass1 =  new PaymentTypeClass();
                    pmtypeclass1.setPaymentType("FACILITYFEE");
                    pmtypeclass1.setPaymentMethod("DUE");
                    pmtypeclass1.setPaymentFreq("e0Y e1M e0W e0D e0F");
                    pmtypeclass1.setBillType("INSTALLMENT");
                    pmtypeclass1.setDeferPeriod("7D");
                    
                    PropertyClass propclass1 = new PropertyClass();
                    propclass1.setProperty("ACCOUNT");
                    pmtypeclass1.setProperty(propclass1, 0);
                    
                    PercentageClass prcntclass1 =  new PercentageClass();
                    prcntclass1.setStartDate("D_20220202");
                    pmtypeclass1.setPercentage(prcntclass1, 0);
                    
                    aaPrdDesPaymentScheduleRecord.setPaymentType(pmtypeclass1, 0);
                    when(contract.getConditionForPropertyEffectiveDate(eq("FACILITYFEE"), any(TDate.class)))
                    .thenReturn(aaPrdDesPaymentScheduleRecord1.toStructure());
      
                 // INTEREST
                  
                    AaPrdDesInterestRecord intRec = new AaPrdDesInterestRecord();
                    FixedRateClass fr = new FixedRateClass();
                    fr.setEffectiveRate("1.25");
                    intRec.setFixedRate(fr, 0);
                    when(contract.getConditionForPropertyEffectiveDate(eq("CURCOMMITFEE"), any(TDate.class)))
                        .thenReturn(intRec.toStructure());

                    AaPrdDesInterestRecord intRec1 = new AaPrdDesInterestRecord();
                    FixedRateClass fr1 = new FixedRateClass();
                    fr1.setEffectiveRate("1.25");
                    intRec1.setFixedRate(fr1, 0);
                    when(contract.getConditionForPropertyEffectiveDate(eq("FACILITYFEE"), any(TDate.class)))
                        .thenReturn(intRec1.toStructure());
                 
                    TDate startDate = new TDate("20201224");
                    TDate todayDate = new TDate("20221001");

                    TNumber tNumber1 = new TNumber();
                    tNumber1.set("123");
                    
                    TNumber tAmt = new TNumber();
                    tAmt.set("12000");
                    BalanceMovement balance = new BalanceMovement();
                    balance.setBalance(tAmt);
                    TDate currDate = new TDate();
                    currDate.set("20231001");
                    balance.setDate(currDate);
                    List<BalanceMovement> balanceMovement = new ArrayList<>();
                    balanceMovement.add(balance);
                   

                    when(contract.getContractBalanceMovementsForPeriod(eq("TOTFACOMMITMENT"), eq("VALUE"),
                            argThat(new TDateMatcher(startDate)), argThat(new TDateMatcher(todayDate)))).thenReturn(balanceMovement);
                    
                    when(contract.getContractBalanceMovementsForPeriod(eq("CURFACOMMITMENT"), eq("VALUE"),
                            argThat(new TDateMatcher(startDate)), argThat(new TDateMatcher(todayDate))))
                                    .thenReturn(balanceMovement);

                    when(contract.getContractBalanceMovementsForPeriod(eq("UTLFACOMMITMENT"), eq("VALUE"),
                            argThat(new TDateMatcher(startDate)), argThat(new TDateMatcher(todayDate))))
                                    .thenReturn(balanceMovement);

                    when(contract.getContractBalanceMovementsForPeriod(eq("ACCCURCOMMITMENTFEE"), eq("VALUE"),
                            argThat(new TDateMatcher(startDate)), argThat(new TDateMatcher(todayDate))))
                                    .thenReturn(balanceMovement);
                })
        ) {
            // -----------------------------
            // Execute
            // -----------------------------
            AibGetFacilityEnqNofile enquiry = new AibGetFacilityEnqNofile();
            List<String> result = enquiry.setIds(filterCriteria, ctx);

            // -----------------------------
            // Assert
            // -----------------------------
            assertEquals(1, result.size());
            //assertTrue(result.get(0).contains("COMMITTED"));
            //assertTrue(result.get(0).contains("10000"));
        }
    }
}
