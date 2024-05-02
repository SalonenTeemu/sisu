package fi.sisu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseUnitTest {
    @Test
    void testAttributesAndToString() {
        CourseUnit cu = new CourseUnit("course", "123", "123456789", 5,
                "c", "<p>desc</p>", "<p>out</p>");

        assertEquals("c course (5 op)", cu.toString());
        assertEquals("course", cu.getName());
        assertEquals("c", cu.getCode());
        assertEquals("123", cu.getId());
        assertEquals("123456789", cu.getGroupId());
        assertEquals(5, cu.getMinCredits());
        assertEquals("desc", cu.getDescription());
        assertEquals("out", cu.getOutcomes());

        cu.setCompleted(true);
        assertTrue(cu.isCompleted());
    }
}