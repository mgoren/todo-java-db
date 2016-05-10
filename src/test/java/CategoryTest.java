import java.util.List;
import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.Arrays;

public class CategoryTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Category_instantiatesCorrectly_true() {
    Category myCategory = new Category("Household chores");
    assertEquals(true, myCategory instanceof Category);
  }

  @Test
  public void getName_categoryInstantiatesWithName_String() {
    Category myCategory = new Category("Household chores");
    assertEquals("Household chores", myCategory.getName());
  }

  @Test
  public void all_emptyAtFirst_0() {
    assertEquals(0, Category.all().size());
  }

  @Test
  public void equals_returnsTrueIfNamesAretheSame_true() {
    Category firstCategory = new Category("Household chores");
    Category secondCategory = new Category("Household chores");
    assertTrue(firstCategory.equals(secondCategory));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    assertTrue(Category.all().get(0).equals(myCategory));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Category savedCategory = Category.all().get(0);
    assertEquals(myCategory.getId(), savedCategory.getId());
  }

  @Test
  public void find_findCategoryInDatabase_true() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Category savedCategory = Category.find(myCategory.getId());
    assertTrue(myCategory.equals(savedCategory));
  }

  @Test
  public void addTask_addsTaskToCategory_true() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Task myTask = new Task("Mow the lawn");
    myTask.save();
    myCategory.addTask(myTask);
    Task savedTask = myCategory.getTasks().get(0);
    assertTrue(myTask.equals(savedTask));
  }

  @Test
  public void getTasks_returnsAllTasks_List() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Task myTask = new Task("Mow the lawn");
    myTask.save();
    myCategory.addTask(myTask);
    List savedTasks = myCategory.getTasks();
    assertEquals(1, savedTasks.size());
  }

  @Test
  public void delete_deletesAllTasksAndCategoriesAssociations() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Task myTask = new Task("Mow the lawn");
    myTask.save();
    myCategory.addTask(myTask);
    myCategory.delete();
    assertEquals(0, myTask.getCategories().size());
  }

  @Test
  public void update_updatesCategoryName_true() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    myCategory.update("Fun things");
    assertEquals("Fun things", Category.find(myCategory.getId()).getName());
  }

  @Test
  public void delete_deletesAllCategoriesAndTasksAssociations() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Task myTask = new Task("Mow the lawn");
    myTask.save();
    myCategory.addTask(myTask);
    myCategory.delete();
    assertEquals(0, myCategory.all().size());
    assertEquals(0, myTask.getCategories().size());
  }

}
