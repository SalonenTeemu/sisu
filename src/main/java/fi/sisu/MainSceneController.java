package fi.sisu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Controller class for the main scene in JavaFX Sisu application.
 */
public class MainSceneController {

    /**
     * The currently selected degree programme.
     */
    DegreeProgramme userDegreeProgramme;

    /**
     * The combo box for selecting a degree programme.
     */
    @FXML
    private ComboBox<DegreeProgramme> degreeProgrammeComboBox;

    /**
     * The tree view for displaying currently selected degree programme and its
     * content, such as study modules and courses.
     */
    @FXML
    private TreeView<DegreeModule> treeView;

    /**
     * The label for displaying the name of the user currently logged in.
     */
    @FXML
    private Label nameLabel;

    /**
     * The label for displaying the student number of the user currently logged
     * in.
     */
    @FXML
    private Label studentNumberLabel;

    /**
     * The tab pane for displaying different views.
     */
    @FXML
    private TabPane tabPane;

    /**
     * The list view for displaying the courses user has selected.
     */
    @FXML
    private ListView<CourseUnit> listView;

    /**
     * The button to show instructions to the user.
     */
    @FXML
    private Button infoButton;

    /**
     * Initializes the controller. Sets up the tree view and list view cell
     * factories, updates the user information labels and populates the degree
     * programme combo box. Sets the action to the infoButton.
     *
     * @throws IOException If there occurs one while getting the degree
     * programmes from the BackgroundHandler.
     */
    public void initialize() throws IOException {
        addListViewCellFactory();
        addTreeViewCellFactory();
        updateStudentInfoLabels();
        updateAvailableDegreeProgrammes();

        // Also call this manually on first initialization to handle initial
        // loading of the main tab
        onMainTabOpened();
        // Listener for tab changes
        tabPane.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.intValue() == 0) {
                        onMainTabOpened();
                    }
                    if (newValue.intValue() == 1) {
                        onTreeViewTabOpened();
                    }
                });

        // The instructions to show to the user when the infoButton is clicked
        String infoText = "Voit tarkastella opintokokonaisuuksien ja kurssien lisätietoja viemällä hiiren kyseisen moduulin päälle. " +
                          "Valitse kursseja vasemmalta oikealle joko klikkaamalla niitä ja sitten painamalla \"Lisää kurssi\"-nappia " +
                          "tai yksinkertaisemmin tuplaklikkaamalla kurssia. Tekemällä operaation opintokokonaisuudelle, voit siirtää sen kaikki kurssit kerralla. " +
                          "Kurssien poisto valituista opinnoista tapahtuu samalla tavalla nyt vain käyttämällä \"Poista kurssi\"-nappia tai tuplaklikkausta. " +
                          "Merkitse opintoja suoritetuksi valituissa opinnoissa klikkaamalla niiden valintaruutua. " +
                          "Muista tallentaa valitut opintosi lopuksi klikkaamalla \"Tallenna\"-nappia.";
        // Create a tooltip to show the information
        Tooltip infoTooltip = new Tooltip(infoText);
        infoTooltip.setMaxWidth(600);
        infoTooltip.setWrapText(true);
        infoTooltip.setFont(Font.font(15));

        // Add action to the infoButton. When clicked, show the instructions and when clicked again, hide them
        infoButton.setOnAction(event -> {
            if (infoTooltip.isShowing()) {
                infoTooltip.hide();
                infoButton.setText("Avaa käyttöohjeet");
            } else {
                infoTooltip.show(infoButton.getScene().getWindow(),
                        infoButton.localToScreen(0, 0).getX() + infoButton.getWidth(),
                        infoButton.localToScreen(0, 0).getY());
                infoButton.setText("Sulje käyttöohjeet");
            }
        });
    }

    /**
     * Populates the degree programme combo box with available degree programmes
     * obtained from the BackgroundHandler.
     */
    private void updateAvailableDegreeProgrammes() {
        var degreeProgrammesListResult = Sisu.getBackgroundHandler()
                .getDegreeProgrammesAsList();
        degreeProgrammeComboBox.getItems()
                .setAll(degreeProgrammesListResult);
    }

    /**
     * Updates the user information labels with the name and the student number
     * of the user currently logged in.
     */
    private void updateStudentInfoLabels() {
        User loggedInUser = Sisu.getAuthentication().getCurrentlyLoggedInUser();
        studentNumberLabel.setText(loggedInUser.getStudentNumber());
        nameLabel.setText(loggedInUser.getName());
    }

    /**
     * Updates the state of the check box in the given list cell based on the
     * status of the corresponding course unit (whether the course is completed
     * or not).
     *
     * @param cell The list cell containing the check box.
     * @param item The course unit corresponding to the check box.
     */
    private void updateCheckBoxState(ListCell<CourseUnit> cell, CourseUnit item) {
        CheckBox checkBox = (CheckBox) cell.getGraphic();
        boolean completed = item.isCompleted();
        checkBox.setSelected(completed);
    }

    /**
     * Sets a custom cell factory to the ListView to add event handlers for the
     * course units displayed. Each course unit is displayed with a check box
     * that toggles the completion state of the course unit and a tooltip with
     * additional information if available.
     */
    public void addListViewCellFactory() {
        // Add a custom cell factory to the listView so that we can add
        // event handlers for the items
        listView.setCellFactory((ListView<CourseUnit> p) -> {
            ListCell<CourseUnit> cell = new ListCell<CourseUnit>() {
                private final CheckBox checkBox = new CheckBox();

                @Override
                protected void updateItem(CourseUnit item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        setText(item.toString());
                        setGraphic(checkBox);
                        if (item.getTooltipText() == null || item.getTooltipText().equals("NULL")) {
                            setTooltip(null);
                        } else {
                            Tooltip tooltip = new Tooltip(item.getTooltipText());
                            tooltip.setMaxWidth(600);
                            tooltip.setWrapText(true);
                            tooltip.setFont(Font.font(15));
                            tooltip.setShowDelay(new Duration(1000));
                            tooltip.setOnShowing(e -> {
                                long duration = (long) tooltip.getShowDuration().toMillis();
                                if (duration < 1000) {
                                    e.consume();
                                }
                            });
                            tooltip.setShowDuration(Duration.INDEFINITE);
                            setTooltip(tooltip);
                        }
                        updateCheckBoxState(this, item);
                        checkBox.setOnAction(event -> {
                            item.setCompleted(checkBox.isSelected());
                        });
                    }
                }
            };
            cell.setOnMouseClicked((event) -> {
                // Check that its a double-click
                if (event.getClickCount() == 2) {
                    removeCourseUnitsFromListView();
                }
            });
            return cell;
        });
    }

    /**
     * Sets a custom cell factory to the TreeView to add event handlers for the
     * items (degree programme and its study modules, courses etc.) displayed.
     * Each item is displayed with a tooltip with additional information if
     * available.
     */
    public void addTreeViewCellFactory() {
        // Add a custom cell factory to the treeView so that we can add
        // event handlers for the items
        treeView.setCellFactory((TreeView<DegreeModule> param) -> {
            TreeCell<DegreeModule> cell = new TreeCell<DegreeModule>() {
                @Override
                protected void updateItem(DegreeModule item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item.toString());
                        if (item.getTooltipText() == null || item.getTooltipText().equals("NULL")) {
                            setTooltip(null);
                        } else {
                            Tooltip tooltip = new Tooltip(item.getTooltipText());
                            tooltip.setMaxWidth(600);
                            tooltip.setWrapText(true);
                            tooltip.setFont(Font.font(15));
                            tooltip.setShowDelay(new Duration(1000));
                            tooltip.setOnShowing(e -> {
                                long duration = (long) tooltip.getShowDuration().toMillis();
                                if (duration < 1000) {
                                    e.consume();
                                }
                            });
                            tooltip.setShowDuration(Duration.INDEFINITE);
                            setTooltip(tooltip);
                        }
                    }
                }
            };
            cell.setOnMouseClicked((event) -> {
                // Check that its a double-click
                if (event.getClickCount() == 2) {
                    addSelectedTreeCourses();
                }
            });
            return cell;
        });
    }

    /**
     * Updates the list view and tree view based on the currently logged in
     * user's degree programme.
     */
    private void onTreeViewTabOpened() {
        User user = Sisu.getAuthentication().getCurrentlyLoggedInUser();
        String degreeProgrammeId = user.getDegreeModule();
        if (userDegreeProgramme == null || !userDegreeProgramme.getId().equals(degreeProgrammeId)) {
            userDegreeProgramme = Sisu.getBackgroundHandler().getDegreeProgrammeById(degreeProgrammeId);
        }
        updateListViewForUsersCourses(user);
        updateTreeViewForDegreeProgramme(userDegreeProgramme);
    }

    /**
     * Loads the user's courses and updates the list view to display them.
     *
     * @param user The user object containing the user's course data.
     */
    private void updateListViewForUsersCourses(User user) {
        List<CourseUnit> courses = user.getCourses();
        addCourseUnitsToSelectedList(courses);
    }

    /**
     * Updates the degree programme combo box to have all the available degree
     * programmes as options. Sets the user's degree programme for the value of
     * the combo box (if the user has one selected), otherwise the default value
     * is displayed.
     */
    private void onMainTabOpened() {
        String studentNumber = Sisu.getAuthentication().getCurrentlyLoggedInStudentNumber();
        User userFromFile;
        String id;
        try {
            userFromFile = UserReaderWriter.getUserData(studentNumber);
            id = userFromFile.getDegreeModule();
        } catch (Exception e) {
            System.err.println("Error reading degreeProgramme from file: " + e.getMessage());
            return;
        }
        Optional<DegreeProgramme> selectedItem = degreeProgrammeComboBox.getItems()
                .stream()
                .filter(programme -> programme.getId()
                .equals(id)).findFirst();

        if (selectedItem.isPresent()) {
            degreeProgrammeComboBox.setValue(selectedItem.get());
        }
    }

    /**
     * Updates the TreeView to display the selected degree programme as a
     * TreeItem and its content (study modules, courses etc.) as nested
     * TreeItems.
     *
     * @param selectedProgramme The DegreeProgramme object containing the study
     * modules, courses etc.
     */
    private void updateTreeViewForDegreeProgramme(DegreeProgramme selectedProgramme) {
        if (selectedProgramme == null) {
            treeView.setRoot(null);
            return;
        }
        Sisu.getBackgroundHandler().getDataOfDegreeProgramme(selectedProgramme);
        List<StudyModule> studyModules = selectedProgramme.getStudyModulesAsList();
        TreeItem<DegreeModule> root = treeView.getRoot();
        if (root == null || !root.getValue().equals(selectedProgramme)) {
            // Initialize root item if it does not exist or the selected
            // programme has changed
            root = new TreeItem<>(selectedProgramme);
            treeView.setRoot(root);
        }
        for (StudyModule module : studyModules) {
            updateTreeItemsRecursively(module, root);
        }
        // After updating the state of the tree, remove any higher-level items
        // that don't have any children nested
        removeEmptyContainerChildren(root);
    }

    /**
     * Recursively removes all empty child elements of the given tree item.
     *
     * @param item The tree item to remove empty children from.
     * @return true if the item is empty after removing all of its empty
     * children, false otherwise.
     */
    private boolean removeEmptyContainerChildren(TreeItem<DegreeModule> item) {
        // Never remove CourseUnits as they cannot contain other items
        if (item.getValue() instanceof CourseUnit) {
            return false;
        }

        // Iterate through children of the item and recursively make them
        // remove their empty items
        var children = item.getChildren();
        var childrenIterator = children.iterator();
        while (childrenIterator.hasNext()) {
            var child = childrenIterator.next();
            // If the child is empty after the recursive call, delete that child
            boolean isEmptyAfterRemovals = removeEmptyContainerChildren(child);
            if (isEmptyAfterRemovals) {
                childrenIterator.remove();
            }
        }
        // Return whether this item became empty and can therefore be removed
        return item.getChildren().isEmpty();
    }

    /**
     * Recursively adds the StudyModule and its sub-modules and courses to the
     * TreeItem hierarchy. Possible duplicates are skipped and items that are
     * already displayed in the list view of selected courses are deleted from
     * the tree view.
     *
     * @param module The StudyModule to add to the tree.
     * @param parent The parent TreeItem of the StudyModule.
     */
    private void updateTreeItemsRecursively(StudyModule module, TreeItem<DegreeModule> parent) {
        // Check if a tree item exists for this module, if not, create it
        var existingTreeItem = parent.getChildren()
                .stream()
                .filter((child) -> child.getValue().equals(module)).findFirst();
        TreeItem<DegreeModule> currentModule;
        if (existingTreeItem.isPresent()) {
            currentModule = existingTreeItem.get();
        } else {
            currentModule = new TreeItem<>(module);
            parent.getChildren().add(currentModule);
        }

        List<CourseUnit> cus = module.getCourseUnitsAsList();
        for (CourseUnit cu : cus) {
            var existingCourseUnit = currentModule.getChildren()
                    .stream()
                    .filter((child) -> child.getValue().equals(cu)).findFirst();
            boolean isItemInSelectedCoursesList = listView.getItems().contains(cu);
            // Skip adding item if it already exists in the tree
            if (!existingCourseUnit.isPresent() && !isItemInSelectedCoursesList) {
                TreeItem<DegreeModule> course = new TreeItem<>(cu);
                currentModule.getChildren().add(course);
            } else if (existingCourseUnit.isPresent() && isItemInSelectedCoursesList) {
                // If item is in the tree and in the selected courses list,
                // remove it from the tree
                currentModule.getChildren().remove(existingCourseUnit.get());
            }
        }
        List<StudyModule> childNodes = module.getChildStudyModulesAsList();
        for (StudyModule sm : childNodes) {
            updateTreeItemsRecursively(sm, currentModule);
        }

        // Keep tree items sorted
        parent.getChildren().sort((a, b) -> {
            return a.getValue().compareTo(b.getValue());
        });
    }

    /**
     * Handles the button click event for logging out of the application.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If the was an error in quitting the application.
     */
    @FXML
    private void handleLogoutButtonClick(ActionEvent event) throws IOException {
        Sisu.switchToLoginScene();
    }

    /**
     * Handles the button click event for saving the selected degree programme
     * to the user's file.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there was error in saving the degree programme.
     * @throws Exception If there was error in saving the degree programme to
     * user's file.
     */
    @FXML
    private void handleSaveDegreeButtonClicked(ActionEvent event) throws IOException, Exception {
        DegreeProgramme selectedProgramme = degreeProgrammeComboBox.getValue();
        if (selectedProgramme == null) {
            System.out.println("No degree programme selected");
            return;
        }

        String studentNumber = Sisu.getAuthentication().getCurrentlyLoggedInStudentNumber();
        UserReaderWriter userReaderWriter = new UserReaderWriter(null);
        User userFromFile = userReaderWriter.getUserData(studentNumber);
        userReaderWriter.setDegreeModuleToAUser(selectedProgramme, userFromFile);
    }

    /**
     * Adds the given list of CourseUnits to the listView, only adding items
     * that aren't already there. Then calls the method to update the tree view
     * based on the user's degree programme so that the tree view no longer
     * contains the courses that were added to the listView.
     *
     * @param courseUnitsToAdd The list of CourseUnits to add to the listView.
     */
    private void addCourseUnitsToSelectedList(List<CourseUnit> courseUnitsToAdd) {
        if (courseUnitsToAdd == null || courseUnitsToAdd.isEmpty()) {
            return;
        }
        // Filter out items that are already in the listView and
        // add only those that aren't
        List<CourseUnit> courseUnitsWithoutDuplicates = courseUnitsToAdd
                .stream()
                .filter((cu) -> !listView.getItems().contains(cu))
                .collect(Collectors.toList());

        listView.getItems().addAll(courseUnitsWithoutDuplicates);
        updateTreeViewForDegreeProgramme(userDegreeProgramme);
    }

    /**
     * Adds the selected course(s) from the TreeView to the listView. If the
     * selected item is a degree programme or any module that contains
     * sub-modules, all of its sub-modules with their content will be added.
     * Then calls the method to update the tree view based on the user's degree
     * programme, so that the tree view will no longer contains the items that
     * were added to the listView.
     */
    private void addSelectedTreeCourses() {
        List<String> courseList = new ArrayList<>();
        var selectedItem = treeView.getSelectionModel().getSelectedItem().getValue();
        ArrayList<CourseUnit> selectedCourses = new ArrayList<>();
        if (selectedItem == null) {
            return;
        }
        if (selectedItem instanceof DegreeProgramme) {
            DegreeProgramme selectedDegreeProgramme = (DegreeProgramme) selectedItem;
            List<StudyModule> studyModules = selectedDegreeProgramme.getStudyModulesAsList();
            studyModules.forEach((studyModule) -> {
                selectedCourses.addAll(studyModule.getCourseUnitsAndNestedChildrenCourseUnits());
            });
        }
        if (selectedItem instanceof StudyModule) {
            StudyModule selectedStudyModule = (StudyModule) selectedItem;
            selectedCourses.addAll(selectedStudyModule.getCourseUnitsAndNestedChildrenCourseUnits());
        }
        if (selectedItem instanceof CourseUnit) {
            selectedCourses.add((CourseUnit) selectedItem);
        }
        addCourseUnitsToSelectedList(selectedCourses);
    }

    /**
     * Removes the selected courses from the listView. Then calls the method to
     * update the tree view based on the user's degree programme, so that the
     * courses that were removed from the listView will appear back in the tree
     * view.
     */
    private void removeCourseUnitsFromListView() {
        var selectedListItem = listView.getSelectionModel().getSelectedItem();
        ArrayList<CourseUnit> selectedCourses = new ArrayList<>();
        if (selectedListItem != null) {
            selectedCourses.add(selectedListItem);
        }

        if (selectedCourses == null || selectedCourses.isEmpty()) {
            return;
        }
        boolean listChanged = listView.getItems().removeAll(selectedCourses);
        if (!listChanged) {
            return;
        }
        updateTreeViewForDegreeProgramme(userDegreeProgramme);
    }

    /**
     * Saves the currently selected courses to the user's file by removing any
     * existing courses and replacing them with the courses that are on the list
     * of selected courses.
     *
     * @throws Exception If there is an error reading/writing the user's data to
     * file.
     */
    private void saveSelectedCourses() throws Exception {
        var selectedCourses = listView.getItems();
        if (selectedCourses == null) {
            System.out.println("No courses selected");
            return;
        }

        String studentNumber = Sisu.getAuthentication().getCurrentlyLoggedInStudentNumber();
        User userFromFile = UserReaderWriter.getUserData(studentNumber);
        // Remove existing courses associated with the user
        userFromFile.getCourses().clear();
        // Add newly selected courses to the user
        for (int i = 0; i < selectedCourses.size(); i++) {
            CourseUnit course = new CourseUnit(selectedCourses.get(i).getName(),
                    selectedCourses.get(i).getId(), selectedCourses.get(i).getGroupId(),
                    selectedCourses.get(i).getMinCredits(), selectedCourses.get(i).getCode(),
                    selectedCourses.get(i).getDescription(), selectedCourses.get(i).getOutcomes());
            course.setCompleted(selectedCourses.get(i).isCompleted());
            UserReaderWriter userReaderWriter = new UserReaderWriter(null);
            userReaderWriter.addCourseToUser(course, userFromFile);
        }
    }

    /**
     * Handles the button click event for adding courses from the tree view to
     * the list view.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there is an error adding the course(s).
     * @throws Exception If there is an error adding the course(s).
     */
    @FXML
    private void handleAddCourseButtonClick(ActionEvent event) throws IOException, Exception {
        addSelectedTreeCourses();
    }

    /**
     * Handles the button click event for removing courses from the list view.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there is an error removing the course(s).
     * @throws Exception If there is an error removing the course(s).
     */
    @FXML
    private void handleRemoveCourseButtonClick(ActionEvent event) throws IOException, Exception {
        removeCourseUnitsFromListView();
    }

    /**
     * Handles the button click event for saving the courses from the list view
     * to the user's file.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there is an error removing the course(s).
     * @throws Exception If there is an error reading/writing the user's data to
     * file.
     */
    @FXML
    private void handleSaveCourseSelectionsButtonClick(ActionEvent event) throws IOException, Exception {
        saveSelectedCourses();
    }
}
