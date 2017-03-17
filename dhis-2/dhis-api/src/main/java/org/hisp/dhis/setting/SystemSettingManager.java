package org.hisp.dhis.setting;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.calendar.CalendarService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

/**
 * @author Stian Strandli
 */
public interface SystemSettingManager
{
    String ID = SystemSettingManager.class.getName();

    String KEY_APPLICATION_TITLE = "applicationTitle";
    String KEY_APPLICATION_INTRO = "keyApplicationIntro";
    String KEY_APPLICATION_NOTIFICATION = "keyApplicationNotification";
    String KEY_APPLICATION_FOOTER = "keyApplicationFooter";
    String KEY_APPLICATION_RIGHT_FOOTER = "keyApplicationRightFooter";
    String KEY_FLAG = "keyFlag";
    String KEY_FLAG_IMAGE = "keyFlagImage";
    String KEY_START_MODULE = "startModule";
    String KEY_FORUM_INTEGRATION = "forumIntegration";
    String KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART = "omitIndicatorsZeroNumeratorDataMart";
    String KEY_REPORT_TEMPLATE_DIRECTORY = "reportTemplateDirectory";
    String KEY_GOOGLE_MAPS_API_KEY = "googleMapsAPIKey";
    String KEY_FACTOR_OF_DEVIATION = "factorDeviation";
    String KEY_TRACKED_ENTITY_EXCEL_TEMPLATE_FILE_NAME = "trackedEntityExcelTemplateFileName";
    String KEY_DATAMART_TASK = "keyDataMartTask";
    String KEY_DATASETCOMPLETENESS_TASK = "keyDataSetCompletenessTask";
    String KEY_EMAIL_HOST_NAME = "keyEmailHostName";
    String KEY_EMAIL_PORT = "keyEmailPort";
    String KEY_EMAIL_USERNAME = "keyEmailUsername";
    String KEY_EMAIL_PASSWORD = "keyEmailPassword";
    String KEY_EMAIL_TLS = "keyEmailTls";
    String KEY_EMAIL_SENDER = "keyEmailSender";
    String KEY_INSTANCE_BASE_URL = "keyInstanceBaseUrl";
    String KEY_SCHEDULED_PERIOD_TYPES = "keyScheduledPeriodTypes";
    String KEY_SCHEDULED_TASKS = "keySchedTasks";
    String KEY_ORGUNITGROUPSET_AGG_LEVEL = "orgUnitGroupSetAggregationLevel";
    String KEY_SMS_CONFIG = "SMS_CONFIG";
    String KEY_CACHE_STRATEGY = "keyCacheStrategy";
    String KEY_TIME_FOR_SENDING_MESSAGE = "timeSendingMessage";
    String KEY_SEND_MESSAGE_SCHEDULED_TASKS = "sendMessageScheduled";
    String KEY_SCHEDULE_MESSAGE_TASKS = "scheduleMessage";
    String KEY_PHONE_NUMBER_AREA_CODE = "phoneNumberAreaCode";
    String KEY_MULTI_ORGANISATION_UNIT_FORMS = "multiOrganisationUnitForms";
    String KEY_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASKS = "scheduleAggregateQueryBuilder";
    String KEY_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY = "scheduleAggregateQueryBuilderTackStrategy";
    String KEY_CONFIGURATION = "keyConfig";
    String KEY_ACCOUNT_RECOVERY = "keyAccountRecovery";
    String KEY_LAST_MONITORING_RUN = "keyLastMonitoringRun";
    String KEY_GOOGLE_ANALYTICS_UA = "googleAnalyticsUA";
    String KEY_CREDENTIALS_EXPIRES = "credentialsExpires";
    String KEY_SELF_REGISTRATION_NO_RECAPTCHA = "keySelfRegistrationNoRecaptcha";
    String KEY_OPENID_PROVIDER = "keyOpenIdProvider";
    String KEY_OPENID_PROVIDER_LABEL = "keyOpenIdProviderLabel";
    String KEY_CAN_GRANT_OWN_USER_AUTHORITY_GROUPS = "keyCanGrantOwnUserAuthorityGroups";
    String KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS = "keyHideUnapprovedDataInAnalytics";
    String KEY_ANALYTICS_MAX_LIMIT = "keyAnalyticsMaxLimit";
    String KEY_CUSTOM_LOGIN_PAGE_LOGO = "keyCustomLoginPageLogo";
    String KEY_CUSTOM_TOP_MENU_LOGO = "keyCustomTopMenuLogo";
    String KEY_ANALYTICS_MAINTENANCE_MODE = "keyAnalyticsMaintenanceMode";
    String KEY_DATABASE_SERVER_CPUS = "keyDatabaseServerCpus";
    String KEY_LAST_SUCCESSFUL_DATA_SYNC = "keyLastSuccessfulDataSynch";
    String KEY_LAST_SUCCESSFUL_ANALYTICS_TABLES_UPDATE = "keyLastSuccessfulAnalyticsTablesUpdate";
    String KEY_LAST_SUCCESSFUL_ANALYTICS_TABLES_RUNTIME = "keyLastSuccessfulAnalyticsTablesRuntime";
    String KEY_LAST_SUCCESSFUL_RESOURCE_TABLES_UPDATE = "keyLastSuccessfulResourceTablesUpdate";
    String KEY_HELP_PAGE_LINK = "helpPageLink";
    String KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL = "keyAcceptanceRequiredForApproval";
    String KEY_SYSTEM_NOTIFICATIONS_EMAIL = "keySystemNotificationsEmail";
    String KEY_ANALYSIS_RELATIVE_PERIOD = "keyAnalysisRelativePeriod";
    String KEY_CORS_WHITELIST = "keyCorsWhitelist";
    String KEY_REQUIRE_ADD_TO_VIEW = "keyRequireAddToView";
    String KEY_ALLOW_OBJECT_ASSIGNMENT = "keyAllowObjectAssignment";

