package com.temenos.aibl3.nofile.enq;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrsettlement.AaArrSettlementRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddespaymentschedule.AaPrdDesPaymentScheduleRecord;
import com.temenos.t24.api.records.aaprddessubarrangementcondition.AaPrdDesSubArrangementConditionRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ddddi.DdDdiRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * description: java class for NOFILE ENQUIRY return the facility details
 * 
 * ENQUIRY>AIB.GETMIG.FACILITYDETS
 * STANDARD.SELECTION>NOFILE.AIB.AA.GET.MIGFACILITYDTLS
 * EB.API>AIB.GETMIG.FACILITYDETS
 * 
 * @author 90276
 * @param <arrangement id>
 *
 */

public class AibGetFacilityEnqNofile extends Enquiry {
    List < String > myList = new ArrayList < > ();
    String  myList1 = "";
    String  myList2 = "";
    String  myList3 = "";
    String  myList4 = "";
    String  myList5 = "";
    String  myList6 = "";
    String  myList7 = "";
    String  myList8 = "";
    String  myList9 = "";
    String  myList10 = "";
    String  myList11 = "";
    String  myList12 = "";
    
    Session session = new Session(this);
    DataAccess da = new DataAccess(this);
    CompanyRecord comp = session.getCompanyRecord();
    DatesRecord datesRecord = new DatesRecord(da.getRecord("DATES", session.getCompanyId()));
    String sTodayDate = datesRecord.getToday().getValue();
    TDate todayDate = new TDate(sTodayDate);


    @Override
    public List <String> setIds(List < FilterCriteria > filterCriteria, EnquiryContext enquiryContext) {

        String arrId = filterCriteria.get(0).getValue();
        Contract contract = new Contract(this);
        contract.setContractId(arrId);

        String aaStartDate = getArrangementRecord(arrId);
        geTermAmtDetails(contract);
        getAccounrcondrecord(contract);
        getOfficerrecord(contract, todayDate);
        getdrawingdtls(contract, todayDate);
        getsettlemenconddetails(contract, da, todayDate);
        getpmtscheduleconddetails(contract, todayDate);
        getecbbalances(aaStartDate, contract, todayDate);

        myList.add(myList1 + "*"+ myList2+"*"+myList3 + "*"+ myList4+"*"+myList5 + "*"+ myList6+"*"+myList7 + "*"+ myList10 + "*" + myList8 +"*"+myList11 + "*"+ myList9+"*"+ myList12);
        
        return myList;

    }
 
   
    public String getArrangementRecord(String arrId) {
        
        AaArrangementRecord aaArrangementRecord = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT", arrId));

        String accountID = aaArrangementRecord.getLinkedAppl(0).getLinkedApplId().getValue();
        String aastdate = aaArrangementRecord.getStartDate().toString();
        String customerNum = aaArrangementRecord.getCustomer(0).getCustomer().getValue();
        String customerRole = aaArrangementRecord.getCustomer(0).getCustomerRole().getValue();

        String aaproduct = aaArrangementRecord.getProduct(0).getProduct().toString();
        String aaccy = aaArrangementRecord.getCurrency().toString();

        List < TField > channelval = aaArrangementRecord.getChannel();
        String finalchannelval = getchannelvalue(channelval);

        String aaorigcontdate = aaArrangementRecord.getOrigContractDate().getValue();

        myList1 = arrId + "*" + accountID + "*" + aastdate + "*" + customerNum + "*" + customerRole + "*" + aaproduct + "*" + aaccy + "*" + finalchannelval + "*" + aaorigcontdate;
       
        return aastdate;

    }

