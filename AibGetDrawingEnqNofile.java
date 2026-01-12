package com.temenos.aibl3.nofile.enq;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaprddesinterest.AaPrdDesInterestRecord;
import com.temenos.t24.api.records.aaprddesinterest.FixedRateClass;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddespaymentschedule.AaPrdDesPaymentScheduleRecord;
import com.temenos.t24.api.records.aascheduledactivity.AaScheduledActivityRecord;
import com.temenos.t24.api.records.accountstatement.AccountStatementRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * description: java class for NOFILE ENQUIRY return the drawings details
 * 
 * ENQUIRY>AIB.GETMIG.DRAWINGDETS
 * STANDARD.SELECTION>NOFILE.AIB.AA.GET.MIGDRAWINGDTLS
 * EB.API>AIB.AA.GE.MIGDRAWINGDTLS
 * 
 * @author 90276
 * @param <arrangement id>
 *
 */

public class AibGetDrawingEnqNofile extends Enquiry {
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
        String aastdate = "";
        String frequency = "";
        getArrangementRecord(arrId,aastdate,frequency);
        getAccounrcondrecord(contract);
        getprinintrate(contract, todayDate);
        getcofintrate(contract, todayDate);
        getpenalrate(contract, todayDate);
        getpmtscheduleconddetails(contract, todayDate);
        getOfficerrecord(contract, todayDate);
        geTermAmtDetails(contract,todayDate);
        getschedactdtls(arrId);
        getecbbalances(aastdate, contract, todayDate);
        
        myList.add(myList1 + "*"+ myList2+"*"+myList3 + "*"+ myList4+"*"+myList5 + "*"+ myList6+"*"+myList7 + "*"+ myList8+"*"+myList9 + "*"+ myList10+"*"+myList11+ "*" +myList12);
        
