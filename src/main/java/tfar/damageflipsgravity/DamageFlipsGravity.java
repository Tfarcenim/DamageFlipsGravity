package tfar.damageflipsgravity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import tfar.gravity.network.CPacketHandler;

import java.util.UUID;

public class DamageFlipsGravity implements ModInitializer, ClientModInitializer {

	public static final String MODID = "damageflipsgravity";

	private static final UUID GRAVITY_FLIP_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABB");
	public static final AttributeModifier GRAVITY_FLIP = new AttributeModifier(GRAVITY_FLIP_ID, "Gravity flip", -0.16, AttributeModifier.Operation.ADDITION); // Add -0.16 to 0.08 so gravity is effectively flipped

	public static final Attribute ENTITY_GRAVITY = new RangedAttribute("damageflipsgravity.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true);

	@Override
	public void onInitialize() {
		Registry.register(Registry.ATTRIBUTE,new ResourceLocation(MODID,"entity_gravity"),ENTITY_GRAVITY);
	}

	@Override
	public void onInitializeClient() {
		CPacketHandler.registerClientMessages();
	}
}
