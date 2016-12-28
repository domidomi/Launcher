package dominika.launcher.AppsByCategory;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Domi on 30.11.2016.
 *
 * Model to store list of apps which belong to certain category.
 * So for category GAMES we will pass list: "strategy, adventure.." etc.
 */

public class CategoryAppsModel {

    ArrayList<List<String>> listOfCategories = new ArrayList<>();

    static String[] categories = {"Games", "Hobby", "Multimedia", "Social", "Utility", "Other"};

    static String[] games = {"Action", "Adventure", "Arcade", "Board", "Card", "Casino", "Casual", "Educational", "Music", "Puzzle", "Racing", "Role Playing", "Simulation", "Sports", "Strategy", "Trivia", "Word"};
    static String[] hobby = {"Art & Design", "Beauty", "Books & Reference", "Comics", "Bussiness", "Auto & Vehicles", "Education", "Entertainment", "Events", "Travel & Local"};
    static String[] multimedia = {"Music & Audio", "Personalization", "Photography", "Productivity", "Entertainment"};
    static String[] social = {"Social", "Lifestyle", "Communication", "Dating" };
    static String[] utility  = {"Finance", "Maps & Navigation", "Medical", "News & Magazines", "Parenting", "Personalization", "Productivity", "Shopping", "Tools"};
    static String[] other = {"Other"};

    public CategoryAppsModel() {

        /*
        * Filling a list of each category
        * */

        listOfCategories.add(Arrays.asList(games));
        listOfCategories.add(Arrays.asList(hobby));
        listOfCategories.add(Arrays.asList(multimedia));
        listOfCategories.add(Arrays.asList(social));
        listOfCategories.add(Arrays.asList(utility));
        listOfCategories.add(Arrays.asList(other));
    }

    public ArrayList<List<String>> getListOfCategories() {
        return listOfCategories;
    }

    public void showListOfCategories() {
        for (int i=0; i < listOfCategories.size(); i++) {
            Log.d("Kategoria:", listOfCategories.get(i).toString());
        }
    }

    public String[] getCategoriesNames()
    {
        return categories;
    }

    public int getSize() {
        return listOfCategories.size();
    }
}
