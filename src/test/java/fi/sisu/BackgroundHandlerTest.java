package fi.sisu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BackgroundHandlerTest {

    @Test
    void testGetDegreeProgrammesAsList() {
        BackgroundHandler backgroundHandler = new BackgroundHandler();

        List<DegreeProgramme> dps = backgroundHandler.getDegreeProgrammesAsList();
        assertEquals("Ympäristö- ja energiatekniikan DI-ohjelma", dps.get(272).getName());
        assertEquals("Akuuttilääketieteen erikoislääkärikoulutus (55/2020)", dps.get(0).getName());
    }

    @Test
    void testGetDegreeProgrammeById() {
        BackgroundHandler backgroundHandler = new BackgroundHandler();

        assertEquals("Työterveyshuollon erikoislääkärikoulutus (55/2020)", backgroundHandler.getDegreeProgrammeById("otm-5e4d66b8-e356-4c8a-afbf-33b38608163e").getName());
        assertEquals("Tietojenkäsittelytieteiden kandidaattiohjelma", backgroundHandler.getDegreeProgrammeById("otm-1d25ee85-df98-4c03-b4ff-6cad7b09618b").getName());
    }

    @Test
    void generalTestPrintOfAllDegreeProgrammes() {
        BackgroundHandler backgroundHandler = new BackgroundHandler();
        // Print every degree programme:
        List<DegreeProgramme> dps = backgroundHandler.getDegreeProgrammesAsList();
        for (DegreeProgramme d : dps) {
            System.err.println(d.toString());
        }
    }

    @ParameterizedTest
    @MethodSource("studyModulesProvider")
    void printStudyModulesRecursively(StudyModule sm, int repeat) {
        String str = "   ";
        System.err.println(str.repeat(repeat) + "StudyModule: " + sm.toString() + " " + sm.getCode() + " " + sm.getGroupId() +  " " + sm.getMinCredits() + " " + sm.getDescription() + " ; " + sm.getOutcomes());

        // First get all courses of this module and print them
        List<CourseUnit> cus = sm.getCourseUnitsAsList();
        for (CourseUnit cu : cus) {
            System.err.println(str.repeat(repeat + 1) + "Course: " + cu.toString() + " " + cu.getCode() + " " + cu.getMinCredits() + " " + cu.getDescription() + " ; " + cu.getOutcomes());
        }

        // Secondly get all childStudyModules of this module and go through them recursively
        List<StudyModule> childsms = sm.getChildStudyModulesAsList();
        for (StudyModule csm : childsms) {
            printStudyModulesRecursively(csm, repeat + 2);
        }
    }

    static Stream<Arguments> studyModulesProvider() {
        BackgroundHandler backgroundHandler = new BackgroundHandler();

        // Get one degree to look closer at:
        DegreeProgramme dP = backgroundHandler.getDegreeProgrammeById("otm-91901c8b-e109-4cf8-b9c7-eaac16417268");
        backgroundHandler.getDataOfDegreeProgramme(dP);
        List<StudyModule> sms = dP.getStudyModulesAsList();

        List<Arguments> argumentsList = new ArrayList<>();

        // Add each study module and repeat value to the argument list
        for (StudyModule sm : sms) {
            argumentsList.add(Arguments.of(sm, 1));
            addChildStudyModulesToList(argumentsList, sm, 3);
        }
        return argumentsList.stream();
    }

    private static void addChildStudyModulesToList(List<Arguments> argumentsList, StudyModule sm, int repeat) {
        List<StudyModule> childSms = sm.getChildStudyModulesAsList();
        if (childSms.isEmpty()) {
            return;
        }
        for (StudyModule childSm : childSms) {
            argumentsList.add(Arguments.of(childSm, repeat));
            addChildStudyModulesToList(argumentsList, childSm, repeat + 2);
        }
    }
}