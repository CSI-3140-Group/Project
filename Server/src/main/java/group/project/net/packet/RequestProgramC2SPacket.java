package group.project.net.packet;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import group.project.data.Course;
import group.project.data.Program;
import group.project.data.Semester;
import group.project.net.Connection;
import group.project.net.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RequestProgramC2SPacket extends Packet {

    @Override
    public void handle(Connection connection) throws IOException {
        Page page = connection.getPage();
        page.navigate("https://www.uocampus.uottawa.ca/psc/csprpr9www_newwin/EMPLOYEE/SA/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL?NavColl=true");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        List<Semester> semesters = new ArrayList<>();

        // Look through terms
        for(Locator locator : page.locator("//*[@name=\"SSR_DUMMY_RECV1$sels$0\"]").all()) {
            locator.setChecked(true);
            page.locator("#DERIVED_SSS_SCT_SSR_PB_GO").first().click();
            page.waitForSelector("//table[@id=\"TERM_CLASSES$scroll$0\"]");

            List<Course> courses = new ArrayList<>();

            for(Locator row : page.locator("//table[@id=\"TERM_CLASSES$scroll$0\"]/tbody/tr/td/table/tbody/tr").all()) {
                String[] entries = row.locator("//td/div/span").all().stream()
                        .map(Locator::textContent).toArray(String[]::new);
                if(entries.length == 0) continue;
                String[] name = entries[0].split(Pattern.quote(" "));

                courses.add(new Course(
                    name[0], name[1], entries[1],
                    entries[2].charAt(0) == 160 ? "N/A" : entries[2],
                    entries[3],
                    entries[4].charAt(0) == 160 ? "N/A" : entries[4],
                    entries[5].charAt(0) == 160 ? "N/A" : entries[5]
                ));
            }

            // Year - Term - Level
            String name = page.locator("//*[@id=\"DERIVED_REGFRM1_SSR_STDNTKEY_DESCR$11$\"]").textContent();
            String[] nameSplit = name.split(Pattern.quote(" "));

            // TGPA and CGPA - Only id, and # of line varies from term to term
            List<String> possibleTGPAs = new ArrayList<>();
            List<String> possibleCGPAs = new ArrayList<>();

            for(int i = 0; i < 15; i++) {
                try {
                    possibleTGPAs.add(page.querySelector("//*[@id=\"STATS_ENRL$" + i + "\"]").textContent());
                    possibleCGPAs.add(page.querySelector("//*[@id=\"STATS_CUMS$" + i + "\"]").textContent());
                } catch (Exception e) {
                    break;
                }
            }

            String tgpa = possibleTGPAs.get(possibleTGPAs.size() - 1);
            String cgpa = possibleCGPAs.get(possibleCGPAs.size() - 1);

            semesters.add(new Semester(
                    nameSplit[4], nameSplit[1], nameSplit[0],
                    tgpa.charAt(0) == 160 ? "N/A" : tgpa,
                    cgpa.charAt(0) == 160 ? "N/A" : cgpa,
                    courses.toArray(new Course[0])
            ));

            page.locator("//*[@id=\"DERIVED_SSS_SCT_SSS_TERM_LINK\"]").click();
        }

        Program program = new Program(semesters.toArray(new Semester[0]));
        connection.send(new EvaluateProgramS2CPacket(program));
    }

}
