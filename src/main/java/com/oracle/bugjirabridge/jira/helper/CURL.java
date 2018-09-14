package com.oracle.bugjirabridge.jira.helper;

public class CURL {

    //Jira API
    public static final String create_test_issue_url = "jira/rest/api/2/issue/";
    public static final String edit_test_issue_url = "jira/rest/api/2/issue/";
    public static final String delete_test_issue_url = "jira/rest/api/2/issue/";
    public static final String get_project_fields_url = "jira/rest/api/2/issue/createmeta";
    public static final String issue_search_url = "jira/rest/api/2/search";
    public static final String ISSUE_LINK_TYPE_URL = "jira/rest/api/2/issueLinkType";
    public static final String ISSUE_LINK_URL = "jira/rest/api/2/issueLink";
    public static final String GET_ISSUE_URL = "jira/rest/api/2/issue/";
    public static final String GET_CUSTOME_FIELD_OPTION = "jira/rest/api/2/customFieldOption/";
    public static final String GET_PRIORITIES_URL = "/jira/rest/api/2/priority/";
    public static final String GET_PRIORITY_URL = "/jira/rest/api/2/priority/";
    public static final String GET_PROJECT_URL = "jira/rest/api/2/project/";

    //ZAPI
    public static final String ZAPI_URL = "jira/rest/zapi/latest/";
    public static final String get_projects_url = "jira/rest/zapi/latest/util/project-list";
    public static final String get_version_url = "jira/rest/zapi/latest/util/versionBoard-list?projectId=";
    public static final String get_cycles_url = "jira/rest/zapi/latest/cycle";
    public static final String add_test_to_cycle_url = "jira/rest/zapi/latest/execution/addTestsToCycle";
    public static final String get_executions_url = "jira/rest/zapi/latest/execution";
    public static final String execute_url = "jira/rest/zapi/latest/execution";
    public static final String create_test_step_url = "/jira/rest/zapi/latest/teststep/";
    public static final String get_test_steps_url = "/jira/rest/zapi/latest/teststep/";
    public static final String delete_test_step_url = "/jira/rest/zapi/latest/teststep/";

    //Structure API

    public static final String SAPI_URL = "jira/rest/structure/latest/";
    public static final String GET_STRUCTURES = SAPI_URL + "structure";
    public static final String GET_FOREST_URL = SAPI_URL + "forest/latest?s={\"structureId\":%s}";
    public static final String UPDATE_FOREST_URL = SAPI_URL + "forest/update";
    public static final String CREATE_ITEM_URL = SAPI_URL + "item/create";
    public static final String LOAD_VALUE = SAPI_URL + "value";


}
