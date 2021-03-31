package test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Main implements ModInitializer
{
    public void onInitialize() {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        GameTestRegistry.register(Main.class);
    }

    @GameTest
    public void notepad_test(GameTestHelper helper) {
        BlockPos chestPos = new BlockPos(2, 2, 2);
        System.out.println("RUNNING GAME TEST");
        helper.startSequence()
                .thenIdle(20)
                .thenExecute(() -> helper.useBlock(chestPos))
                .thenExecuteAfter(40, () -> {
                    BlockEntity entity = helper.getBlockEntity(chestPos);
                    if (entity instanceof ChestViewCountAccessor) {
                        if(((ChestViewCountAccessor) entity).getNumberOfLookingPlayers() != 1) {
                            throw new GameTestAssertException("OPEN CHEST TEST FAILED");
                        }
                    } else {
                        throw new GameTestAssertException("OPEN CHEST TEST FAILED - ChestViewCountAccessor failed to apply?");
                    }
                }).thenSucceed();
    }
}