package ml.ayushdhanai.foodguide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends PagerAdapter{

    List<Model> models;
    Context context;
    LayoutInflater layoutInflater;

    public MyAdapter(List<Model> models,Context context) {
        this.models = models;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.card_item,container,false);

        ImageView imageView;
        TextView title,ingredients,recipe;

        title = (TextView) view.findViewById(R.id.title);
        ingredients = (TextView) view.findViewById(R.id.ingredients);
        recipe = (TextView) view.findViewById(R.id.recipe);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        ingredients.setMovementMethod(new ScrollingMovementMethod());
        recipe.setMovementMethod(new ScrollingMovementMethod());

        imageView.setImageResource(models.get(position).getImage());

        title.setText(models.get(position).getFoodName());
        ingredients.setText(models.get(position).getIngredients());
        recipe.setText(models.get(position).getRecipe());
        container.addView(view,0);
        return view;
    }
}
