package zapi;

import java.io.IOException;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import Beans.TestCase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.ProtocolNotSuppException;
import HTTPClient.URI;
import com.oracle.bugjirabridge.jira.util.JIRAHttpConnection;
import com.oracle.bugjirabridge.jira.util.JIRAHttpException;

/** Helper class for calling ZAPI */
public class zapi {

    /**
     * Status IDs enum
     */
    public enum Status {
        PASS(1), FAIL(2), WIP(3), BLOCKED(4), UNEXECUTED(0);
        private final int value;

        private Status(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * URLS
     */
    //ZAPI
    private static final String ZAPI_URL = "jira/rest/zapi/latest/";
    private static final String get_project_url = "jira/rest/zapi/latest/util/project-list";
    private static final String get_version_url = "jira/rest/zapi/latest/util/versionBoard-list?projectId=";
    private static final String get_cycles_url = "jira/rest/zapi/latest/cycle";
    private static final String add_test_to_cycle_url = "jira/rest/zapi/latest/execution/addTestsToCycle";
    private static final String get_executions_url = "jira/rest/zapi/latest/execution";
    private static final String execute_url = "jira/rest/zapi/latest/execution";
    private static final String update_attachment_url = "jira/rest/zapi/latest/attachment?entityId=";
    private static final String create_test_step_url = "/jira/rest/zapi/latest/teststep/";


    //Jira API
    private static final String create_test_issue_url = "jira/rest/api/2/issue/";

    private static final String get_project_fields_url = "jira/rest/api/2/issue/createmeta";
    private static final String issue_search_url = "jira/rest/api/2/search";


    /**
     * JIRA credentials: format "username:password" or "" for none.
     */
    private static String url = "https://jira-uat.us.oracle.com";
    private static String userName = "oardc-omsp_sg@oracle.com";
    private static String password = "!QAZ2wsx";

    // ================================================================================
    // ZAPI methods
    // ================================================================================

    public static String getProjectId(final String projectName, String url, String userName, String password)
            throws IOException, JSONException, InterruptedException, ParseException, ModuleException {
        // Get list of versions on the specified project            
        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");
        HTTPResponse response = conn.Get(get_project_url);
        int statusCode = response.getStatusCode();
        String responseData = new String(response.getData());
        if (statusCode == 200) {
            if (responseData.equals(null)) {
                throw new IllegalStateException("JSONObject is null");
            }

            JSONObject responseObject = new JSONObject(new String(responseData));

            final JSONArray projectOptions = (JSONArray) responseObject.get("options");

            // Iterate over versions
            for (int i = 0; i < projectOptions.length(); i++) {
                final JSONObject obj2 = projectOptions.getJSONObject(i);
                // If label matches specified version name
                if (obj2.getString("label").equals(projectName)) {
                    // Return the ID for this version
                    return obj2.getString("value");
                }
            }
            throw new IllegalStateException("Project ID not found for ProjectName=" + projectName);
        } else {
            return null;
        }
    }

    public static String getVersionID(final String versionName, final String projectId, String url, String userName, String password)
            throws IOException, JSONException, InterruptedException, ModuleException, ParseException {
        // Get list of versions on the specified project
        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");
        HTTPResponse response = conn.Get(get_version_url + projectId);
        int statusCode = response.getStatusCode();
        String responseData = new String(response.getData());
        if (statusCode == 200) {
            // success - Jira is up
//    		System.out.println("Response from Get call: " + responseData);
            if (responseData.equals(null)) {
                throw new IllegalStateException("JSONObject is null for projectId=" + projectId);
            }

            JSONObject responseObject = new JSONObject(new String(responseData));

            final JSONArray versionOptions = (JSONArray) responseObject.get("unreleasedVersions");

            // Iterate over versions
            for (int i = 0; i < versionOptions.length(); i++) {
                final JSONObject obj2 = versionOptions.getJSONObject(i);
                // If label matches specified version name
                if (obj2.getString("label").equals(versionName)) {
                    // Return the ID for this version
                    return obj2.getString("value");
                }
            }

            throw new IllegalStateException("Version ID not found for versionName=" + versionName);
        } else {
            return null;
        }
    }

    public static void updateTestExecution(final String executionId, final Status status,
                                           final String comment, String url, String userName, String password) throws IOException, JSONException, InterruptedException, ModuleException, ParseException {
        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");

        String jsonStr = "{\"status\":\"" + String.valueOf(status.getValue()) + "\",\"comment\":\"execute test\"}";
//        System.out.println(jsonStr);
        NVPair[] headers = {new NVPair("Content-Type", "application/json")};
        HTTPResponse response = conn.Get("/jira/rest/auth/1/session");
        int statusCode = response.getStatusCode();
//    	System.out.println(response);
        response = conn.Put(ZAPI_URL + "execution/" + executionId + "/execute", jsonStr.getBytes(), headers);
//    	System.out.println(ZAPI_URL + "execution/" + executionId + "/execute");
        statusCode = response.getStatusCode();
        String responseData = new String(response.getData());
        System.out.println(responseData);
        if (statusCode == 200) {
            System.out.println("Update test execution successfully");
        } else {
            System.out.println("Update test execution failed ");
        }
    }

    public static Object createTestCycle(String cycle_name, String url, String userName, String password, String versionId, String productId)
            throws JSONException, IOException, ModuleException, ParseException, JIRAHttpException, java.text.ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH-mm");
        String time = sdf.format(cal.getTime());
        String testCycleName = "UIAuto" + "_" + time;


        Locale locale = Locale.US;

        SimpleDateFormat frm = new SimpleDateFormat("d/MMM/yy", locale);
        String startTime = frm.format(new Date());

        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");

        String jsonStr = "{\"clonedCycleId\":\"\", \"name\":\"" + cycle_name + "\", \"build\":\"\",\"environment\":\"\", \"description\":\"Create cycle with sprint\", \"startDate\": \"" + startTime + "\", \"endDate\": \"" + startTime + "\", \"projectId\": \"" + productId + "\", \"versionId\": \"" + versionId + "\"}";
        NVPair[] headers = {new NVPair("Content-Type", "application/json")};
        HTTPResponse response = conn.Get("/jira/rest/auth/1/session");
        int statusCode = response.getStatusCode();

        response = conn.Post(get_cycles_url, jsonStr.getBytes(), headers);
        statusCode = response.getStatusCode();
        String responseData = new String(response.getData());
        Object cycleId = null;
        if (statusCode == 200) {
            System.out.println("Create test cycle successfully");
            JSONObject responseObject = new JSONObject(new String(responseData));
            cycleId = responseObject.get("id");
        } else {
            System.out.println("Create test cycle failed ");
        }
        // Logout of Jira
        response = conn.Delete("/jira/rest/auth/1/session", headers);
        statusCode = response.getStatusCode();
        if (statusCode != 204) {
            responseData = new String(response.getData());
            System.out.println("Problem logging out of Jira. Status code " + statusCode + " was returned with response data: " + responseData);
        }
        return cycleId;
    }

