package ws.zettabyte.zettalib.recipe;

import java.util.ArrayList;

import net.minecraft.item.crafting.IRecipe;

public interface IRecipeProvider {
	ArrayList<IRecipe> getRecipes();
}
