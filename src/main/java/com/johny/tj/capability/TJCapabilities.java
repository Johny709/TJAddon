package com.johny.tj.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class TJCapabilities {

    @CapabilityInject(IMultipleWorkable.class)
    public static Capability<IMultipleWorkable> CAPABILITY_MULTIPLE_WORKABLE = null;

    @CapabilityInject(IMultiControllable.class)
    public static Capability<IMultiControllable> CAPABILITY_MULTI_CONTROLLABLE = null;

    @CapabilityInject(IParallelController.class)
    public static Capability<IParallelController> CAPABILITY_PARALLEL_CONTROLLER = null;

    @CapabilityInject(LinkPos.class)
    public static Capability<LinkPos> CAPABILITY_LINK_POS = null;

    @CapabilityInject(LinkPosInterDim.class)
    public static Capability<LinkPosInterDim> CAPABILITY_LINK_POS_INTERDIM = null;

    @CapabilityInject(LinkEntity.class)
    public static Capability<LinkEntity> CAPABILITY_LINK_ENTITY = null;

    @CapabilityInject(LinkEntityInterDim.class)
    public static Capability<LinkPosInterDim> CAPABILITY_LINK_ENTITY_INTERDIM = null;
}
