package ninjaphenix.expandedstorage.common.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import ninjaphenix.expandedstorage.common.block.BarrelBlock;
import ninjaphenix.expandedstorage.common.block.ChestBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ModifierItem extends Item
{
    public ModifierItem(final Properties settings) { super(settings); }

    @Override
    public InteractionResult useOn(final UseOnContext context)
    {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        if (block instanceof BarrelBlock) { return useModifierOnBlock(context, state, pos, BlockType.SINGLE); }
        else if (block instanceof ChestBlock) { return useModifierOnBlock(context, state, pos, ChestBlock.getBlockType(state)); }
        else { return useModifierOnBlock(context, state); }
    }

    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state, final BlockPos pos, final BlockType type)
    {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(final ItemStack stack, final Player player, final LivingEntity entity, final InteractionHand hand)
    {
        return useModifierOnEntity(stack, player, entity, hand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand)
    {
        final InteractionResultHolder<ItemStack> result = useModifierInAir(world, player, hand);
        if (result.getResult() == InteractionResult.SUCCESS) { player.getCooldowns().addCooldown(this, 5); }
        return result;
    }

    protected InteractionResult useModifierOnBlock(final UseOnContext context, final BlockState state)
    {
        return InteractionResult.PASS;
    }

    protected InteractionResult useModifierOnEntity(final ItemStack stack, final Player player, final LivingEntity entity, final InteractionHand hand)
    {
        return InteractionResult.PASS;
    }

    protected InteractionResultHolder<ItemStack> useModifierInAir(final Level world, final Player player, final InteractionHand hand)
    {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}