        return myList;

    }

    public void getArrangementRecord(String arrId,String aastdate,String frequency) {

        AaArrangementRecord aaArrangementRecord = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT", arrId));

        String accountID = aaArrangementRecord.getLinkedAppl(0).getLinkedApplId().getValue(); 
        String customerNum = aaArrangementRecord.getCustomer(0).getCustomer().getValue();
        String masteraaid = aaArrangementRecord.getMasterArrangement().getValue();
        String aaproduct = aaArrangementRecord.getProduct(0).getProduct().toString();
        String aaccy = aaArrangementRecord.getCurrency().toString();
        String aaorigcontdate = aaArrangementRecord.getOrigContractDate().getValue();
       // use this to calculate balance
        aastdate = aaArrangementRecord.getStartDate().toString();
        
        getacctstmt(accountID,frequency);
      
        myList1 = arrId + "*" + customerNum + "*" + masteraaid + "*" + aaproduct + "*" + aaccy + "*" + aaorigcontdate + "*"+accountID;
    }
    
    public void getAccounrcondrecord(Contract contract) {

        AaPrdDesAccountRecord aaPrdDesAccountRecord = contract.getAccountCondition("ACCOUNT");

        String altid1 = aaPrdDesAccountRecord.getAltIdType(0).getAltId().getValue();
        String altid4 = aaPrdDesAccountRecord.getAltIdType(3).getAltId().getValue();

        String busdaycentr1 = "";
        String busdaycentr2 = "";
        
        if (aaPrdDesAccountRecord.getBusDayCentres()!= null) {
            for (int i = 0; i < aaPrdDesAccountRecord.getBusDayCentres().size(); i++) {
                if (i==0) {
                    busdaycentr1 = aaPrdDesAccountRecord.getBusDayCentres(i).getValue();
                } else 
                {
                    busdaycentr2 = aaPrdDesAccountRecord.getBusDayCentres(i).getValue();
                }
            }
        }
        
        myList2 = altid1 + "*" + altid4 + "*" + busdaycentr1 + "*" + busdaycentr2;
    }
    
    public void getprinintrate(Contract contract, TDate todayDate) {
        List<AaPrdDesInterestRecord> aaPrdDesInterestRecord = contract.getInterestConditionForEffectiveDate("PRINCIPALINT", todayDate);
        getprineffRate(aaPrdDesInterestRecord);  
    }

    public void getprineffRate(List<AaPrdDesInterestRecord> aaPrdDesInterestRecord) {
        String prinfixrte = "";
        String prinperiodindex = "";
        String prinperiodreset = "";
        String prinmarginrte = "";
        for (FixedRateClass aaPrdDesInterestRec: aaPrdDesInterestRecord.get(0).getFixedRate()) {
            
            prinfixrte = aaPrdDesInterestRec.getFixedRate().getValue();
            prinperiodindex = aaPrdDesInterestRec.getPeriodicIndex().getValue();
            prinperiodreset = aaPrdDesInterestRec.getPeriodicReset().getValue();
            int prinmgnsize = aaPrdDesInterestRec.getMarginType().size();
            for (int i=0 ; i<prinmgnsize ; i++) {
                prinmarginrte = aaPrdDesInterestRec.getMarginType(i).getMarginRate().getValue();
            }
        }
      
        myList3 = prinfixrte + "*"+prinperiodindex+"*"+prinperiodreset + "*"+prinmarginrte;
    }

    public void getcofintrate(Contract contract, TDate todayDate) {
        List<AaPrdDesInterestRecord> aaPrdDesInterestRecord1 = contract.getInterestConditionForEffectiveDate("COFINTEREST", todayDate);
        getcofinteffRate(aaPrdDesInterestRecord1);  
    }

    public void getcofinteffRate(List<AaPrdDesInterestRecord> aaPrdDesInterestRecord1) {
        String coffixrte = "";
        String cofperiodindex = "";
        String cofperiodreset = "";
        String cofmarginrte = "";
        for (FixedRateClass aaPrdDesInterestRec1: aaPrdDesInterestRecord1.get(0).getFixedRate()) {
            
            coffixrte = aaPrdDesInterestRec1.getFixedRate().getValue();
            cofperiodindex = aaPrdDesInterestRec1.getPeriodicIndex().getValue();
            cofperiodreset = aaPrdDesInterestRec1.getPeriodicReset().getValue();
            
            int cofmgnsize = aaPrdDesInterestRec1.getMarginType().size();
            for (int i=0 ; i<cofmgnsize ; i++) {
                cofmarginrte = aaPrdDesInterestRec1.getMarginType(i).getMarginRate().getValue();
            }
        }
        
        myList4 = coffixrte + "*"+cofperiodindex+"*"+cofperiodreset + "*"+cofmarginrte;
    }

    public void getpenalrate(Contract contract, TDate todayDate) {
        List<AaPrdDesInterestRecord> aaPrdDesInterestRecord2 = contract.getInterestConditionForEffectiveDate("PENALINTEREST", todayDate);
        String penalfixedrate = "";
        penalfixedrate = getpenaleffectiveRate(aaPrdDesInterestRecord2);
        myList5 = penalfixedrate;
    }

    public String getpenaleffectiveRate(List<AaPrdDesInterestRecord> aaPrdDesInterestRecord2) {
        String result1 = "";
        for (FixedRateClass aaPrdDesInterestRec2: aaPrdDesInterestRecord2.get(0).getFixedRate()) {
            result1 = aaPrdDesInterestRec2.getFixedRate().getValue();
        }
        return result1;
    }
    
    public void getpmtscheduleconddetails(Contract contract, TDate todayDate) {

        TStructure pmtschedprop = contract.getConditionForPropertyEffectiveDate("SCHEDULE", todayDate);
        AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord = new AaPrdDesPaymentScheduleRecord(pmtschedprop);

        int pmttypesize = aaPrdDesPaymentScheduleRecord.getPaymentType().size();
        getpmtschedtls(pmttypesize,aaPrdDesPaymentScheduleRecord);
    }
    

    public void getpmtschedtls(int pmttypesize, AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord) {
        String pmtypeval = "";        
        String pmtmethodval = "";        
        String pmtfreqval = "";
        String pmtpropval = "";
        String pmtduefreqval = "";
        String pmtbilltypeval = "";
        String pmtbillprodval = "";
        String pmtstdateval = "";
        String pmtenddteval = "";
        String pmtcalcamtval = "";
        String pmtactamtval = "";
 
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        StringBuilder sb4 = new StringBuilder();
        StringBuilder sb5 = new StringBuilder();
        StringBuilder sb6 = new StringBuilder();
        StringBuilder sb7 = new StringBuilder();
        StringBuilder sb8 = new StringBuilder();
        StringBuilder sb9 = new StringBuilder();
        StringBuilder sb10 = new StringBuilder();
 
        if (pmttypesize > 0) {
           for (int i = 0; i < pmttypesize; i++) {
                sb.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentType().getValue());
                sb1.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentMethod().getValue());
                sb2.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPaymentFreq().getValue());
            
                getpropertydtls(i,aaPrdDesPaymentScheduleRecord,sb3,sb4);
    
                sb5.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getBillType().getValue());
                sb6.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getBillProduced().getValue());
            
                 getpercentdtls(i,aaPrdDesPaymentScheduleRecord,sb7,sb8,sb9,sb10);
  
                 if (i < pmttypesize - 1) {
                    sb.append(";");
                    sb1.append(";");
                    sb2.append(";");
                    sb3.append(";");
                    sb4.append(";");
                    sb5.append(";");
                    sb6.append(";");   
                    sb7.append(";");
                    sb8.append(";");
                    sb9.append(";");
                    sb10.append(";");
                  }
            }
        pmtypeval = sb.toString();
        pmtmethodval = sb1.toString();
        pmtfreqval = sb2.toString();
        pmtpropval = sb3.toString();
        pmtduefreqval = sb4.toString();
        pmtbilltypeval = sb5.toString();
        pmtbillprodval = sb6.toString();
        pmtstdateval = sb7.toString();
        pmtenddteval = sb8.toString();
        pmtcalcamtval = sb9.toString();
        pmtactamtval = sb10.toString();
       }
        myList6 = pmtypeval + "*" + pmtmethodval + "*"+pmtfreqval+"*"+pmtpropval+"*"+pmtduefreqval+"*"+pmtbilltypeval+"*"+pmtbillprodval+"*"+pmtstdateval+"*"+pmtenddteval+"*"+pmtcalcamtval+"*"+pmtactamtval;
    }
    
 
    public void getpercentdtls(int i, AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord, StringBuilder sb7,
            StringBuilder sb8, StringBuilder sb9, StringBuilder sb10) {
        
        int percentsize = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage().size();
        for (int k =0; k<percentsize; k++) {
            sb7.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(k).getStartDate().getValue());
            sb8.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(k).getEndDate().getValue());
            sb9.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(k).getCalcAmount().getValue());
            sb10.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getPercentage(k).getActualAmt().getValue());
            
            if (k < percentsize - 1) {
               
                sb7.append("/");
                sb8.append("/");
                sb9.append("/");
                sb10.append("/");
            }
        }
    }

    public void getpropertydtls(int i, AaPrdDesPaymentScheduleRecord aaPrdDesPaymentScheduleRecord, StringBuilder sb3,
            StringBuilder sb4) {
        
        int propsize = aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty().size();
        for (int j =0; j<propsize; j++) {
            sb3.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty(j).getProperty().getValue());
            sb4.append(aaPrdDesPaymentScheduleRecord.getPaymentType(i).getProperty(j).getDueFreq().getValue());
            if (j < propsize - 1) {
                sb3.append("/");
                sb4.append("/");
            }
        }
    }

    public void getOfficerrecord(Contract contract, TDate todayDate) {

        TStructure officerproprec = contract.getConditionForPropertyEffectiveDate("OFFICERS", todayDate);
        AaPrdDesOfficersRecord officerproprec1 = new AaPrdDesOfficersRecord(officerproprec);
        String primaryofficer = officerproprec1.getPrimaryOfficer().getValue();

        int officercnt = officerproprec1.getOtherOfficer().size();
       
        myList7 = primaryofficer;
        getofficervalue(officercnt, officerproprec1);
    }

    public void getofficervalue(int officercnt, AaPrdDesOfficersRecord Officerproprec1) {
        String finalofficerval= "";
        String finaloffroleval = "";

        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        if (officercnt > 0) {
            for (int i = 0; i < officercnt; i++) {
                sb.append(Officerproprec1.getOtherOfficer(i).getOtherOfficer().getValue());
                sb1.append(Officerproprec1.getOtherOfficer(i).getOfficerRole().getValue());

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
   
        myList8 = finalofficerval + "*" + finaloffroleval;
    }

    public void geTermAmtDetails(Contract contract,TDate todayDate) {

        TStructure termamtproperty = contract.getConditionForPropertyEffectiveDate("COMMITMENT", todayDate);
        AaArrTermAmountRecord aatermamtrec = new AaArrTermAmountRecord(termamtproperty);
        String commitmentamt = aatermamtrec.getAmount().getValue();
        String matdate = aatermamtrec.getMaturityDate().getValue();
       
        myList9 = commitmentamt + "*" + matdate;
    }
    
    public void getschedactdtls(String arrId) {
        AaScheduledActivityRecord aaScheduledActivityRecord = new AaScheduledActivityRecord(
                da.getRecord("AA.SCHEDULED.ACTIVITY", arrId));
        
        int activitySize = 0;
        activitySize = aaScheduledActivityRecord.getActivityName().size();
        String prinperiodicnxtresetdte = "";
        String cofperiodicnxtresetdte = "";
        
        for (int i = 0; i < activitySize; i++) {
            String activityNameData = aaScheduledActivityRecord.getActivityName().get(i).getActivityName().getValue();
            if (activityNameData.equals("LENDING-PERIODIC.RESET-PRINCIPALINT")) {
                prinperiodicnxtresetdte = aaScheduledActivityRecord.getActivityName().get(i).getNextDate().getValue();
            } else {
                if (activityNameData.equals("LENDING-PERIODIC.RESET-COFINTEREST")) {
                    cofperiodicnxtresetdte = aaScheduledActivityRecord.getActivityName().get(i).getNextDate().getValue();
                }
            }
        }
        myList10 =  prinperiodicnxtresetdte + "*" +cofperiodicnxtresetdte;
    }
    
    public void getacctstmt(String acctid,String stmtfqu1) {
        AccountStatementRecord accountStatementRecord = new AccountStatementRecord(
                da.getRecord("ACCOUNT.STATEMENT", acctid));
        stmtfqu1 = accountStatementRecord.getStmtFqu1(0).getValue();
        
        myList11 = stmtfqu1;
    }

    public void getecbbalances(String aaStartDate, Contract contract, TDate todayDate) {

        TDate startDate = new TDate(aaStartDate);
        String val = "VALUE";

        List <BalanceMovement> accintamtlist = contract.getContractBalanceMovementsForPeriod("ACCPRINCIPALINT", val,
            startDate, todayDate);
        double accintamt = accintamtlist.get(0).getBalance().doubleValue();
        String accintamtt = String.format("%.2f", accintamt);

        List <BalanceMovement> curballist = contract.getContractBalanceMovementsForPeriod("CURACCOUNT", val,
            startDate, todayDate);
        double curbalamt = curballist.get(0).getBalance().doubleValue();
        String curbalamtt = String.format("%.2f", curbalamt);

        List <BalanceMovement> totcommlist = contract.getContractBalanceMovementsForPeriod("TOTCOMMITMENT", val,
            startDate, todayDate);
        double totcommtamt = totcommlist.get(0).getBalance().doubleValue();
        String totcommtamtt = String.format("%.2f", totcommtamt);

        List <BalanceMovement> utlcommlist = contract.getContractBalanceMovementsForPeriod("UTLCOMMITMENT", 
                val,startDate, todayDate);
        double utlcommamt = utlcommlist.get(0).getBalance().doubleValue();
        String utlcommamtt = String.format("%.2f", utlcommamt);
        
        List <BalanceMovement> curcommlist = contract.getContractBalanceMovementsForPeriod("CURCOMMITMENT", 
                val,startDate, todayDate);
        double curcommbal = curcommlist.get(0).getBalance().doubleValue();
        String curcommbalt = String.format("%.2f", curcommbal);
        
        myList12 = accintamtt + "*" + curbalamtt + "*" + totcommtamtt + "*" + utlcommamtt + "*" + curcommbalt;
    }
}