    String DEFAULT_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY = "lastMonth";
    String DEFAULT_FLAG = "dhis2";
    int DEFAULT_MAX_NUMBER_OF_ATTEMPTS = 20;
    int DEFAULT_TIMEFRAME_MINUTES = 1;
    double DEFAULT_FACTOR_OF_DEVIATION = 2.0;
    int DEFAULT_ORGUNITGROUPSET_AGG_LEVEL = 3;
    String DEFAULT_GOOGLE_MAPS_API_KEY = "ABQIAAAAut6AhySExnYIXm5s2OFIkxRKNzJ-_9njnryRTbvC6CtrS4sRvRREWnxwlZUa630pLuPf3nD9i4fq9w";
    String DEFAULT_START_MODULE = "dhis-web-dashboard-integration";
    String DEFAULT_APPLICATION_TITLE = "District Health Information Software 2";
    int DEFAULT_EMAIL_PORT = 587;
    int DEFAULT_COMPLETENESS_OFFSET = 15;
    String DEFAULT_TIME_FOR_SENDING_MESSAGE = "08:00";
    String DEFAULT_CACHE_STRATEGY = "CACHE_6AM_TOMORROW";
    int DEFAULT_ANALYTICS_MAX_LIMIT = 50000;
    int DEFAULT_DATABASE_SERVER_CPUS = 0; // Detect automatically
    String DEFAULT_HELP_PAGE_LINK = "../dhis-web-commons-about/help.action";
    String DEFAULT_ANALYSIS_RELATIVE_PERIOD = "LAST_12_MONTHS";
	
    final Map<String, Serializable> DEFAULT_SETTINGS_VALUES = new HashMap<String, Serializable>()
    {
        {
            put( KEY_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY, DEFAULT_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY );
            put( KEY_FLAG, DEFAULT_FLAG );
            put( CalendarService.KEY_CALENDAR, CalendarService.DEFAULT_CALENDAR );
            put( CalendarService.KEY_DATE_FORMAT, CalendarService.DEFAULT_DATE_FORMAT );
            put( KEY_FACTOR_OF_DEVIATION, DEFAULT_FACTOR_OF_DEVIATION );
            put( KEY_ORGUNITGROUPSET_AGG_LEVEL, DEFAULT_ORGUNITGROUPSET_AGG_LEVEL );
            put( KEY_START_MODULE, DEFAULT_START_MODULE );
            put( KEY_APPLICATION_TITLE, DEFAULT_APPLICATION_TITLE );
            put( KEY_EMAIL_PORT, DEFAULT_EMAIL_PORT );
            put( KEY_TIME_FOR_SENDING_MESSAGE, DEFAULT_TIME_FOR_SENDING_MESSAGE );
            put( KEY_CACHE_STRATEGY, DEFAULT_CACHE_STRATEGY );
            put( KEY_ANALYTICS_MAX_LIMIT, DEFAULT_ANALYTICS_MAX_LIMIT );
            put( KEY_ANALYSIS_RELATIVE_PERIOD, DEFAULT_ANALYSIS_RELATIVE_PERIOD );
        }
    };

    final String SYSPROP_PORTAL = "runningAsPortal";

    final HashSet<String> DEFAULT_SCHEDULED_PERIOD_TYPES = new HashSet<String>()
    {
        {
            add( MonthlyPeriodType.NAME );
            add( QuarterlyPeriodType.NAME );
            add( YearlyPeriodType.NAME );
        }
    };

    void saveSystemSetting( String name, Serializable value );

    Serializable getSystemSetting( String name );

    Serializable getSystemSetting( String name, Serializable defaultValue );

    List<SystemSetting> getAllSystemSettings();

    void deleteSystemSetting( String name );

    List<String> getFlags();

    String getFlagImage();

    String getEmailHostName();

    int getEmailPort();

    String getEmailUsername();

    boolean getEmailTls();
    
    String getEmailSender();

    String getInstanceBaseUrl();

    boolean accountRecoveryEnabled();

    boolean selfRegistrationNoRecaptcha();

    boolean emailEnabled();
    
    boolean systemNotificationEmailValid();

    boolean hideUnapprovedDataInAnalytics();
    
    String googleAnalyticsUA();

    Integer credentialsExpires();

    List<String> getCorsWhitelist();

    Map<String, Serializable> getSystemSettingsAsMap();
    
    Map<String, Serializable> getSystemSettings( Set<String> names );
}
