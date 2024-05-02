package fi.sisu;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudyModuleTest {

    @Test
    void testGetCourseUnitsAsList() {
        StudyModule sm = new StudyModule("sm1", "123", "987", 10, "code123", "outcomes123", "description123");

        CourseUnit cu1 = new CourseUnit("cu1", "123", "123", 1, "code123", "outcomes123", "description123");
        CourseUnit cu2 = new CourseUnit("cu2", "321", "321", 3, "code321", "outcomes321", "description321");
        CourseUnit cu3 = new CourseUnit("cu3", "999", "999", 5, "code999", "outcomes999", "description999");

        sm.addCourseUnit(cu3);
        sm.addCourseUnit(cu2);
        sm.addCourseUnit(cu1);
        List<CourseUnit> cus = sm.getCourseUnitsAsList();
        assertEquals("cu1", cus.get(0).getName());
        assertEquals("cu2", cus.get(1).getName());
        assertEquals("cu3", cus.get(2).getName());
    }

    @Test
    void testGetCourseUnitById() {
        StudyModule sm = new StudyModule("sm1", "123", "987", 10, "code123", "outcomes123", "description123");

        CourseUnit cu1 = new CourseUnit("cu1", "123", "123", 1, "code123", "outcomes123", "description123");
        CourseUnit cu2 = new CourseUnit("cu2", "321", "321", 3, "code321", "outcomes321", "description321");
        CourseUnit cu3 = new CourseUnit("cu3", "999", "999", 5, "code999", "outcomes999", "description999");

        sm.addCourseUnit(cu3);
        sm.addCourseUnit(cu2);
        sm.addCourseUnit(cu1);

        assertEquals("cu1", sm.getCourseUnitById("123").getName());
        assertEquals("cu2", sm.getCourseUnitById("321").getName());
        assertEquals("cu3", sm.getCourseUnitById("999").getName());
    }

    @Test
    void testGetChildStudyModulesAsList() {
        StudyModule sm = new StudyModule("sm", "123", "987", 10, "code123", "outcomes123", "description123");

        StudyModule sm1 = new StudyModule("sm1", "321", "321", 10, "code321", "outcomes123", "description123");
        StudyModule sm2 = new StudyModule("sm2", "987", "987", 20, "code987", "outcomes123", "description123");
        StudyModule sm3 = new StudyModule("sm3", "555", "555", 30, "code555", "outcomes123", "description123");

        sm.addChildStudyModule(sm3);
        sm.addChildStudyModule(sm2);
        sm.addChildStudyModule(sm1);
        List<StudyModule> childSms = sm.getChildStudyModulesAsList();
        assertEquals("sm1", childSms.get(0).getName());
        assertEquals("sm2", childSms.get(1).getName());
        assertEquals("sm3", childSms.get(2).getName());
    }

    @Test
    void testAttributesAndToString() {
        StudyModule sm = new StudyModule("studyModule", "123", "123456789", 30,
                "SM", "<p>desc</p>", "<p>out</p>");

        assertEquals("SM studyModule (30 op)", sm.toString());
        assertEquals("studyModule", sm.getName());
        assertEquals("SM", sm.getCode());
        assertEquals("123", sm.getId());
        assertEquals("123456789", sm.getGroupId());
        assertEquals(30, sm.getMinCredits());
        assertEquals("desc", sm.getDescription());
        assertEquals("out", sm.getOutcomes());
    }

    @Test
    void testGetCourseUnitsAndNestedChildrenCourseUnits() {
        StudyModule sm = new StudyModule("studyModule", "123", "123456789", 30,
                "SM", "<p>desc</p>", "<p>out</p>");

        StudyModule sm1 = new StudyModule("sm1", "321", "321", 10, "code321", "outcomes123", "description123");
        StudyModule sm2 = new StudyModule("sm2", "987", "987", 20, "code987", "outcomes123", "description123");
        StudyModule sm3 = new StudyModule("sm3", "555", "555", 30, "code555", "outcomes123", "description123");

        sm.addChildStudyModule(sm3);
        sm.addChildStudyModule(sm2);
        sm.addChildStudyModule(sm1);

        CourseUnit cu1 = new CourseUnit("cu1", "123", "123", 1, "code123", "outcomes123", "description123");
        CourseUnit cu2 = new CourseUnit("cu2", "321", "321", 3, "code321", "outcomes321", "description321");
        CourseUnit cu3 = new CourseUnit("cu3", "999", "999", 5, "code999", "outcomes999", "description999");

        sm1.addCourseUnit(cu3);
        sm2.addCourseUnit(cu2);
        sm3.addCourseUnit(cu1);

        List<CourseUnit> allCourses = sm.getCourseUnitsAndNestedChildrenCourseUnits();
        assertEquals("cu1", allCourses.get(0).getName());
        assertEquals("cu2", allCourses.get(1).getName());
        assertEquals("cu3", allCourses.get(2).getName());
    }
}