    public String getchannelvalue(List < TField > channelval) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < channelval.size(); i++) {
            sb.append(channelval.get(i).getValue());
            if (i < channelval.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    public void geTermAmtDetails(Contract contract) {

        TStructure termamtproperty = contract.getConditionForPropertyEffectiveDate("FACOMMITMENT", todayDate);
        AaArrTermAmountRecord aatermamtrec = new AaArrTermAmountRecord(termamtproperty);
        String commitmentamt = aatermamtrec.getAmount().getValue();
        String matdate = aatermamtrec.getMaturityDate().getValue();
        String factype = aatermamtrec.getFacilityType().getValue();
        
        myList2 = commitmentamt + "*" + matdate + "*" + factype;
       
    }


    public void getAccounrcondrecord(Contract contract) {

        AaPrdDesAccountRecord aaPrdDesAccountRecord = contract.getAccountCondition("ACCOUNT");

        String aibsectorcode = aaPrdDesAccountRecord.getLocalRefField("AIB.ACCT.SECTOR").getValue();
        String aibRegionCode = aaPrdDesAccountRecord.getLocalRefField("AIB.REGION.CODE").getValue();

        String altid1 = aaPrdDesAccountRecord.getAltIdType(0).getAltId().getValue();
        String altid2 = aaPrdDesAccountRecord.getAltIdType(1).getAltId().getValue();
        String altid4 = aaPrdDesAccountRecord.getAltIdType(3).getAltId().getValue();

        String busdaycentr1 = aaPrdDesAccountRecord.getBusDayCentres(0).getValue();
        String busdaycentr2 = aaPrdDesAccountRecord.getBusDayCentres(1).getValue();
 
        myList3 = altid1 + "*" + altid2 + "*" + altid4 + "*" + aibsectorcode + "*" + busdaycentr1 + "*" + busdaycentr2 + "*" + aibRegionCode;

    }


    public void getOfficerrecord(Contract contract, TDate todayDate) {

        TStructure Officerprop = contract.getConditionForPropertyEffectiveDate("OFFICERS", todayDate);
        AaPrdDesOfficersRecord Officerproprec = new AaPrdDesOfficersRecord(Officerprop);
        String primaryofficer = Officerproprec.getPrimaryOfficer().getValue();

        int officercnt = Officerproprec.getOtherOfficer().size();
       
        myList4 = primaryofficer;
        getofficervalue(officercnt, Officerproprec);
    }

    public void getofficervalue(int officercnt, AaPrdDesOfficersRecord Officerproprec) {
        String finalofficerval= "";
        String finaloffroleval = "";

        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        if (officercnt > 0) {
            for (int i = 0; i < officercnt; i++) {
                sb.append(Officerproprec.getOtherOfficer(i).getOtherOfficer().getValue());
                sb1.append(Officerproprec.getOtherOfficer(i).getOfficerRole().getValue());

                if (i < officercnt - 1) {
                    sb.append(";");
                    sb1.append(";");
                }
            }
            finalofficerval = sb.toString();
            finaloffroleval = sb1.toString();
        } else {
            finalofficerval = sb.toString();
            finaloffroleval = sb1.toString();

        }
        
        myList5 = finalofficerval + "*" + finaloffroleval;
    }

    public void getdrawingdtls(Contract contract, TDate todayDate) {

        final String PL_PENALINTEREST = "PENALINTEREST";
        final String PL_PRINCIPALINT = "PRINCIPALINT";
        final String PL_ARREARSINTEREST = "ARREARSINTEREST";
        final String PL_ACCOUNT = "ACCOUNT";

        TStructure drawingprop = contract.getConditionForPropertyEffectiveDate("DRAWING.CONDITIONS", todayDate);
        AaPrdDesSubArrangementConditionRecord drawingproprec = new AaPrdDesSubArrangementConditionRecord(drawingprop);

        int prodlinecnt = drawingproprec.getProductLine().size();

        String penaltyattributeval = "";
        String prinattributeval = "";
        String prinintflooval = "";
        String arrearsattributeval = "";
        String arrearsInterestFlooringval = "";
        String acctseccode = "";
        for (int i = 0; i < prodlinecnt; i++) {

            String drwprop = drawingproprec.getProductLine(i).getProperty().getValue();
            int attributecnt = drawingproprec.getProductLine(i).getAttribute().size();

            switch (drwprop) {

                case PL_PENALINTEREST:
                    penaltyattributeval = getpenattval(i, attributecnt,drawingproprec);
                    break;

                case PL_PRINCIPALINT:
                    if (prinattributeval.isEmpty()) {
                        prinattributeval = getprinattval(i, attributecnt,drawingproprec);
                    }
                    if (prinintflooval.isEmpty()) {
                        prinintflooval = getintfloorval(i, attributecnt,drawingproprec);
                    }
                    break;

                case PL_ARREARSINTEREST: 
                    if (arrearsattributeval.isEmpty()) {
                        arrearsattributeval = getarrearattval(i, attributecnt,drawingproprec);
                    }
                    if (arrearsInterestFlooringval.isEmpty()) {
                        arrearsInterestFlooringval = getarrintflrval(i, attributecnt,drawingproprec);
                    }
                    break;

                case PL_ACCOUNT:
                    acctseccode = getsectorval(i, attributecnt,drawingproprec);
                    break;

                default:
                    break;
            }
        }
        myList6 = penaltyattributeval + "*" + prinattributeval + "*" + arrearsattributeval + "*" + acctseccode + "*" + prinintflooval + "*" + arrearsInterestFlooringval;
    }

 
   
    public String getpenattval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String penaltyattributeval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("PENALTY.RATE".equals(attribute1)) {
                penaltyattributeval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
            }
        }
        
        return penaltyattributeval;
    }
    
    public String getprinattval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String prinattributeval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("MARGIN.RATE".equals(attribute1)) {
                prinattributeval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
            }
        }
        return prinattributeval;
      }
   
    public String getintfloorval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String intflooringval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("INTEREST.FLOORING".equals(attribute1)) {
                intflooringval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
          }
        }
        return intflooringval;
    }

    public String getarrearattval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String arrearsattributeval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("MARGIN.RATE".equals(attribute1)) {
                arrearsattributeval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
            }
        }
        return arrearsattributeval;
      }
   
    public String getarrintflrval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String arrearsInterestFlooringval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("INTEREST.FLOORING".equals(attribute1)) {
                arrearsInterestFlooringval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
            }
        }
        return arrearsInterestFlooringval;
    }
   
    public String getsectorval(int i, int attributecnt, AaPrdDesSubArrangementConditionRecord drawingproprec) {
        String secodeval = "";
        String attribute1 = "";
        for (int j = 0; j < attributecnt; j++) {
            attribute1 = drawingproprec.getProductLine(i).getAttribute(j).getAttribute().getValue();
            if ("SECTOR.CODE".equals(attribute1)) {
                secodeval = drawingproprec.getProductLine(i).getAttribute(j).getValue().getValue();
            }
        }
        return secodeval;
    }
      
    public void getsettlemenconddetails(Contract contract, DataAccess da, TDate todayDate) {

        TStructure settlementProperty = contract.getConditionForPropertyEffectiveDate("SETTLEMENT", todayDate);
        AaArrSettlementRecord aaArrSettlementRecord = new AaArrSettlementRecord(settlementProperty);

        String mandateReference = "";
       
        if (!aaArrSettlementRecord.getPayinCurrency(0).getDdMandateRef().isEmpty()) {
            mandateReference = aaArrSettlementRecord.getPayinCurrency(0).getDdMandateRef().get(0).getDdMandateRef().getValue();
        }

        getddirecorddetails(mandateReference, da);
    }

    /**
     * @param mandateReference
     */
    public void getddirecorddetails(String mandateReference, DataAccess da) {

        DdDdiRecord ddDdiRecord = new DdDdiRecord(da.getRecord("DD.DDI", mandateReference));

        String ddidesc = ddDdiRecord.getDescription(0).getValue();
        String ddddiDirection = ddDdiRecord.getDirection().getValue();
        String ddddiParamId = ddDdiRecord.getParamId().getValue();
        String ddddiClearSysRef = ddDdiRecord.getClearSysRef().getValue();
        String ddddiCurrency = ddDdiRecord.getCurrency().getValue();
        String ddddiCombineDdItems = ddDdiRecord.getCombineDdItems().getValue();
        String ddddiStatus = ddDdiRecord.getStatus().getValue();
        String ddddiCreateDate = ddDdiRecord.getCreateDate().getValue();
        String ddddiCurrencyMarket = ddDdiRecord.getCurrencyMarket().getValue();
        String ddddiRequestType = ddDdiRecord.getRequestType().getValue();
        String ddddiIbanDestAcct = ddDdiRecord.getIbanDestAcct().getValue();
        String ddddiCreditorId = ddDdiRecord.getCreditorId().getValue();
        String ddddiMandateScheme = ddDdiRecord.getMandateScheme().getValue();
        ddddiMandateScheme = ddddiMandateScheme.replace("*", "-");
        String ddddiDebBankBic = ddDdiRecord.getDebBankBic().getValue();
        String ddddiDebName = ddDdiRecord.getDebName().getValue();


        List < TField > dddebaddressval = ddDdiRecord.getDebAddress();
        String ddddiDebCountry = ddDdiRecord.getDebCountry().getValue();
        String ddddiCredName = ddDdiRecord.getCredName().getValue();

        List < TField > ddcredaddressval = ddDdiRecord.getCredAddress();
        String ddddiCredCountry = ddDdiRecord.getCredCountry().getValue();
        String ddddiTypeOfPayment = ddDdiRecord.getTypeOfPayment().getValue();
        String ddddiDateOfSigning = ddDdiRecord.getDateOfSigning().getValue();
        
        String mandateReference1 = "D-" + mandateReference;
    
        myList7 = mandateReference1 + "*" + ddidesc + "*" + ddddiDirection + "*" + ddddiParamId + "*" + ddddiClearSysRef + "*" + ddddiCurrency + "*" + ddddiCombineDdItems + "*" + ddddiStatus + "*" + ddddiCreateDate + "*" + ddddiCurrencyMarket + "*" + ddddiRequestType + "*" + ddddiIbanDestAcct + "*" + ddddiCreditorId + "*" + ddddiMandateScheme + "*" + ddddiDebBankBic + "*" + ddddiDebName + "*" + dddebaddressval + "*" + ddddiDebCountry + "*" + ddddiCredName + "*" + ddcredaddressval + "*" + ddddiCredCountry + "*" + ddddiTypeOfPayment + "*" + ddddiDateOfSigning;
    }

    public void getpmtscheduleconddetails(Contract contract, TDate todayDate) {

        TStructure pmtschedprop = contract.getConditionForPropertyEffectiveDate("SCHEDULE", todayDate);
        AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord = new AaPrdDesPaymentScheduleRecord(pmtschedprop);

        int pmttypesize = aaPrdDesPaymentScheduleRecord.getPaymentType().size();

        String paymentFreq1 = "";
        String pmtmethod1 = "";
        String billtype1 = "";
        String deferperiod1 = "";
        String property1 = "";
        String startdte1 = "";

        String paymentFreq2 = "";
        String pmtmethod2 = "";
        String billtype2 = "";
        String deferperiod2 = "";
        String property2 = "";
        String startdte2 = "";

        for (int i = 0; i < pmttypesize; i++) {
            String paymentType = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentType().getValue();
            if ("COMMITFEES".equals(paymentType)) {
                pmtmethod1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentMethod().getValue();
                paymentFreq1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentFreq().getValue();
                billtype1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getBillType().getValue();
                deferperiod1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getDeferPeriod().getValue();

                int propsize = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty().size();
                for (int j = 0; j < propsize; j++) {
                    property1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty(j).getProperty().getValue();
                    startdte1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(j).getStartDate().getValue();
                }
                getcommitfeerate(contract, todayDate);
  
                myList8 = paymentType + "*" + pmtmethod1 + "*" + paymentFreq1 + "*" + startdte1 + "*" + billtype1 + "*" + deferperiod1 + "*" + property1;
            }

            if ("FACILITYFEE".equals(paymentType)) {
                pmtmethod2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentMethod().getValue();
                paymentFreq2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentFreq().getValue();
                billtype2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getBillType().getValue();
                deferperiod2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getDeferPeriod().getValue();
                int propsize1 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty().size();

                for (int j = 0; j < propsize1; j++) {
                    property2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty(j).getProperty().getValue();
                    startdte2 = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(j).getStartDate().getValue();
                }
                getfacfeerate(contract, todayDate);
           
                myList9 = paymentType + "*" + pmtmethod2 + "*" + paymentFreq2 + "*" + startdte2 + "*" + billtype2 + "*" + deferperiod2 + "*" + property2;
            }
        }

    }

    public void getcommitfeerate(Contract contract, TDate todayDate) {

        List < AaPrdDesInterestRecord > aaPrdDesInterestRecord = contract.getInterestConditionForEffectiveDate("CURCOMMITFEE", todayDate);
        String effectiveRate = "";
        effectiveRate = getcommEffectiveRate(aaPrdDesInterestRecord);
      
        myList10 = effectiveRate;
    }

    public String getcommEffectiveRate(List < AaPrdDesInterestRecord > aaPrdDesInterestRecord) {
        String result = "";
        for (FixedRateClass aaPrdDesInterestRec: aaPrdDesInterestRecord.get(0).getFixedRate()) {
            result = aaPrdDesInterestRec.getEffectiveRate().getValue();
        }
        return result;
    }


    public void getfacfeerate(Contract contract, TDate todayDate) {

        List<AaPrdDesInterestRecord> aaPrdDesInterestRecord = contract.getInterestConditionForEffectiveDate("FACILITYFEE", todayDate);
        String faceffectiveRate = "";
        faceffectiveRate = getFacEffectiveRate(aaPrdDesInterestRecord);
     
        myList11 = faceffectiveRate;

    }

    public String getFacEffectiveRate(List < AaPrdDesInterestRecord > aaPrdDesInterestRecord) {
        String result1 = "";
        for (FixedRateClass aaPrdDesInterestRec: aaPrdDesInterestRecord.get(0).getFixedRate()) {
            result1 = aaPrdDesInterestRec.getEffectiveRate().getValue();
        }
        return result1;
    }

    public void getecbbalances(String aaStartDate, Contract contract, TDate todayDate) {

        TDate startDate = new TDate(aaStartDate);
        String val = "VALUE";

        List <BalanceMovement> totCommitment = contract.getContractBalanceMovementsForPeriod("TOTFACOMMITMENT", val,
            startDate, todayDate);
        double totComitment = totCommitment.get(0).getBalance().doubleValue();
        String totComitmentt = String.format("%.2f", totComitment);

        List <BalanceMovement> curCommlist = contract.getContractBalanceMovementsForPeriod("CURFACOMMITMENT", val,
            startDate, todayDate);
        double curCommitment = curCommlist.get(0).getBalance().doubleValue();
        String curCommitmentt = String.format("%.2f", curCommitment);

        List <BalanceMovement> utlcommlist = contract.getContractBalanceMovementsForPeriod("UTLFACOMMITMENT", val,
            startDate, todayDate);
        double utlCommitment = utlcommlist.get(0).getBalance().doubleValue();
        String utlCommitmentt = String.format("%.2f", utlCommitment);

        List <BalanceMovement> acccomfeelist = contract.getContractBalanceMovementsForPeriod("ACCCURCOMMITMENTFEE", 
                val,startDate, todayDate);
        double accCommitfee = acccomfeelist.get(0).getBalance().doubleValue();
        String accCommitfeet = String.format("%.2f", accCommitfee);

        myList12 = totComitmentt + "*" + curCommitmentt + "*" + utlCommitmentt + "*" + accCommitfeet;
    }
}