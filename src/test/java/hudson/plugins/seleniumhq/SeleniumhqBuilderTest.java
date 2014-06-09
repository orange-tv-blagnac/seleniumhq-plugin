package hudson.plugins.seleniumhq;

import hudson.model.FreeStyleBuild;
import hudson.model.ParameterValue;
import hudson.model.Result;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SingleFileSCM;

public class SeleniumhqBuilderTest extends HudsonTestCase {
	/**
	 * Test du Builder with no htmlSuite Runner
	 * @throws Exception
	 */
	public void test1() throws Exception {
		String browser = null;
		String startURL = null;
		String suiteFile = null;
		String resultFile = null;
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s
				.contains("ERROR: Please configure the Selenium Remote Control htmlSuite Runner in admin of hudson"));
	}

	/**
	 * Test du Builder with browser = null
	 * @throws Exception
	 */
	public void test2() throws Exception {
		String browser = null;
		String startURL = null;
		String suiteFile = null;
		String resultFile = null;
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("ERROR: Build config : browser field is mandatory"));
	}

	/**
	 * Test du Builder with startURL = null
	 * @throws Exception
	 */
	public void test3() throws Exception {
		String browser = "*iexplore";
		String startURL = null;
		String suiteFile = null;
		String resultFile = null;
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("ERROR: Build config : startURL field is mandatory"));
	}

	/**
	 * Test du Builder with suiteFile = null
	 * @throws Exception
	 */
	public void test4() throws Exception {
		String browser = "*iexplore";
		String startURL = "http://www.google.com";
		String suiteFile = "";
		String resultFile = null;
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("ERROR: Build config : suiteFile field is mandatory"));
	}

	/**
	 * Test du Builder with resultFile = null
	 * @throws Exception
	 */
	public void test5() throws Exception {
		String browser = "*iexplore";
		String startURL = "http://www.google.com";
		String suiteFile = "TestSuites";
		String resultFile = null;
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("ERROR: Build config : resultFile field is mandatory"));
	}

	/**
	 * Test du Builder with bad suiteFile
	 * @throws Exception
	 */
	public void test6() throws Exception {
		String browser = "*iexplore";
		String startURL = "http://www.google.com";
		String suiteFile = "TestSuites";
		String resultFile = "index.html";
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.FAILURE, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("ERROR: The suiteFile is not a file or an url ! Check your build configuration."));
	}

	/**
	 * Test du Builder with bad good suiteFile path as file
	 * @throws Exception
	 */
	public void test7() throws Exception {

		String browser = "*iexplore";
		String startURL = "http://www.google.com";
		String suiteFile = "";
		String resultFile = "index.html";
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		suiteFile = new File(new File(project.getRootDir(), "workspace"), "emptyResult.html").toURI().toString();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));
		project.setScm(new SingleFileSCM("emptyResult.html", getClass().getResource("emptyResult.html")));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");
		FreeStyleBuild build = project.scheduleBuild2(0).get();

		assertEquals(Result.SUCCESS, build.getResult());

		String s = FileUtils.readFileToString(build.getLogFile());
		assertTrue(s.contains("Unable to access jarfile sdf"));
	}

	/**
	 * Test du Builder with bad good real file
	 * @throws Exception
	 */
	public void test8() throws Exception {
		String browser = "*iexplore";
		String startURL = "http://www.google.com";
		String suiteFile = "http://www.google.com";
		String resultFile = "index.html";
		String other = null;
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other));
		project.setScm(new SingleFileSCM("emptyResult.html", getClass().getResource("emptyResult.html")));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("rtyrturturtur");
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		assertEquals(Result.SUCCESS, build.getResult());
	}

	/**
	 * Test du Builder with startURL variables substitutions
	 * @throws Exception
	 */
	public void test9() throws Exception {

		String browser = "${browser}";
		String startURL = "http://www.${mySite}.com/${myPath}";
		String suiteFile = "";
		String resultFile = "${resultFile}";
		String other = "";
		FreeStyleProject project = createFreeStyleProject();
		suiteFile = new File(new File(project.getRootDir(), "workspace"), "emptyResult.html").toURI().toString();
		SeleniumhqBuilder seleniumhqBuilder = new SeleniumhqBuilder(browser, startURL, suiteFile, resultFile, other);
		project.getBuildersList().add(seleniumhqBuilder);
		project.setScm(new SingleFileSCM("emptyResult.html", getClass().getResource("emptyResult.html")));

		SeleniumhqBuilder.DESCRIPTOR.setSeleniumRunner("sdf");

		FreeStyleBuild build = project.scheduleBuild2(0).get();

		ArrayList<ParameterValue> arrayList = new ArrayList<ParameterValue>();
		arrayList.add(new StringParameterValue("mySite", "xxx"));
		arrayList.add(new StringParameterValue("myPath", "path"));
		arrayList.add(new StringParameterValue("resultFile", "index.html"));
		arrayList.add(new StringParameterValue("browser", "*iexplore"));
		ParametersAction parameters = new ParametersAction(arrayList);

		build.addAction(parameters);

		String startURLResolved = seleniumhqBuilder.substituteVars(seleniumhqBuilder.getStartURL(), build, System.out);
		assertEquals("http://www.xxx.com/path", startURLResolved);

		String browserResolved = seleniumhqBuilder.substituteVars(seleniumhqBuilder.getBrowser(), build, System.out);
		assertEquals("*iexplore", browserResolved);

		String resultFileResolved = seleniumhqBuilder.substituteVars(seleniumhqBuilder.getResultFile(), build,
				System.out);
		assertEquals("index.html", resultFileResolved);

	}
}
