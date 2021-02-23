package ninjaphenix.expandedstorage.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.client.models.*;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

public final class CursedChestBlockEntityRenderer extends BlockEntityRenderer<CursedChestBlockEntity>
{
    private static final BlockState defaultState = ModContent.DIAMOND_CHEST.defaultBlockState();

    private static final ImmutableMap<CursedChestType, SingleChestModel> MODELS = new ImmutableMap.Builder<CursedChestType, SingleChestModel>()
            .put(CursedChestType.SINGLE, new SingleChestModel())
            .put(CursedChestType.FRONT, new FrontChestModel())
            .put(CursedChestType.BACK, new BackChestModel())
            .put(CursedChestType.TOP, new TopChestModel())
            .put(CursedChestType.BOTTOM, new BottomChestModel())
            .put(CursedChestType.LEFT, new LeftChestModel())
            .put(CursedChestType.RIGHT, new RightChestModel())
            .build();

    public CursedChestBlockEntityRenderer(final BlockEntityRenderDispatcher dispatcher) { super(dispatcher); }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(final CursedChestBlockEntity blockEntity, final float tickDelta, final PoseStack stack,
                       final MultiBufferSource vertexConsumerProvider, final int light, final int overlay)
    {
        final BlockState state = blockEntity.hasLevel() ? blockEntity.getBlockState() : defaultState;
        final CursedChestType chestType = state.getValue(CursedChestBlock.TYPE);
        final SingleChestModel model = getModel(chestType);
        stack.pushPose();
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        stack.translate(-0.5D, -0.5D, -0.5D);
        model.setLidPitch(blockEntity.getLidOpenness(tickDelta));

        final DoubleBlockCombiner.NeighborCombineResult<? extends CursedChestBlockEntity> wrapper = blockEntity.hasLevel() ?
                ((CursedChestBlock) state.getBlock()).combine(state, blockEntity.getLevel(), blockEntity.getBlockPos(), true) :
                DoubleBlockCombiner.Combiner::acceptNone;
        model.render(stack, new Material(Sheets.CHEST_SHEET,
                                         Registries.CHEST.get(blockEntity.getBlock()).getChestTexture(chestType))
                             .buffer(vertexConsumerProvider, RenderType::entityCutout),
                     wrapper.apply(new BrightnessCombiner<>()).applyAsInt(light), overlay);
        stack.popPose();
    }

    public SingleChestModel getModel(final CursedChestType type) { return MODELS.get(type); }
}