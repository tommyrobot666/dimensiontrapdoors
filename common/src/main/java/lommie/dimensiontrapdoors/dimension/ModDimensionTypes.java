package lommie.dimensiontrapdoors.dimension;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import lommie.dimensiontrapdoors.DimensionTrapdoors;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensionTypes {
    public final static DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(DimensionTrapdoors.MOD_ID, Registries.DIMENSION_TYPE);

    public static final DeferredSupplier<DimensionType> TRAPDOOR = DIMENSION_TYPES.register("trapdoor",
            () -> new DimensionType(
                    true,
                    true,
                    true,
                    1,
                    -64,
                    128,
                    128,
                    TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("infiniburn_overworld")),
                    16,
                    new DimensionType.MonsterSettings(ConstantInt.ZERO,0),
                    DimensionType.Skybox.NONE,
                    DimensionType.CardinalLightType.DEFAULT,
                    EnvironmentAttributeMap.builder().build(),
                    HolderSet.empty()
            ));
}
