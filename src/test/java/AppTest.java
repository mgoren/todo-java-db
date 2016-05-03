import org.sql2o.*; // ADDED FOR DB VERSION
import org.junit.*; // ADDED FOR DB VERSION
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.junit.Assert.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  // ADDED BEFORE AND AFTER SECTIONS FOR DB VERSION
  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/to_do_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteTasksQuery = "DELETE FROM tasks *;";
      String deleteCategoriesQuery = "DELETE FROM categories *;";
      con.createQuery(deleteTasksQuery).executeUpdate();
      con.createQuery(deleteCategoriesQuery).executeUpdate();
    }
  }

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Todo list!");
    assertThat(pageSource()).contains("View Category List");
    assertThat(pageSource()).contains("Add a New Category");
  }

  @Test
  public void categoryIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Add a New Category"));
    fill("#name").with("Household chores");
    submit(".btn");
    assertThat(pageSource()).contains("Your category has been saved.");
  }

  @Test
  public void categoryIsDisplayedTest() {
    goTo("http://localhost:4567/categories/new");
    fill("#name").with("Household chores");
    submit(".btn");
    click("a", withText("View categories"));
    assertThat(pageSource()).contains("Household chores");
  }

  @Test
  public void categoryShowPageDisplaysName() {
    goTo("http://localhost:4567/categories/new");
    fill("#name").with("Household chores");
    submit(".btn");
    click("a", withText("View categories"));
    click("a", withText("Household chores"));
    assertThat(pageSource()).contains("Household chores");
  }

  @Test
  public void categoryTasksFormIsDisplayed() {
    goTo("http://localhost:4567/categories/new");
    fill("#name").with("Shopping");
    submit(".btn");
    click("a", withText("View categories"));
    click("a", withText("Shopping"));
    click("a", withText("Add a new task"));
    assertThat(pageSource()).contains("Add a task to Shopping");
  }

  @Test
  public void tasksIsAddedAndDisplayed() {
    goTo("http://localhost:4567/categories/new");
    fill("#name").with("Banking");
    submit(".btn");
    click("a", withText("View categories"));
    click("a", withText("Banking"));
    click("a", withText("Add a new task"));
    fill("#description").with("Deposit paycheck");
    submit(".btn");
    click("a", withText("View categories"));
    click("a", withText("Banking"));
    assertThat(pageSource()).contains("Deposit paycheck");
  }

  @Test
  public void taskShowPage() {
    Category myCategory = new Category("Home");
    myCategory.save();
    Task myTask = new Task("Clean", myCategory.getId());
    myTask.save();
    String categoryPath = String.format("http://localhost:4567/categories/%d", myCategory.getId());
    goTo(categoryPath);
    click("a", withText("Clean"));
    assertThat(pageSource()).contains("Clean");
    assertThat(pageSource()).contains("Return to Home");
  }

  @Test
  public void taskUpdate() {
    Category myCategory = new Category("Home");
    myCategory.save();
    Task myTask = new Task("Clean", myCategory.getId());
    myTask.save();
    String taskPath = String.format("http://localhost:4567/categories/%d/tasks/%d", myCategory.getId(), myTask.getId());
    goTo(taskPath);
    fill("#description").with("Dance");
    submit("#update-task");
    assertThat(pageSource()).contains("Dance");
  }

  @Test
  public void taskDelete() {
    Category myCategory = new Category("Home");
    myCategory.save();
    Task myTask = new Task("Clean", myCategory.getId());
    myTask.save();
    String taskPath = String.format("http://localhost:4567/categories/%d/tasks/%d", myCategory.getId(), myTask.getId());
    goTo(taskPath);
    submit("#delete-task");
    assertEquals(0, Task.all().size());
  }

}
