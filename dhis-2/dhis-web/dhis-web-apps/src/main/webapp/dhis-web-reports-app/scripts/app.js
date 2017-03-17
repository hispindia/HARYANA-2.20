/**
 * Created by harsh on 22/6/15.
 */


var reportsApp = angular.module('reportsApp',['ui.bootstrap',
    'ngRoute',
    'ngCookies',
    'ngSanitize',
    'ngMessages',
    'reportsAppServices',
    'reportsAppControllers',
    'reportsAppFilters',
    'd2Directives',
    'd2Filters',
    'd2Services',
    'd2Controllers',
    'angularLocalStorage',
    'ui.select2',
    'd2HeaderBar',
    'nvd3ChartDirectives',
    'pascalprecht.translate'
    ])

    .config(function( $routeProvider,$translateProvider) {


        $routeProvider.when('/', {
            templateUrl:'views/home.html',
            controller: 'HomeController'
        }).when('/report-configuration', {
            templateUrl:'components/configuration/configuration.html',
            controller: 'ConfigurationController'
        }).when('/program-management', {
            templateUrl:'components/programs/programs.html',
            controller: 'ProgramsController'
        }).when('/template-management', {
            templateUrl:'components/templates/templates.html',
            controller: 'TemplatesController'
        }).when('/reports', {
            templateUrl:'components/reports/reports.html',
            controller: 'ReportsController'
        }).when('/add-report', {
            templateUrl:'components/reports/addReportPage.html',
            controller: 'AddReportController'
        }).when('/edit-report', {
            templateUrl:'components/reports/editReportPage.html',
            controller: 'EditReportController'
        }).when('/generate-report', {
            templateUrl:'components/report-generation/report-generation.html',
            controller: 'ReportGenerationController'
        }).when('/data-status', {
            templateUrl:'components/data-status/data-status.html',
            controller: 'DataStatusController'
        }).when('/data-status-result', {
            templateUrl:'components/data-status/data-status-result.html',
            controller: 'DataStatusResultController'
        }).otherwise({
            redirectTo : '/'
        });

        $translateProvider.preferredLanguage('en');
        $translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.useLoader('i18nLoader');


    });
