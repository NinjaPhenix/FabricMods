package ninjaphenix.expandedstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.common.Const;
import ninjaphenix.expandedstorage.common.ModContent;
import ninjaphenix.expandedstorage.common.Registries;
import ninjaphenix.expandedstorage.common.block.CursedChestBlock;
import ninjaphenix.expandedstorage.common.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.common.misc.CursedChestType;

public final class CursedChestBlockEntityRenderer implements BlockEntityRenderer<CursedChestBlockEntity>
{
    private static final BlockState defaultState = ModContent.DIAMOND_CHEST.defaultBlockState();

    private final ModelPart singleBottom, singleLid, singleLock;
    private final ModelPart vanillaLeftBottom, vanillaLeftLid, vanillaLeftLock;
    private final ModelPart vanillaRightBottom, vanillaRightLid, vanillaRightLock;
    private final ModelPart tallTopBottom, tallTopLid, tallTopLock;
    private final ModelPart tallBottomBottom;
    private final ModelPart longFrontBottom, longFrontLid, longFrontLock;
    private final ModelPart longBackBottom, longBackLid;

    public CursedChestBlockEntityRenderer(final Context context)
    {
        ChestRenderer.createDoubleBodyLeftLayer();
        final ModelPart single = context.bakeLayer(Const.SINGLE_LAYER);
        singleBottom = single.getChild("bottom");
        singleLid = single.getChild("lid");
        singleLock = single.getChild("lock");
        final ModelPart vanillaLeft = context.bakeLayer(Const.VANILLA_LEFT_LAYER);
        vanillaLeftBottom = vanillaLeft.getChild("bottom");
        vanillaLeftLid = vanillaLeft.getChild("lid");
        vanillaLeftLock = vanillaLeft.getChild("lock");
        final ModelPart vanillaRight = context.bakeLayer(Const.VANILLA_RIGHT_LAYER);
        vanillaRightBottom = vanillaRight.getChild("bottom");
        vanillaRightLid = vanillaRight.getChild("lid");
        vanillaRightLock = vanillaRight.getChild("lock");
        final ModelPart tallTop = context.bakeLayer(Const.TALL_TOP_LAYER);
        tallTopBottom = tallTop.getChild("bottom");
        tallTopLid = tallTop.getChild("lid");
        tallTopLock = tallTop.getChild("lock");
        final ModelPart tallBottom = context.bakeLayer(Const.TALL_BOTTOM_LAYER);
        tallBottomBottom = tallBottom.getChild("bottom");
        final ModelPart longFront = context.bakeLayer(Const.LONG_FRONT_LAYER);
        longFrontBottom = longFront.getChild("bottom");
        longFrontLid = longFront.getChild("lid");
        longFrontLock = longFront.getChild("lock");
        final ModelPart longBack = context.bakeLayer(Const.LONG_BACK_LAYER);
        longBackBottom = longBack.getChild("bottom");
        longBackLid = longBack.getChild("lid");
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(final CursedChestBlockEntity blockEntity, final float tickDelta, final PoseStack stack,
                       final MultiBufferSource vertexConsumerProvider, final int light, final int overlay)
    {
        final boolean hasLevel = blockEntity.hasLevel();
        final BlockState state = hasLevel ? blockEntity.getBlockState() : defaultState;
        final CursedChestType chestType = state.hasProperty(CursedChestBlock.TYPE) ? state.getValue(CursedChestBlock.TYPE) : CursedChestType.SINGLE;
        final Block block = state.getBlock();
        if (block instanceof CursedChestBlock)
        {
            stack.pushPose();
            stack.translate(0.5D, 0.5D, 0.5D);
            stack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
            stack.translate(-0.5D, -0.5D, -0.5D);
            final VertexConsumer consumer = new Material(Sheets.CHEST_SHEET,
                                                         Registries.CHEST.get(blockEntity.getBlock()).getChestTexture(chestType))
                    .buffer(vertexConsumerProvider, RenderType::entityCutout);
            final float openNess = blockEntity.getOpenNess(tickDelta);
            final NeighborCombineResult<? extends CursedChestBlockEntity> wrapper = hasLevel ?
                    ((CursedChestBlock) state.getBlock()).combine(state, blockEntity.getLevel(), blockEntity.getBlockPos(), true) :
                    Combiner::acceptNone;
            final int brightness = wrapper.apply(new BrightnessCombiner<>()).applyAsInt(light);
            switch (chestType)
            {
                case SINGLE:
                    render(stack, consumer, singleBottom, singleLid, singleLock, openNess, brightness, overlay);
                    break;
                case TOP:
                    render(stack, consumer, tallTopBottom, tallTopLid, tallTopLock, openNess, brightness, overlay);
                    break;
                case BOTTOM:
                    render(stack, consumer, tallBottomBottom, null, null, openNess, brightness, overlay);
                    break;
                case FRONT:
                    render(stack, consumer, longFrontBottom, longFrontLid, longFrontLock, openNess, brightness, overlay);
                    break;
                case BACK:
                    render(stack, consumer, longBackBottom, longBackLid, null, openNess, brightness, overlay);
                    break;
                case LEFT:
                    render(stack, consumer, vanillaLeftBottom, vanillaLeftLid, vanillaLeftLock, openNess, brightness, overlay);
                    break;
                case RIGHT:
                    render(stack, consumer, vanillaRightBottom, vanillaRightLid, vanillaRightLock, openNess, brightness, overlay);
                    break;
            }
            stack.popPose();
        }
    }

    private void render(final PoseStack stack, final VertexConsumer consumer, final ModelPart bottom, final ModelPart lid,
                        final ModelPart lock, float openNess, final int brightness, int overlay)
    {
        if (lid != null)
        {
            openNess = 1 - openNess;
            openNess = 1 - openNess * openNess * openNess;
            lid.xRot = -openNess * 1.5707964F;
            lid.render(stack, consumer, brightness, overlay);
            if (lock != null)
            {
                lock.xRot = lid.xRot;
                lock.render(stack, consumer, brightness, overlay);
            }
        }
        bottom.render(stack, consumer, brightness, overlay);
    }

    public static LayerDefinition createSingleBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 14, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 15, 2, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createVanillaLeftBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 15, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 15, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15, -1, 15, 1, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createVanillaRightBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0, 0, 1, 15, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 15, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0, -1, 15, 1, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createTallTopBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 14, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 15, 2, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createTallBottomBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 1, 14, 16, 14), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public static LayerDefinition createLongFrontBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 20).addBox(1, 0, 0, 14, 10, 15), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 15, 14, 5, 15), PartPose.offset(0, 9, -15));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 31, 2, 4, 1), PartPose.offset(0, 8, -16));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createLongBackBodyLayer()
    {
        final MeshDefinition meshDefinition = new MeshDefinition();
        final PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 20).addBox(1, 0, 1, 14, 10, 15), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 15), PartPose.offset(0, 9, 1));
        return LayerDefinition.create(meshDefinition, 48, 48);
    }
}