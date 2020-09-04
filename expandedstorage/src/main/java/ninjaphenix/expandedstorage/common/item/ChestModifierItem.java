package ninjaphenix.expandedstorage.common.item;

import ninjaphenix.expandedstorage.common.block.BarrelBlock;
import ninjaphenix.expandedstorage.common.block.ChestBlock;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class ChestModifierItem extends Item
{
    private static final EnumProperty<CursedChestType> TYPE = ChestBlock.TYPE;

    public ChestModifierItem(final Properties settings) { super(settings); }

    @Override
    public InteractionResult useOn(final UseOnContext context)
    {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BarrelBlock)
        {
            return useModifierOnBarrel(context, state, pos);
        }
        else if (state.getBlock() instanceof ChestBlock)
        {
            InteractionResult result = InteractionResult.FAIL;
            final CursedChestType type = state.getValue(TYPE);
            final Direction facing = state.getValue(HORIZONTAL_FACING);
            if (type == CursedChestType.SINGLE) { result = useModifierOnChestBlock(context, state, pos, null, null); }
            else if (type == CursedChestType.BOTTOM)
            {
                final BlockPos otherPos = pos.relative(Direction.UP);
                result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
            }
            else if (type == CursedChestType.TOP)
            {
                final BlockPos otherPos = pos.relative(Direction.DOWN);
                result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
            }
            else if (type == CursedChestType.LEFT)
            {
                final BlockPos otherPos = pos.relative(facing.getCounterClockWise());
                result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
            }
            else if (type == CursedChestType.RIGHT)
            {
                final BlockPos otherPos = pos.relative(facing.getClockWise());
                result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
            }
            else if (type == CursedChestType.FRONT)
            {
                final BlockPos otherPos = pos.relative(facing.getOpposite());
                result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
            }
            else if (type == CursedChestType.BACK)
            {
                final BlockPos otherPos = pos.relative(facing);
                result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
            }
            return result;
        }
        else { return useModifierOnBlock(context, state); }
    }

    protected InteractionResult useModifierOnBarrel(final UseOnContext context, final BlockState state, final BlockPos pos)
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

    protected InteractionResult useModifierOnChestBlock(final UseOnContext context, final BlockState mainState, final BlockPos mainBlockPos,
                                                   final BlockState otherState, final BlockPos otherBlockPos)
    {
        return InteractionResult.PASS;
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