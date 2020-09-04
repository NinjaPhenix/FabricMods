package ninjaphenix.expandedstorage.common.block;

import javax.annotation.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ninjaphenix.expandedstorage.common.ExpandedStorage;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.entity.StorageBlockEntity;

public abstract class StorageBlock extends BaseEntityBlock implements WorldlyContainerHolder
{
    public final ResourceLocation TIER_ID;

    protected StorageBlock(final Properties settings, final ResourceLocation tierId)
    {
        super(settings);
        TIER_ID = tierId;
    }

    protected abstract ResourceLocation getOpenStat();

    public abstract <R extends Registries.TierData> MappedRegistry<R> getDataRegistry();

    protected ExtendedScreenHandlerFactory createContainerFactory(final BlockState state, final LevelAccessor world, final BlockPos pos)
    {
        final BlockEntity entity = world.getBlockEntity(pos);
        if(!(entity instanceof StorageBlockEntity)) { return null; }
        final StorageBlockEntity container = (StorageBlockEntity) entity;
        return new ExtendedScreenHandlerFactory()
        {
            @Override
            public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buffer)
            {
                buffer.writeBlockPos(pos).writeInt(container.getContainerSize());
            }

            @Override
            public Component getDisplayName()
            {
                return container.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(final int syncId, final Inventory inv, final Player player)
            {
                if (container.stillValid(player))
                {
                    container.unpackLootTable(player);
                    return ExpandedStorage.INSTANCE.getScreenHandler(syncId, container.getBlockPos(), container, player, getDisplayName());
                }
                return null;
            }
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public final boolean hasAnalogOutputSignal(final BlockState state) { return true; }

    @Override
    public RenderShape getRenderShape(final BlockState state) { return RenderShape.MODEL; }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(final BlockState state, final Level world, final BlockPos pos, final BlockState newState,
                                final boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container)
            {
                Containers.dropContents(world, pos, (Container) blockEntity);
                world.updateNeighborsAt(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState state, final Level world, final BlockPos pos)
    {
        return null;
    }

    @Override
    public void setPlacedBy(final Level world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer,
                         final ItemStack stack)
    {
        if (stack.hasCustomHoverName())
        {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageBlockEntity)
            {
                ((StorageBlockEntity) blockEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(final BlockState state, final Level world, final BlockPos pos)
    {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState state, final Level world, final BlockPos pos, final Player player, final InteractionHand hand,
                              final BlockHitResult hit)
    {
        if (!world.isClientSide)
        {
            final ExtendedScreenHandlerFactory factory = createContainerFactory(state, world, pos);
            if (factory != null)
            {
                ExpandedStorage.INSTANCE.openContainer(player, factory);
                player.awardStat(getOpenStat());
                PiglinAi.angerNearbyPiglins(player, true);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public WorldlyContainer getContainer(final BlockState state, final LevelAccessor world, final BlockPos pos)
    {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof StorageBlockEntity) { return (StorageBlockEntity) entity; }
        return null;
    }
}