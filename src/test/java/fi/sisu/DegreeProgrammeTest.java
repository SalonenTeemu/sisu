package fi.sisu;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DegreeProgrammeTest {
    @Test
    void testGetStudyModulesAsList() {
        DegreeProgramme dp = new DegreeProgramme("degreeprogramme", "123", "987", 120, "code1", "desc", "out");
        StudyModule sm1 = new StudyModule("sm1", "123", "987", 10, "code123", "outcomes123", "description123");
        StudyModule sm2 = new StudyModule("sm2", "567", "123", 30, "code999", "outcomes999", "description999");
        StudyModule sm3 = new StudyModule("sm3", "999", "789", 20, "code321","outcomes321", "description321");

        dp.addStudyModule(sm3);
        dp.addStudyModule(sm2);
        dp.addStudyModule(sm1);
        List<StudyModule> sms = dp.getStudyModulesAsList();
        assertEquals("sm1", sms.get(0).getName());
        assertEquals("sm2", sms.get(1).getName());
        assertEquals("sm3", sms.get(2).getName());
    }

    @Test
    void testStudyModuleById() {
        DegreeProgramme dp = new DegreeProgramme("degreeprogramme", "123", "987", 120, "code123", "desc", "out");
        StudyModule sm1 = new StudyModule("sm1", "123", "987", 10, "code123", "outcomes123", "description123");
        StudyModule sm2 = new StudyModule("sm2", "567", "123", 30, "code999", "outcomes999", "description999");
        StudyModule sm3 = new StudyModule("sm3", "999", "789", 20, "code321","outcomes321", "description321");

        dp.addStudyModule(sm3);
        dp.addStudyModule(sm2);
        dp.addStudyModule(sm1);
        assertEquals("sm1", dp.getStudyModuleById("123").getName());
        assertEquals("sm2", dp.getStudyModuleById("567").getName());
        assertEquals("sm3", dp.getStudyModuleById("999").getName());
    }

    @Test
    void testAttributesAndToString() {
        DegreeProgramme dp = new DegreeProgramme("degreeP", "123", "123456789", 100,
                "dP", "<p>desc</p>", "<p>out</p>");

        assertEquals("dP degreeP (100 op)", dp.toString());
        assertEquals("degreeP", dp.getName());
        assertEquals("dP", dp.getCode());
        assertEquals("123", dp.getId());
        assertEquals("123456789", dp.getGroupId());
        assertEquals(100, dp.getMinCredits());
        assertEquals("desc", dp.getDescription());
        assertEquals("out", dp.getOutcomes());
    }
}