    public static Object createTestCase(TestCase tc)
            throws JSONException, IOException, ModuleException, ParseException, JIRAHttpException, java.text.ParseException {

        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");

        String jsonStr = "{\"fields\":{\"project\":{\"id\":\"18507\"},\"summary\":\"something's wrong\",\"issuetype\":{\"id\":\"27\"},\"assignee\":{\"name\":\"jingzhou.wang@oracle.com\"},\"priority\":{\"id\":\"3\"},\"labels\":[],\"timetracking\":{},\"versions\":[],\"environment\":\"environment\",\"description\":\"description\",\"duedate\":null,\"fixVersions\":[],\"components\":[]}}";
        NVPair[] headers = {new NVPair("Content-Type", "application/json")};
        HTTPResponse response = conn.Get("/jira/rest/auth/1/session");
        int statusCode = response.getStatusCode();
        response = conn.Post(create_test_issue_url, jsonStr.getBytes(), headers);
        statusCode = response.getStatusCode();

        String responseData = new String(response.getData());
        Object cycleId = null;
        if (statusCode == 201) {
            System.out.println("Create test case successfully");
            JSONObject responseObject = new JSONObject(new String(responseData));
            cycleId = responseObject.get("id");
        } else {
            System.out.println("Failed to create test case");
        }
        // Logout of Jira
        response = conn.Delete("/jira/rest/auth/1/session", headers);
        statusCode = response.getStatusCode();
        if (statusCode != 204) {
            responseData = new String(response.getData());
            System.out.println("Problem logging out of Jira. Status code " + statusCode + " was returned with response data: " + responseData);
        }
        return cycleId;
    }

    public static Object createTestStep(String tcId)
            throws JSONException, IOException, ModuleException, ParseException, JIRAHttpException, java.text.ParseException {

        JIRAHttpConnection conn = new JIRAHttpConnection(new URI(url), userName, password.toCharArray(), "JIRA");

        String jsonStr = "{\"step\":\"Check for schedule count\",\"data\":\"filter id\",\"result\":\"count should be equal to schedules returned by this filter.\"}";
        NVPair[] headers = {new NVPair("Content-Type", "application/json")};
        HTTPResponse response = conn.Get("/jira/rest/auth/1/session");
        int statusCode = response.getStatusCode();
        response = conn.Post(create_test_step_url+"4886824", jsonStr.getBytes(), headers);
        statusCode = response.getStatusCode();

       // System.out.println("statusCode = " + statusCode);
       // System.out.println("response.getReasonLine() = " + response.getReasonLine());

        String responseData = new String(response.getData());
        Object cycleId = null;
        if (statusCode == 200) {
            System.out.println("Create test step successfully");
            JSONObject responseObject = new JSONObject(new String(responseData));
        } else {
            System.out.println("Failed to create test step");
        }
        // Logout of Jira
        response = conn.Delete("/jira/rest/auth/1/session", headers);
        statusCode = response.getStatusCode();
        if (statusCode != 204) {
            responseData = new String(response.getData());
            System.out.println("Problem logging out of Jira. Status code " + statusCode + " was returned with response data: " + responseData);
        }
        return cycleId;
    }



}
    
