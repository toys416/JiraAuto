import Beans.TestCase;
import org.json.JSONArray;
import org.junit.Test;
import zapi.zapi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import org.junit.Assert.*;

/**
* Date : 2017-04-27
* Owner : yuhong.ma
* @author yuhong.ma
*/

public class zapiTest {

	@Test
	public void test() throws Exception {
		//String url = "https://jira.oraclecorp.com";
		String url= "https://jira-uat.us.oracle.com";
//	    String userName = "oardc-omsp_sg@oracle.com";
//	    String password = "!QAZ2wsx";
		String userName = "jingzhou.wang@oracle.com";
		String password = "Toyszhou@416";
		String productName = "E-Business Suite";
		zapi zapiTest = new zapi();

	    String productId = zapiTest.getProjectId(productName, url, userName, password);
		System.out.println("productId = " + productId);

		//TestCase tc=new TestCase("18507","summary","desc","1","27","jingzhou.wang@oracle.com");
		//zapiTest.createTestCase(tc);
		zapiTest.createTestStep("4886632");


	}

}
