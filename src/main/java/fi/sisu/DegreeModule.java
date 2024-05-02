package fi.sisu;

import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * An abstract class for storing information on DegreeProgrammes, Modules and Courses.
 */
public abstract class DegreeModule implements Comparable<DegreeModule> {
    private String name;
    private String id;
    private String groupId;
    private int minCredits;
    private String code;
    private String description;
    private String outcomes;

    // Overriding equals operator to compare degreeModules by ID instead of object reference
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof DegreeModule)) {
            return false;
        }
        DegreeModule other = (DegreeModule) o;
        return this.id.equals(other.id);
    }

    // Required when overriding equals operator
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     *
     * @param name name of the Degree programme, Study module or Course.
     * @param id id of the Degree programme, Study module or Course.
     * @param groupId group id of the Degree programme, Study module or Course.
     * @param minCredits minimum credits of the Degree programme, Study module or Course.
     * @param code code of the Degree programme, Study module or Course.
     * @param description description of the Degree programme, Study module or Course.
     * @param outcomes learning outcomes of the Degree programme, Study module or Course.
     */
    public DegreeModule(String name, String id, String groupId, int minCredits, String code, String description, String outcomes) {
        this.name = name;
        this.id = id;
        this.groupId = groupId;
        this.minCredits = minCredits;
        this.code = code;

        // Parse out all html-elements and extra spaces of the description and learning outcomes with the help of Jsoup
        Document doc = Jsoup.parse(outcomes);
        this.outcomes = doc.text().replaceAll("(\\\\n|\\\\t)", "").replaceAll(" {2}", "");

        doc = Jsoup.parse(description);
        this.description = doc.text().replaceAll("(\\\\n|\\\\t)", "").replaceAll(" {2}", "");
    }

    /**
     * Set the name attribute of the Degree programme, Study module or Course.
     *
     * @param name the name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the Degree programme, Study module or Course.
     *
     * @return name of the Degree programme, Study module or Course.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the id attribute of the Degree programme, Study module or Course.
     *
     * @param id the id to be set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the id of the Degree programme, Study module or Course.
     *
     * @return id of the Degree programme, Study module or Course.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the groupId attribute of the Degree programme, Study module or Course.
     *
     * @param groupId the groupId to be set.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the group id of the Degree programme, Study module or Course.
     *
     * @return group id of the Degree programme, Study module or Course.
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * Set the minCredits attribute of the Degree programme, Study module or Course.
     *
     * @param minCredits the minCredits to be set.
     */
    public void setMinCredits(int minCredits) {
        this.minCredits = minCredits;
    }

    /**
     * Returns the minimum credits of the Degree programme, Study module or Course.
     *
     * @return minimum credits of the Degree programme, Study module or Course.
     */
    public int getMinCredits() {
        return this.minCredits;
    }

    /**
     * Set the code attribute of the Degree programme, Study module or Course.
     *
     * @param code the code to be set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the code of the Degree programme, Study module or Course.
     *
     * @return code of the Degree programme, Study module or Course.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the description of the Degree programme, Study module or Course by
     * parsing out the html elements and spaces.
     *
     * @param description the non parsed description acquired from the API.
     */
    public void setDescription(String description) {
        Document doc = Jsoup.parse(description);
        this.description = doc.text().replaceAll("(\\\\n|\\\\t)", "")
                .replaceAll(" {2}", "");
    }

    /**
     * Returns the description of the Degree programme, Study module or Course.
     *
     * @return description of the Degree programme, Study module or Course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the outcomes of the Degree programme, Study module or Course by
     * parsing out the html elements and spaces.
     *
     * @param outcomes the non parsed outcomes acquired from the API.
     */
    public void setOutcomes(String outcomes) {
        Document doc = Jsoup.parse(outcomes);
        this.outcomes = doc.text().replaceAll("(\\\\n|\\\\t)", "")
                .replaceAll(" {2}", "");
    }

    /**
     * Returns the learning outcomes of the Degree programme, Study module or Course.
     *
     * @return learning outcomes of the Degree programme, Study module or Course.
     */
    public String getOutcomes() {
        return outcomes;
    }

    /**
     * Returns a string based on the data available from the Degree programme, Study module or Course.
     *
     * @return String of the Degree programme, Study module or Course.
     */
    @Override
    public String toString() {
        if (!this.getCode().equals("NULL")) {
            if (this.getMinCredits() != 0) {
                return this.getCode() + " " + this.getName() + " (" + this.getMinCredits() + " op)";
            } else {
                return this.getCode() + " " + this.getName();
            }
        } else {
            if (this.getMinCredits() != 0) {
                return this.getName() + " (" + this.getMinCredits() + " op)";
            } else {
                return this.getName();
            }
        }
    }

    /**
     * Comparator based on the name attribute for the Degree programmes, Study modules and Courses.
     *
     * @param dM the Degree programme, Study module or Course to be compared to.
     * @return int based on the name attributes of the two compared Degree programmes, Study modules or Courses.
     */
    @Override
    public int compareTo(DegreeModule dM) {
        return this.getName().compareTo(dM.getName());
    }

    /**
     * Method used in creating additional information boxes when hovering over modules in the GUI.
     * Returns a string based on what information is available for that Degree programme, Study module or Course.
     *
     * @return a String of the additional data available from the Degree programme, Study module or Course.
     */
    public String getTooltipText() {
        if (getOutcomes().equals("NULL") && getDescription().equals("NULL")) {
            return "NULL";
        } else if (getOutcomes().equals("NULL") && !getDescription().equals("NULL")) {
            return getDescription();
        } else if (!getOutcomes().equals("NULL") && getDescription().equals("NULL")) {
            return getOutcomes();
        } else {
            return getDescription() + "\n\n" + getOutcomes();
        }
    }
}
