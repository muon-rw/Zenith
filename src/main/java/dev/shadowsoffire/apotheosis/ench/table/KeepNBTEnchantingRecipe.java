package dev.shadowsoffire.apotheosis.ench.table;

import com.google.gson.JsonObject;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry.Stats;
import io.github.fabricators_of_create.porting_lib.util.CraftingHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.tuple.Pair;

public class KeepNBTEnchantingRecipe extends EnchantingRecipe {

    public static final Serializer SERIALIZER = new Serializer();

    public KeepNBTEnchantingRecipe(ResourceLocation id, ItemStack output, Ingredient input, Stats requirements, Stats maxRequirements) {
        super(id, output, input, requirements, maxRequirements);
    }

    @Override
    public ItemStack assemble(ItemStack input, float eterna, float quanta, float arcana) {
        ItemStack out = this.getOutput().copy();
        if (input.hasTag()) out.setTag(input.getTag().copy());
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KeepNBTEnchantingRecipe.SERIALIZER;
    }

    public static class Serializer extends EnchantingRecipe.Serializer {

        @Override
        public KeepNBTEnchantingRecipe fromJson(ResourceLocation id, JsonObject obj) {
            ItemStack output = CraftingHelper.getItemStack(obj.get("result").getAsJsonObject(), true, true);
            Ingredient input = Ingredient.fromJson(obj.get("input"));
            Pair<Stats, Stats> requirements = readStats(id, obj);
            return new KeepNBTEnchantingRecipe(id, output, input, requirements.getLeft(), requirements.getRight());
        }

    }

}
