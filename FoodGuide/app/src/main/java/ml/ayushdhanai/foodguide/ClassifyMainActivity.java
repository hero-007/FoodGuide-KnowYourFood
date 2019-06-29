package ml.ayushdhanai.foodguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassifyMainActivity extends AppCompatActivity {

    List<Model> models = new ArrayList<>();
    MyAdapter adapter;
    HorizontalInfiniteCycleViewPager pager;
    String title,food_ingredients,food_recipe,category;



    HashMap<Integer, Integer> imagenumber= new HashMap<>();
    // Write a message to the database

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_activity_main);



        Intent x = getIntent();
        title = x.getStringExtra("title");
        food_ingredients = x.getStringExtra("food_ingredients");
        food_recipe = x.getStringExtra("food_recipe");
        category= x.getStringExtra("category");

        initData();
        pager = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle);
        adapter = new MyAdapter(models, getBaseContext());
        pager.setAdapter(adapter);


        pager.setBackgroundResource(R.drawable.first_layer);


    }


    private void initData() {



        imagenumber.put(0, R.drawable.apple_pie);
        imagenumber.put(1, R.drawable.apple_pie);
        imagenumber.put(3, R.drawable.baklava);
        imagenumber.put(11, R.drawable.bruschetta);
        imagenumber.put(93, R.drawable.spring_roll);
        imagenumber.put(101, R.drawable.waffles);
        imagenumber.put(96, R.drawable.sushi);
        imagenumber.put(94, R.drawable.steak);
        imagenumber.put(97, R.drawable.tacos);
        imagenumber.put(32, R.drawable.donuts);
        imagenumber.put(33, R.drawable.dumplings);
        imagenumber.put(47, R.drawable.garlicbread);
        imagenumber.put(41, R.drawable.frenchfries);
        imagenumber.put(45, R.drawable.friedrice);
        imagenumber.put(50, R.drawable.grilledcheesesandwich);
        imagenumber.put(54, R.drawable.hamburger);
        imagenumber.put(56, R.drawable.hotdog);
        imagenumber.put(30, R.drawable.cupcakes);
        imagenumber.put(21, R.drawable.chickenwings);
        imagenumber.put(22, R.drawable.chocolatecake);
        imagenumber.put(92, R.drawable.spaghetticarbonara);

        int val=R.drawable.apple_pie;
        try{
            Log.e("Category Data : ",""+category);
            Log.e("Category Data : ",""+Integer.parseInt(category));

            val = imagenumber.get(Integer.parseInt(category));
            Log.e("Category Data : ",""+val);
        }
        catch (NullPointerException e)
        {
            Toast.makeText(this, "Null Pointer Exception", Toast.LENGTH_SHORT).show();
        }

        //Display
        models.add(new Model(val, title, food_ingredients, food_recipe));


    }


}
