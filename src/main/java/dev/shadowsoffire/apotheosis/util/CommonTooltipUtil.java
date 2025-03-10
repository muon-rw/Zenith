package dev.shadowsoffire.apotheosis.util;

import com.google.common.base.Predicates;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.ench.api.IEnchantingBlock;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.IFormattableAttribute;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.Consumer;

public class CommonTooltipUtil {

    public static void appendBossData(Level level, LivingEntity entity, Consumer<Component> tooltip) {
        DynamicHolder<LootRarity> rarity = RarityRegistry.byLegacyId(ZenithComponents.BOSS_DATA.get(entity).getRarity());
        if (!rarity.isBound()) return;
        tooltip.accept(Component.translatable("info.zenith.boss", rarity.get().toComponent()).withStyle(ChatFormatting.GRAY));
        tooltip.accept(CommonComponents.EMPTY);
        tooltip.accept(Component.translatable("info.zenith.boss_modifiers").withStyle(ChatFormatting.GRAY));
        AttributeMap map = entity.getAttributes();
        BuiltInRegistries.ATTRIBUTE.stream().map(map::getInstance).filter(Predicates.notNull()).forEach(inst -> {
            for (AttributeModifier modif : inst.getModifiers()) {
                if (modif.getName().startsWith("placebo_random_modifier_")) {
                    tooltip.accept(IFormattableAttribute.toComponent(inst.getAttribute(), modif, AttributesLib.getTooltipFlag()));
                }
            }
        });
    }

    public static void appendBlockStats(Level world, BlockState state, BlockPos pos, Consumer<Component> tooltip) {
        float maxEterna = EnchantingStatRegistry.getMaxEterna(state, world, pos);
        float eterna = EnchantingStatRegistry.getEterna(state, world, pos);
        float quanta = EnchantingStatRegistry.getQuanta(state, world, pos);
        float arcana = EnchantingStatRegistry.getArcana(state, world, pos);
        float rectification = EnchantingStatRegistry.getQuantaRectification(state, world, pos);
        int clues = EnchantingStatRegistry.getBonusClues(state, world, pos);
        boolean treasure = ((IEnchantingBlock) state.getBlock()).allowsTreasure(state, world, pos);
        if (eterna != 0 || quanta != 0 || arcana != 0 || rectification != 0 || clues != 0) {
            tooltip.accept(Component.translatable("info.zenith.ench_stats").withStyle(ChatFormatting.GOLD));
        }
        if (eterna != 0) {
            if (eterna > 0) {
                tooltip.accept(Component.translatable("info.zenith.eterna.p", String.format("%.2f", eterna), String.format("%.2f", maxEterna)).withStyle(ChatFormatting.GREEN));
            }
            else tooltip.accept(Component.translatable("info.zenith.eterna", String.format("%.2f", eterna)).withStyle(ChatFormatting.GREEN));
        }
        if (quanta != 0) {
            tooltip.accept(Component.translatable("info.zenith.quanta" + (quanta > 0 ? ".p" : ""), String.format("%.2f", quanta)).withStyle(ChatFormatting.RED));
        }
        if (arcana != 0) {
            tooltip.accept(Component.translatable("info.zenith.arcana" + (arcana > 0 ? ".p" : ""), String.format("%.2f", arcana)).withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (rectification != 0) {
            tooltip.accept(Component.translatable("info.zenith.rectification" + (rectification > 0 ? ".p" : ""), String.format("%.2f", rectification)).withStyle(ChatFormatting.YELLOW));
        }
        if (clues != 0) {
            tooltip.accept(Component.translatable("info.zenith.clues" + (clues > 0 ? ".p" : ""), String.format("%d", clues)).withStyle(ChatFormatting.DARK_AQUA));
        }
        if (treasure) {
            tooltip.accept(Component.translatable("info.zenith.allows_treasure").withStyle(ChatFormatting.GOLD));
        }
        Set<Enchantment> blacklist = ((IEnchantingBlock) state.getBlock()).getBlacklistedEnchantments(state, world, pos);
        if (blacklist.size() > 0) {
            tooltip.accept(Component.translatable("info.zenith.filter").withStyle(s -> s.withColor(0x58B0CC)));
            for (Enchantment e : blacklist) {
                MutableComponent name = (MutableComponent) e.getFullname(1);
                name.getSiblings().clear();
                name.withStyle(s -> s.withColor(0x5878AA));
                tooltip.accept(Component.literal(" - ").append(name).withStyle(s -> s.withColor(0x5878AA)));
            }
        }
    }

    public static void appendTableStats(Level world, BlockPos pos, Consumer<Component> tooltip) {
        ApothEnchantmentMenu.TableStats stats = ApothEnchantmentMenu.gatherStats(world, pos, 0);
        tooltip.accept(Component.translatable("info.zenith.eterna.t", String.format("%.2f", stats.eterna()), String.format("%.2f", EnchantingStatRegistry.getAbsoluteMaxEterna())).withStyle(ChatFormatting.GREEN));
        tooltip.accept(Component.translatable("info.zenith.quanta.t", String.format("%.2f", Math.min(100, stats.quanta()))).withStyle(ChatFormatting.RED));
        tooltip.accept(Component.translatable("info.zenith.arcana.t", String.format("%.2f", Math.min(100, stats.arcana()))).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.accept(Component.translatable("info.zenith.rectification.t", String.format("%.2f", Mth.clamp(stats.rectification(), -100, 100))).withStyle(ChatFormatting.YELLOW));
        tooltip.accept(Component.translatable("info.zenith.clues.t", String.format("%d", stats.clues())).withStyle(ChatFormatting.DARK_AQUA));
    }

}
