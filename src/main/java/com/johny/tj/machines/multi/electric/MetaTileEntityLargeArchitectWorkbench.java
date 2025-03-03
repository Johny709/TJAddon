package com.johny.tj.machines.multi.electric;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import com.johny.tj.TJConfig;
import com.johny.tj.builder.multicontrollers.TJLargeSimpleRecipeMapMultiblockController;
import gregicadditions.capabilities.GregicAdditionsCapabilities;
import gregicadditions.item.components.ConveyorCasing;
import gregicadditions.item.components.RobotArmCasing;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class MetaTileEntityLargeArchitectWorkbench extends TJLargeSimpleRecipeMapMultiblockController {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.INPUT_ENERGY, GregicAdditionsCapabilities.MAINTENANCE_HATCH};
    private int slices = 1;

    public MetaTileEntityLargeArchitectWorkbench(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap, TJConfig.largeArchitectWorkbench.eutPercentage, TJConfig.largeArchitectWorkbench.durationPercentage, TJConfig.largeArchitectWorkbench.chancePercentage, TJConfig.largeArchitectWorkbench.stack);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        FactoryBlockPattern factoryPattern = FactoryBlockPattern.start(BlockPattern.RelativeDirection.LEFT, BlockPattern.RelativeDirection.DOWN, BlockPattern.RelativeDirection.BACK);

        for (int count = 0; count < this.slices; count++) {
            factoryPattern.aisle("~~~", "~~~", "HHH", "HHH");
            factoryPattern.aisle("CrC", "C#C", "HcH", "HHH");
        }
        factoryPattern.aisle("~~~", "~~~","HSH", "HHH")
                .where('S', selfPredicate())
                .where('C', statePredicate(getCasingState()))
                .where('H', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
                .where('c', conveyorPredicate())
                .where('r', robotArmPredicate())
                .where('#', isAirPredicate())
                .where('~', (tile) -> true);
        return factoryPattern.build();
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        ConveyorCasing.CasingType conveyor = context.getOrDefault("Conveyor", ConveyorCasing.CasingType.CONVEYOR_LV);
        RobotArmCasing.CasingType robotArm = context.getOrDefault("RobotArm", RobotArmCasing.CasingType.ROBOT_ARM_LV);
        int min = Math.min(conveyor.getTier(), robotArm.getTier());
        maxVoltage = (long) (Math.pow(4, min) * 8);
    }

    @Override
    protected void checkStructurePattern() {
        if (getWorld() == null)
            return;
        if (this.structurePattern == null)
            this.structurePattern = createStructurePattern();
        super.checkStructurePattern();
    }

    @Override
    protected void reinitializeStructurePattern() {
        this.structurePattern = null;
    }

    public void resetStructure() {
        this.invalidateStructure();
        this.structurePattern = createStructurePattern();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityLargeArchitectWorkbench(metaTileEntityId, recipeMap);
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (playerIn.getHeldItemMainhand().isItemEqual(MetaItems.SCREWDRIVER.getStackForm()))
            return false;
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!getWorld().isRemote) {
            if (!playerIn.isSneaking()) {
                if (this.slices < TJConfig.largeArchitectWorkbench.maximumSlices) {
                    this.slices++;
                    playerIn.sendMessage(new TextComponentTranslation("tj.multiblock.industrial_fusion_reactor.message.1").appendSibling(new TextComponentString(" " + this.slices)));
                } else {
                    playerIn.sendMessage(new TextComponentTranslation("tj.multiblock.industrial_fusion_reactor.message.4").appendSibling(new TextComponentString(" " + this.slices)));
                }
            } else {
                if (this.slices > 1) {
                    this.slices--;
                    playerIn.sendMessage(new TextComponentTranslation("tj.multiblock.industrial_fusion_reactor.message.2").appendSibling(new TextComponentString(" " + this.slices)));
                } else
                    playerIn.sendMessage(new TextComponentTranslation("tj.multiblock.industrial_fusion_reactor.message.3").appendSibling(new TextComponentString(" " + this.slices)));
            }
            this.resetStructure();
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("Slices", this.slices);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.slices = data.getInteger("Slices");
    }
}
