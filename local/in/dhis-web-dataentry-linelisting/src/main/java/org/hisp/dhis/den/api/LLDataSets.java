package org.hisp.dhis.den.api;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class LLDataSets
{
    public static final String LL_BIRTHS = "Line listing Births";
    public static final String LL_DEATHS = "Line listing Deaths";
    public static final String LL_MATERNAL_DEATHS = "Line listing Maternal Deaths";
    public static final String LL_UU_IDSP_EVENTS = "Line listing Unusual IDSP events- FormS";
    public static final String LL_UU_IDSP_EVENTSP = "Line listing Unusual IDSP events- FormP";
    public static final String LL_DEATHS_IDSP = "Line listing Deaths IDSP";
    public static final String LL_IDSP_LAB = "Line Listing IDSP Lab";
    public static final String LL_COLD_CHAIN = "Line listing Cold Chain";
    
    public static final String LL_FAMILY_PLANING = "Line listing Family Planing";
    public static final String LL_YUKTI_STATUS = "Line listing Yukti Status";

    //----------------------------------------------------------------
    // LineListing Family Planing Data Element for Bihar
    //----------------------------------------------------------------
    
    

    // 7271 : LLFP- Name of the Hospital 7271
    public static final int LLFP_HOSPITAL_NAME = 7271;
    // 7272 :  LLFP- Contact No 7272
    public static final int LLFP_CONTACT_NO = 7272;
    // 7273 :  LLFP- Sterilisation done for Female 7273
    public static final int LLFP_STERILISATION_FEMALE = 7273;
    // 7274 :  LLFP- Sterilisation done for Male 7274
    public static final int LLFP_STERILISATION_MALE = 7274;
    // 7275 :  LLFP- No of Cases Paid for Female 7275
    public static final int LLFP_CASES_PAID_FEMALE = 7275;
    // 7276 :  LLFP- No of Cases Paid for Male 7276
    public static final int LLFP_CASES_PAID_MALE = 7276;
    // 7277 :  LLFP- Amount Paid 7277
    public static final int LLFP_AMOUNT_PAID = 7277;
    // 7278 :  LLFP- Amount Due 7278
    public static final int LLFP_AMOUNT_DUE = 7278;
    // 7279 :  LLFP- Remark 7279
    public static final int LLFP_REMARK = 7279;
    // 7287 : LLFP- Name of the Hospital 7314
    public static final int LLFP_ACCREDITATION_ID = 7314;
    
    //----------------------------------------------------------------
    // LineListing Yukti status ata Element for Bihar
    //----------------------------------------------------------------
    
    
    
    // 7280 : LLYS- Name of the Site 7280
    public static final int LLYS_SITE_NAME = 7280;
    // 7281 :  LLYS- Contact No 7281
    public static final int LLYS_CONTACT_NO = 7281;
    // 7282 :  LLYS- No of MTPs Performed 7282
    public static final int LLYS_MTP_PERFORMED = 7282;
    // 7283 :  LLYS- No of Accredited Cases Paid 7283
    public static final int LLYS_ACCREDITED_CASE_PAID = 7283;
    // 7284 :  LLYS- Amount Paid 7284
    public static final int LLYS_AMOUNT_PAID = 7284;
    // 7285 :  LLYS- Amount Due 7285
    public static final int LLYS_AMOUNT_DUE = 7285;
    // 7286 :  LLYS- Remark 7286
    public static final int LLYS_REMARK = 7286;
    // 7288 : LLFP- Name of the Hospital 7315
    public static final int LLYS_ACCREDITATION_ID = 7315;
    
    //----------------------------------------------------------------
    // LineListing Cold Chain
    //----------------------------------------------------------------
    public static final int LLCC_EQUIPMENT = 5786;
    public static final int LLCC_MACHINE = 5787;
    public static final int LLCC_MACHINE_WORKING = 5788;
    public static final int LLCC_BREAKDOWN_DATE = 5789;
    public static final int LLCC_INTIMATION_DATE = 5790;
    public static final int LLCC_REPAIR_DATE = 5791;
    public static final int LLCC_REMARKS = 5792;
    
    //----------------------------------------------------------------
    // LineListing IDSP LAB
    //----------------------------------------------------------------
    public static final int LLIDSPL_PATIENT_NAME = 1053;
    public static final int LLIDSPL_AGE = 1055;
    public static final int LLIDSPL_SEX = 1054;
    public static final int LLIDSPL_ADDRESS = 1056;
    public static final int LLIDSPL_TEST = 1057;
    public static final int LLIDSPL_LAB_DIAGNOSIS = 1058;
    public static final int LLIDSPL_OUTCOME = 3120;
    
    //----------------------------------------------------------------
    // LineListing Death IDSP
    //----------------------------------------------------------------
    public static final int LLDIDSP_CHILD_NAME = 1048;
    public static final int LLDIDSP_VILLAGE_NAME = 1049;
    public static final int LLDIDSP_SEX = 1050;
    public static final int LLDIDSP_AGE_CATEGORY = 1051;
    public static final int LLDIDSP_DEATH_CAUSE = 1052;

    //----------------------------------------------------------------
    // LineListing Unusual IDSP Event DataElements FORM-P
    //----------------------------------------------------------------    
    public static final int LLUUIDSPEP_EVENT_REPORTED = 1044;
    public static final int LLUUIDSPEP_DATE_OF_EVENT = 1045;    
    public static final int LLUUIDSPEP_WAS_INVESTIGATED = 1046;
    public static final int LLUUIDSPEP_ACTION_TAKEN = 1047;

    //----------------------------------------------------------------
    // LineListing Unusual IDSP Event DataElements
    //----------------------------------------------------------------
    public static final int LLUUIDSPE_SC_NAME = 1040;
    public static final int LLUUIDSPE_DATE_OF_EVENT = 1041;
    public static final int LLUUIDSPE_DEATAILS = 1042;
    public static final int LLUUIDSPE_WAS_INVESTIGATED = 1043;
    
    //----------------------------------------------------------------
    // LineListing Birth DataElements
    //----------------------------------------------------------------
    public static final int LLB_CHILD_NAME = 1020;
    public static final int LLB_VILLAGE_NAME = 1021;
    public static final int LLB_SEX = 1022;
    public static final int LLB_DOB = 1023;
    public static final int LLB_WIEGH = 1024;
    public static final int LLB_BREASTFED = 1025;
    
    public static final int LLB_BIRTHS = 22;
    public static final int LLB_BIRTHS_MALE = 24;
    public static final int LLB_BIRTHS_FEMALE = 23;
    public static final int LLB_WEIGHED_MALE = 515;
    public static final int LLB_WEIGHED_FEMALE = 516;
    public static final int LLB_WEIGHED_LESS1800_MALE = 519;
    public static final int LLB_WEIGHED_LESS1800_FEMALE = 520;
    public static final int LLB_WEIGHED_LESS2500_MALE = 517;
    public static final int LLB_WEIGHED_LESS2500_FEMALE = 518;
    public static final int LLB_BREASTFED_MALE = 521;
    public static final int LLB_BREASTFED_FEMALE = 522;
    
    //----------------------------------------------------------------
    // LineListing Death DataElements
    //----------------------------------------------------------------
    public static final int LLD_CHILD_NAME = 1027;
    public static final int LLD_VILLAGE_NAME = 1028;
    public static final int LLD_SEX = 1029;
    public static final int LLD_AGE_CATEGORY = 1030;
    public static final int LLD_DEATH_CAUSE = 1031;

    // production Maharashtra
    public static final int LLD_DEATH_AGE_TYPE = 6826;
    public static final int LLD_DEATH_AGE = 6827;
    
    // for Haryana production
    //public static final int LLD_DEATH_AGE_TYPE = 91478;
    //public static final int LLD_DEATH_AGE = 91479;
    
    public static final int LLD_DEATH_OVER05Y = 552;
    public static final int LLD_DEATH_OVER05Y_MALE = 553;
    public static final int LLD_DEATH_OVER05Y_FEMALE = 554;
    public static final int LLD_DEATH_OVER15Y_MALE = 1195;
    public static final int LLD_DEATH_OVER15Y_FEMALE = 1196;
    public static final int LLD_DEATH_OVER55Y_MALE = 1197;
    public static final int LLD_DEATH_OVER55Y_FEMALE = 1198;
    public static final int LLD_DEATH_BELOW5Y = 555;
    public static final int LLD_DEATH_BELOW5Y_MALE = 556;
    public static final int LLD_DEATH_BELOW5Y_FEMALE = 557;
    public static final int LLD_DEATH_BELOW1Y_MALE = 558;
    public static final int LLD_DEATH_BELOW1Y_FEMALE = 559;
    public static final int LLD_DEATH_BELOW1M_MALE = 560;
    public static final int LLD_DEATH_BELOW1M_FEMALE = 561;
    public static final int LLD_DEATH_BELOW1W_MALE = 562;
    public static final int LLD_DEATH_BELOW1W_FEMALE = 563;
    public static final int LLD_DEATH_BELOW1D_MALE = 564;
    public static final int LLD_DEATH_BELOW1D_FEMALE = 565;
            
    // 1121 : Birth Asphyxia under one  month
    public static final int LLD_CAUSE_DE1 = 1121;           
    // 1122 : Sepsis under one  month
    public static final int LLD_CAUSE_DE2 = 1122;      
    // 1123 : Low Birth Weight under one  month
    public static final int LLD_CAUSE_DE3 = 1123;
    // 1124 : Immunization reactions under one  month
    public static final int LLD_CAUSE_DE4 = 1124;
    // 1125 : Others under one  month
    public static final int LLD_CAUSE_DE5 = 1125;
    // 1126 : Not known under one  month
    public static final int LLD_CAUSE_DE6 = 1126;
    // 1127 : Pneumonia 1 month to 5 year
    public static final int LLD_CAUSE_DE7 = 1127;
    // 1128 : Diarrhoeal disease 1 month to 5 year
    public static final int LLD_CAUSE_DE8 = 1128;
    // 1129 : Measles 1 month to 5 year
    public static final int LLD_CAUSE_DE9 = 1129;
    // 1130 : Other Fever related 1 month to 5 year
    public static final int LLD_CAUSE_DE10 = 1130;
    // 1131 : Others 1 month to 5 year
    public static final int LLD_CAUSE_DE11 = 1131;
    // 1132 : Not known 1 month to 5 year
    public static final int LLD_CAUSE_DE12 = 1132;
       
    // 1133 : Diarrhoeal disease 5-14 years
    public static final int LLD_CAUSE_DE13 = 1133;
    // 1134 : Tuberculosis 5-14 years
    public static final int LLD_CAUSE_DE14 = 1134;
    // 1135 : Malaria 5-14 years
    public static final int LLD_CAUSE_DE15 = 1135;
    // 1136 : HIV/AIDS 5-14 years
    public static final int LLD_CAUSE_DE16 = 1136;
    // 1137 : Other Fever related 5-14 years
    public static final int LLD_CAUSE_DE17 = 1137;
    // 1138 : Pregnancy related death( maternal mortality) 15-55 years
    public static final int LLD_CAUSE_DE18 = 1138;
    // 1139 : Sterilisation related deaths 15-55 years
    public static final int LLD_CAUSE_DE19 = 1139;
    // 1140 : Accidents or injuries 5-14 years
    public static final int LLD_CAUSE_DE20 = 1140;
    // 1141 : Suicides 5-14 years
    public static final int LLD_CAUSE_DE21 = 1141;
    // 1142 : Animal Bites or stings 5-14 years
    public static final int LLD_CAUSE_DE22 = 1142;
    // 1143 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) 5-14 years
    public static final int LLD_CAUSE_DE23 = 1143;
    // 1144 : Other known Chronic disease( sick for more than 3 weeks, no fever) 5-14 years
    public static final int LLD_CAUSE_DE24 = 1144;
    // 1145 : Cause Not Known, 5-14 years
    public static final int LLD_CAUSE_DE25 = 1145;
    // 1146 : Respiratory Infections and Disease – other than tuberculosis 5-14 years
    public static final int LLD_CAUSE_DE26 = 1146;
    // 1147 : Heart disease and hypertension 5-14 years
    public static final int LLD_CAUSE_DE27 = 1147;
    // 1148 : Stroke and Neurological disease 5-14 years
    public static final int LLD_CAUSE_DE28 = 1148;
    
    // 1199 : Malaria 15-55 years
    public static final int LLD_CAUSE_DE29 = 1199;
    // 1200 : Malaria Over 55 years
    public static final int LLD_CAUSE_DE30 = 1200;
    // 1201 : Tuberculosis 15-55 years
    public static final int LLD_CAUSE_DE31 = 1201;
    // 1202 : Tuberculosis Over 55 years
    public static final int LLD_CAUSE_DE32 = 1202;
    // 1203 : Malaria Below 5 years
    public static final int LLD_CAUSE_DE33 = 1203;
    // 1204 : Tuberculosis Below 5 years
    public static final int LLD_CAUSE_DE34 = 1204;
   
    // 1205 : Diarrhoeal disease 15-55 years
    public static final int LLD_CAUSE_DE35 = 1205;
    // 1206 : HIV/AIDS 15-55 years
    public static final int LLD_CAUSE_DE36 = 1206;
    // 1207 : Other Fever related 15-55 years
    public static final int LLD_CAUSE_DE37 = 1207;
    // 1208 : Accidents or injuries 15-55 years
    public static final int LLD_CAUSE_DE40 = 1208;
    // 1209 : Suicides 15-55 years
    public static final int LLD_CAUSE_DE41 = 1209;
    // 1210 : Animal Bites or stings 15-55 years
    public static final int LLD_CAUSE_DE42 = 1210;
    // 1211 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) 15-55 years
    public static final int LLD_CAUSE_DE43 = 1211;
    // 1212 : Other known Chronic disease( sick for more than 3 weeks, no fever) 15-55 years
    public static final int LLD_CAUSE_DE44 = 1212;
    // 1213 : Cause Not Known, 15-55 years
    public static final int LLD_CAUSE_DE45 = 1213;
    // 1214 : Respiratory Infections and Disease – other than tuberculosis 15-55 years
    public static final int LLD_CAUSE_DE46 = 1214;
    // 1215 : Heart disease and hypertension 15-55 years
    public static final int LLD_CAUSE_DE47 = 1215;
    // 1216 : Stroke and Neurological disease 15-55 years
    public static final int LLD_CAUSE_DE48 = 1216;
    
    // 1217 : Diarrhoeal disease over 55 years
    public static final int LLD_CAUSE_DE49 = 1217;
    // 1218 : HIV/AIDS over 55 years
    public static final int LLD_CAUSE_DE50 = 1218;
    // 1219 : Other Fever related over 55 years
    public static final int LLD_CAUSE_DE51 = 1219;
    // 1220 : Accidents or injuries over 55 years
    public static final int LLD_CAUSE_DE54 = 1220;
    // 1221 : Suicides over 55 years
    public static final int LLD_CAUSE_DE55 = 1221;
    // 1222 : Animal Bites or stings over 55 years
    public static final int LLD_CAUSE_DE56 = 1222;
    // 1223 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) over 55 years
    public static final int LLD_CAUSE_DE57 = 1223;
    // 1224 : Other known Chronic disease( sick for more than 3 weeks, no fever) over 55 years
    public static final int LLD_CAUSE_DE58 = 1224;
    // 1225 : Cause Not Known, over 55 years
    public static final int LLD_CAUSE_DE59 = 1225;
    // 1226 : Respiratory Infections and Disease – other than tuberculosis over 55 years
    public static final int LLD_CAUSE_DE60 = 1226;
    // 1227 : Heart disease and hypertension over 55 years
    public static final int LLD_CAUSE_DE61 = 1227;
    // 1228 : Stroke and Neurological disease over 55 years
    public static final int LLD_CAUSE_DE62 = 1228;

    // 1229 : Immunization reactions 1 month to 5 years
    public static final int LLD_CAUSE_DE63 = 1229;
        
    // 1230 : Birth Asphyxia under one  day
    public static final int LLD_CAUSE_DE64 = 1230;           
    // 1231 : Sepsis under one  day
    public static final int LLD_CAUSE_DE65 = 1231;      
    // 1232 : Low Birth Weight under one  day
    public static final int LLD_CAUSE_DE66 = 1232;
    // 1233 : Immunization reactions under one  day
    public static final int LLD_CAUSE_DE67 = 1233;
    // 1234 : Others under one  day
    public static final int LLD_CAUSE_DE68 = 1234;
    // 1235 : Not known under one  day
    public static final int LLD_CAUSE_DE69 = 1235;
   
    // 1236 : Birth Asphyxia under one  week
    public static final int LLD_CAUSE_DE70 = 1236;           
    // 1237 : Sepsis under one  week
    public static final int LLD_CAUSE_DE71 = 1237;      
    // 1238 : Low Birth Weight under one  week
    public static final int LLD_CAUSE_DE72 = 1238;
    // 1239 : Immunization reactions under one  week
    public static final int LLD_CAUSE_DE73 = 1239;
    // 1240 : Others under one  week
    public static final int LLD_CAUSE_DE74 = 1240;
    // 1241 : Not known under one  week
    public static final int LLD_CAUSE_DE75 = 1241;
   
    // 1242 : Pneumonia 1 month to 1 year
    public static final int LLD_CAUSE_DE76 = 1242;
    // 1243 : Diarrhoeal disease 1 month to 1 year
    public static final int LLD_CAUSE_DE77 = 1243;
    // 1244 : Measles 1 month to 5 year
    public static final int LLD_CAUSE_DE78 = 1244;
    // 1245 : Tuberculosis 1 month to 1 year
    public static final int LLD_CAUSE_DE79 = 1245;
    // 1246 : Malaria 1 month to 1 year
    public static final int LLD_CAUSE_DE80 = 1246;
    // 1247 : Immunization reactions 1 month to 1 year
    public static final int LLD_CAUSE_DE81 = 1247;
    // 1248 : Other Fever related  1 month to 1 year
    public static final int LLD_CAUSE_DE82 = 1248;
    // 1249 : Others 1 month to 1 year
    public static final int LLD_CAUSE_DE83 = 1249;
    // 1250 : Not known 1 month to 1 year
    public static final int LLD_CAUSE_DE84 = 1250;
    
    
    
    
    
    //for Haryana application cause of death
    
    //Pneumonia below 1 day
    public static final int LLD_CAUSE_DE85 = 6050;
    //Pneumonia  1 day to 1 week
    public static final int LLD_CAUSE_DE86 = 6051;
    //Pneumonia  1 week to 1 month
    public static final int LLD_CAUSE_DE87 = 6052;
    
    
    //Pneumonia  1 month to 1 year
        //public static final int LLD_CAUSE_DE88 = 0;
    //Pneumonia  1 year to 5 year
        //public static final int LLD_CAUSE_DE89 = 0;
    
    
    
    //Fever related below 1 day
    public static final int LLD_CAUSE_DE90 = 6053;
    //Fever related 1 day to 1 week
    public static final int LLD_CAUSE_DE91 = 6054;
    //Fever related 1 week to 1 month
    public static final int LLD_CAUSE_DE92 = 6055;
    
    //Fever related 1 month to 1 year
        //public static final int LLD_CAUSE_DE93 = 0;
    //Fever related 1 year to 5 year
        //public static final int LLD_CAUSE_DE94 = 0;
    
    //Meconium aspiration syndrome below 1 day
    public static final int LLD_CAUSE_DE95 = 6056;
    //Meconium aspiration syndrome 1 day to 1 week
    public static final int LLD_CAUSE_DE96 = 6057;
    //Meconium aspiration syndrome 1 week to 1 month
    public static final int LLD_CAUSE_DE97 = 6058;
    //Meconium aspiration syndrome 1 month to 1 year
    public static final int LLD_CAUSE_DE98 = 6059;
    //Meconium aspiration syndrome 1 year to 5 year
    public static final int LLD_CAUSE_DE99 = 6060;
    
    
    //Meningitis below 1 day
    public static final int LLD_CAUSE_DE100 = 6061;
    //Meningitis 1 day to 1 week
    public static final int LLD_CAUSE_DE101 = 6062;
    //Meningitis 1 week to 1 month
    public static final int LLD_CAUSE_DE102 = 6063;
    //Meningitis 1 month to 1 year
    public static final int LLD_CAUSE_DE103 = 6064;
    //Meningitis 1 year to 5 year
    public static final int LLD_CAUSE_DE104 = 6065;
    
    
    //Major Congenital Malformation below 1 day
    public static final int LLD_CAUSE_DE105 = 6066;
    //Major Congenital Malformation 1 day to 1 week
    public static final int LLD_CAUSE_DE106 = 6067;
    //Major Congenital Malformation 1 week to 1 month
    public static final int LLD_CAUSE_DE107 = 6068;
    //Major Congenital Malformation 1 month to 1 year
    public static final int LLD_CAUSE_DE108 = 6069;
    //Major Congenital Malformation 1 year to 5 year
    public static final int LLD_CAUSE_DE109 = 6070;
    
    
    
    // Prematurity below 1 day
    public static final int LLD_CAUSE_DE110 = 6071;
    //Prematurity 1 day to 1 week
    public static final int LLD_CAUSE_DE111 = 6072;
    //Prematurity 1 week to 1 month
    public static final int LLD_CAUSE_DE112 = 6073;
    //Prematurity1 month to 1 year
    public static final int LLD_CAUSE_DE113 = 6074;
    //Prematurity 1 year to 5 year
    public static final int LLD_CAUSE_DE114 = 6075;
    
    
    
    //Hypothermia below 1 day
    public static final int LLD_CAUSE_DE115 = 6076;
    //Hypothermia 1 day to 1 week
    public static final int LLD_CAUSE_DE116 = 6077;
    //Hypothermia 1 week to 1 month
    public static final int LLD_CAUSE_DE117 = 6078;
    //Hypothermia 1 month to 1 year
    public static final int LLD_CAUSE_DE118 = 6079;
    //Hypothermia 1 year to 5 year
    public static final int LLD_CAUSE_DE119 = 6080;
    
   
    //Diptheria below 1 day
    public static final int LLD_CAUSE_DE120 = 6081;
    //Diptheria 1 day to 1 week
    public static final int LLD_CAUSE_DE121 = 6082;
    //Diptheria 1 week to 1 month
    public static final int LLD_CAUSE_DE122 = 6083;
    //Diptheria 1 month to 1 year
    public static final int LLD_CAUSE_DE123 = 6084;
    //Diptheria 1 year to 5 year
    public static final int LLD_CAUSE_DE124 = 6085;
    
    
    //Childhood Tuberculosis below 1 day
    public static final int LLD_CAUSE_DE125 = 6086;
    //Childhood Tuberculosis 1 day to 1 week
    public static final int LLD_CAUSE_DE126 = 6087;
    //Childhood Tuberculosis 1 week to 1 month
    public static final int LLD_CAUSE_DE127 = 6088;
    //Childhood Tuberculosis 1 month to 1 year
    public static final int LLD_CAUSE_DE128 = 6089;
    //Childhood Tuberculosis 1 year to 5 year
    public static final int LLD_CAUSE_DE129 = 6090;
    
    
    //Dysentry below 1 day
    public static final int LLD_CAUSE_DE130 = 6091;
    //Dysentry 1 day to 1 week
    public static final int LLD_CAUSE_DE131 = 6092;
    //Dysentry 1 week to 1 month
    public static final int LLD_CAUSE_DE132 = 6093;
    //Dysentry 1 month to 1 year
    public static final int LLD_CAUSE_DE133 = 6094;
    //Dysentry 1 year to 5 year
    public static final int LLD_CAUSE_DE134 = 6095;
    
    
    //Pertusis below 1 day
    public static final int LLD_CAUSE_DE135 = 6096;
    //Pertusis 1 day to 1 week
    public static final int LLD_CAUSE_DE136 = 6097;
    //Pertusis 1 week to 1 month
    public static final int LLD_CAUSE_DE137 = 6098;
    //Pertusis 1 month to 1 year
    public static final int LLD_CAUSE_DE138 = 6099;
    //Pertusis 1 year to 5 year
    public static final int LLD_CAUSE_DE139 = 6100;
    
    
    //Polio below 1 day
    public static final int LLD_CAUSE_DE140 = 6101;
    //Polio 1 day to 1 week
    public static final int LLD_CAUSE_DE141 = 6102;
    //Polio 1 week to 1 month
    public static final int LLD_CAUSE_DE142 = 6103;
    //Polio 1 month to 1 year
    public static final int LLD_CAUSE_DE143 = 6104;
    //Polio 1 year to 5 year
    public static final int LLD_CAUSE_DE144 = 6105;
    
    
    //Tetanus Neonatorum below 1 day
    public static final int LLD_CAUSE_DE145 = 6106;
    //Tetanus Neonatorum 1 day to 1 week
    public static final int LLD_CAUSE_DE146 = 6107;
    //Tetanus Neonatorum 1 week to 1 month
    public static final int LLD_CAUSE_DE147 = 6108;
    //Tetanus Neonatorum 1 month to 1 year
    public static final int LLD_CAUSE_DE148 = 6109;
    //Tetanus Neonatorum 1 year to 5 year
    public static final int LLD_CAUSE_DE149 = 6110;
    
    
    //Tetanus (Others) below 1 day
    public static final int LLD_CAUSE_DE150 = 6111;
    //Tetanus (Others) 1 day to 1 week
    public static final int LLD_CAUSE_DE151 = 6112;
    //Tetanus (Others) 1 week to 1 month
    public static final int LLD_CAUSE_DE152 = 6113;
    //Tetanus (Others) 1 month to 1 year
    public static final int LLD_CAUSE_DE153 = 6114;
    //Tetanus (Others) 1 year to 5 year
    public static final int LLD_CAUSE_DE154 = 6115;
    
    
    //Acute Flaccide paralysis below 1 day
    public static final int LLD_CAUSE_DE155 = 6116;
    //Acute Flaccide paralysis 1 day to 1 week
    public static final int LLD_CAUSE_DE156 = 6117;
    //Acute Flaccide paralysis 1 week to 1 month
    public static final int LLD_CAUSE_DE157 = 6118;
    //Acute Flaccide paralysis 1 month to 1 year
    public static final int LLD_CAUSE_DE158 = 6119;
    //Acute Flaccide paralysis 1 year to 5 year
    public static final int LLD_CAUSE_DE159 = 6120;
    
    
    //Respiratory Infections (other than TB) below 1 day
    public static final int LLD_CAUSE_DE160 = 6121;
    //Respiratory Infections (other than TB) 1 day to 1 week
    public static final int LLD_CAUSE_DE161 = 6122;
    //Respiratory Infections (other than TB) 1 week to 1 month
    public static final int LLD_CAUSE_DE162 = 6123;
    //Respiratory Infections (other than TB) 1 month to 1 year
    public static final int LLD_CAUSE_DE163 = 6124;
    //Respiratory Infections (other than TB) 1 year to 5 year
    public static final int LLD_CAUSE_DE164 = 6125;
    
    
    
    
    public static final int LLD_OPTIONCOMBO_DEFAULT = 1;
    public static final int LLD_CAUSE_OPTIONCOMBO_MALE = 8;
    public static final int LLD_CAUSE_OPTIONCOMBO_FEMALE = 7;
    public static final String LLD_ASPHYXIA = "ASPHYXIA";
    public static final String LLD_SEPSIS = "SEPSIS";
    public static final String LLD_LOW_BIRTH_WEIGH = "LOWBIRTHWEIGH";   
    public static final String LLD_IMMREAC = "IMMREAC";
    public static final String LLD_PNEUMONIA = "PNEUMONIA";
    public static final String LLD_DIADIS = "DIADIS";
    public static final String LLD_MEASLES = "MEASLES";
    public static final String LLD_TUBER = "TUBER";
    public static final String LLD_MALARIA = "MALARIA";
    public static final String LLD_HIVAIDS = "HIVAIDS";
    public static final String LLD_OFR = "OFR";
    public static final String LLD_PRD = "PRD";
    public static final String LLD_SRD = "SRD";
    public static final String LLD_AI = "AI";
    public static final String LLD_SUICIDES = "SUICIDES";
    public static final String LLD_ABS = "ABS";
    public static final String LLD_RID = "RID";
    public static final String LLD_HDH = "HDH";
    public static final String LLD_SND = "SND";
    public static final String LLD_OKAD = "OKAD";
    public static final String LLD_OKCD = "OKCD";
    public static final String LLD_OTHERS = "OTHERS";                                          
    public static final String LLD_NOT_KNOWN = "NK";
    
    // for haryana application
    
    
    // Respiratory Infections (other than TB)
    public static final String LLD_RI_OTHER_THAN_TB = "RIOTHERTB";
    // Fever related
    public static final String LLD_FR = "FEVERRELATED"; 
    // Meconium aspiration syndrome
    public static final String LLD_MAS = "MAS";
    // Meningitis
    public static final String LLD_MENINGITIS = "MENINGITIS";
    // Major Congenital Malformation
    public static final String LLD_MCM = "MCM";
    // Prematurity
    public static final String LLD_PREMATURITY = "PREMATURITY";
    // Hypothermia
    public static final String LLD_HYPOTHERMIA = "HYPOTHERMIA";
    // Diptheria
    public static final String LLD_DIPTHERIA = "DIPTHERIA";
    // Childhood Tuberculosis
    public static final String LLD_CHILD_TUBERCULOSIS = "CHILDTUBERCULOSIS";
    //Dysentry
    public static final String LLD_DYSENTRY = "DYSENTRY";
    // Pertusis
    public static final String LLD_PERTUSIS = "PERTUSIS";
    // Polio
    public static final String LLD_POLIO = "POLIO";
    // Tetanus Neonatorum
    public static final String LLD_TETANUS_NEONATORUM = "TETANUSNEONATORUM";
    // Tetanus (Others)
    public static final String LLD_TETANUS_OTHERS = "TETANUSOTHERS";
    //Acute Flaccide paralysis
    public static final String LLD_AFP = "AFP";
   
    
    
    
    
    
    
    //-------------------------------------------------------------------
    // Line listing Maternal Death
    //-------------------------------------------------------------------
    public static final int LLMD_MOTHER_NAME = 1032;
    public static final int LLMD_VILLAGE_NAME = 1033;
    public static final int LLMD_AGE_AT_DEATH = 1034;
    public static final int LLMD_DURATION_OF_PREGNANCY = 1035;
    public static final int LLMD_DELIVERY_AT = 1036;
    public static final int LLMD_NATURE_OF_ASSISTANCE = 1037;
    public static final int LLMD_DEATH_CAUSE = 1038;
    public static final int LLMD_AUDITED = 1039;
    
    public static final int LLMD_DURING_PREGNANCY = 523;
    public static final int LLMD_DURING_FIRST_TRIM = 524;
    public static final int LLMD_DURING_SECOND_TRIM = 525;
    public static final int LLMD_DURING_THIRD_TRIM = 526;    
    public static final int LLMD_DURING_DELIVERY = 527;
    public static final int LLMD_AFTER_DEL_WITHIN_42DAYS = 528;
    
    public static final int LLMD_AGE_BELOW16 = 529;
    public static final int LLMD_AGE_16TO19 = 530;
    public static final int LLMD_AGE_19TO35 = 531;
    public static final int LLMD_AGE_ABOVE35 = 532;
    
    public static final int LLMD_AT_HOME = 533;
    public static final int LLMD_AT_SC = 534;
    public static final int LLMD_AT_PHC = 535;
    public static final int LLMD_AT_CHC = 536;
    public static final int LLMD_AT_MC = 537;
    public static final int LLMD_AT_PVTINST = 5726; // This data element should be created for state specific for line listing maternal death delivery at PVT INST for Punjab Application
    
    
    public static final int LLMD_BY_UNTRAINED = 538;
    public static final int LLMD_BY_TRAINED = 539;
    public static final int LLMD_BY_ANM = 540;
    public static final int LLMD_BY_NURSE = 541;
    public static final int LLMD_BY_DOCTOR = 542;
    
    public static final int LLMD_CAUSE_ABORTION = 543;
    public static final int LLMD_CAUSE_OPL = 544;
    public static final int LLMD_CAUSE_FITS = 545;
    public static final int LLMD_CAUSE_SH = 546;
    public static final int LLMD_CAUSE_BBCD = 547;
    public static final int LLMD_CAUSE_BACD = 548;
    public static final int LLMD_CAUSE_HFBD = 549;
    public static final int LLMD_CAUSE_HFAD = 550;
    public static final int LLMD_CAUSE_NK = 551;
    //public static final int LLMD_CAUSE_MDNK = 5725;// This data element should be created for state specific for line listing maternal death Other Causes (including cause not known) for Punjab Application
    //public static final int LLMD_CAUSE_MDNK = 5938;// This data element should be created for state specific for line listing maternal death Other Causes (including cause not known) for Orissa Application
    public static final int LLMD_CAUSE_MDNK = 6839;// This data element should be created for state specific for line listing maternal death Other Causes (including cause not known) for Maharashtra Application
    // for haryana application
    // Anaemia as cause of Direct/Associated with other Medical Disease
    public static final int LLMD_CAUSE_ANAEMIA = 6043;
    // Severe Anaemia (<7gm)
    public static final int LLMD_CAUSE_SEVERE_ANAEMIA = 6044; 
    // Moderate Anaemia (>7gm <11gm)
    public static final int LLMD_CAUSE_MODERATE_ANAEMIA = 6045;



}
