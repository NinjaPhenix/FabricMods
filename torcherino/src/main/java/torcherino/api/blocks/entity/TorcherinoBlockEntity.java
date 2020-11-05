package torcherino.api.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.chainmail.api.blockentity.ExpandedBlockEntity;
import torcherino.api.Tier;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;

public class TorcherinoBlockEntity extends BlockEntity implements Nameable, TierSupplier, ExpandedBlockEntity
{
    private static final String onlineMode = Config.INSTANCE.online_mode;
    public static int randomTicks;
    private Component customName;
    private int xRange, yRange, zRange, speed, redstoneMode;
    private Iterable<BlockPos> area;
    private boolean active;
    private ResourceLocation tierID;
    private String uuid = "";

    public TorcherinoBlockEntity(final BlockPos pos, final BlockState state)
    {
        super(Registry.BLOCK_ENTITY_TYPE.get(new ResourceLocation("torcherino", "torcherino")), pos, state);
    }

    @Override
    public boolean hasCustomName() { return customName != null; }

    @Override
    public Component getCustomName() { return customName; }

    public void setCustomName(Component name) { customName = name; }

    private String getOwner() { return uuid; }

    public void setOwner(String s) { uuid = s; }

    @Override
    public Component getName() { return hasCustomName() ? customName : new TranslatableComponent(getBlockState().getBlock().getDescriptionId()); }

    @Override
    public void onLoad()
    {
        if (level.isClientSide) { return; }
        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
        level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> getBlockState().neighborChanged(level, worldPosition, null, null, false)));
    }

    // todo: move this to block code
    //@Override
    //public void tick()
    //{
    //    if (!active || speed == 0 || (xRange == 0 && yRange == 0 && zRange == 0)) { return; }
    //    if (!onlineMode.equals("") && !Torcherino.hasIsOnline(getOwner())) { return; }
    //    randomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
    //    area.forEach(this::tickBlock);
    //}

    private void tickBlock(final BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        final Block block = state.getBlock();
        if (TorcherinoAPI.INSTANCE.isBlockBlacklisted(block)) { return; }
        if (level instanceof ServerLevel && block.isRandomlyTicking(state) &&
                level.getRandom().nextInt(Mth.clamp(4096 / (speed * Config.INSTANCE.random_tick_rate), 1, 4096)) < randomTicks)
        {
            state.randomTick((ServerLevel) level, pos, level.getRandom());
        }
        // todo: can this be cached
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isRemoved())
        {
            final BlockEntityType<BlockEntity> type = (BlockEntityType<BlockEntity>) blockEntity.getType();
            if(!TorcherinoAPI.INSTANCE.isBlockEntityBlacklisted(type))
            {
                final BlockEntityTicker<BlockEntity> ticker = state.getTicker(level, type);
                if (ticker != null)
                {
                    for (int i = 0; i < speed; i++)
                    {
                        if (blockEntity.isRemoved()) { break; }
                        ticker.tick(level, pos, state, blockEntity);
                    }
                }
            }

        }
    }

    public void writeClientData(final FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(worldPosition);
        buffer.writeComponent(getName());
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }

    public void readClientData(final FriendlyByteBuf buffer)
    {
        Tier tier = TorcherinoAPI.INSTANCE.getTiers().get(getTier());
        this.xRange = Mth.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.zRange = Mth.clamp(buffer.readInt(), 0, tier.getXZRange());
        this.yRange = Mth.clamp(buffer.readInt(), 0, tier.getYRange());
        this.speed = Mth.clamp(buffer.readInt(), 1, tier.getMaxSpeed());
        this.redstoneMode = Mth.clamp(buffer.readInt(), 0, 3);

        area = BlockPos.betweenClosed(worldPosition.getX() - xRange, worldPosition.getY() - yRange, worldPosition.getZ() - zRange,
                worldPosition.getX() + xRange, worldPosition.getY() + yRange, worldPosition.getZ() + zRange);
    }

    @Override
    public ResourceLocation getTier()
    {
        if (tierID == null)
        {
            Block block = getBlockState().getBlock();
            if (block instanceof TierSupplier) { tierID = ((TierSupplier) block).getTier(); }
        }
        return tierID;
    }

    public void setPoweredByRedstone(final boolean powered)
    {
        switch (redstoneMode)
        {
            case 0:
                active = !powered;
                break;
            case 1:
                active = powered;
                break;
            case 2:
                active = true;
                break;
            case 3:
                active = false;
                break;
        }
    }

    @Override
    public CompoundTag save(final CompoundTag tag)
    {
        super.save(tag);
        if (hasCustomName()) { tag.putString("CustomName", Component.Serializer.toJson(getCustomName())); }
        tag.putInt("XRange", xRange);
        tag.putInt("ZRange", zRange);
        tag.putInt("YRange", yRange);
        tag.putInt("Speed", speed);
        tag.putInt("RedstoneMode", redstoneMode);
        tag.putBoolean("Active", active);
        tag.putString("Owner", getOwner() == null ? "" : getOwner());
        return tag;
    }

    @Override
    public void load(final CompoundTag tag)
    {
        super.load(tag);
        if (tag.contains("CustomName", 8)) { setCustomName(Component.Serializer.fromJson(tag.getString("CustomName"))); }
        xRange = tag.getInt("XRange");
        zRange = tag.getInt("ZRange");
        yRange = tag.getInt("YRange");
        speed = tag.getInt("Speed");
        redstoneMode = tag.getInt("RedstoneMode");
        active = tag.getBoolean("Active");
        uuid = tag.getString("Owner");
    }
}
