package com.npstra.tinkersgadgets.compat.tconstruct.traits;

import com.npstra.tinkersgadgets.Config;
import slimeknights.tconstruct.library.traits.AbstractTrait;

import java.util.Arrays;
import java.util.function.Supplier;

public class TraitsRegistry {
    public static final AbstractTrait PIERCING = build(TraitPiercing.ID, TraitPiercing::new);
    public static final AbstractTrait BOUNCING = build(TraitBouncing.ID, TraitBouncing::new);
    public static final AbstractTrait FRACTURE = build(TraitFracture.ID, TraitFracture::new);
    public static final AbstractTrait COLLECTION = build(TraitCollection.ID, TraitCollection::new);
    public static final AbstractTrait INTERACT = build(TraitInteract.ID, TraitInteract::new);
    public static final AbstractTrait DEFLECT = build(TraitDeflect.ID, TraitDeflect::new);
    public static final AbstractTrait REBOUND = build(TraitRebound.ID, TraitRebound::new);
    public static final AbstractTrait SHATTER = build(TraitShatter.ID, TraitShatter::new);
    public static final AbstractTrait ECHO = build(TraitEcho.ID, TraitEcho::new);
    public static final AbstractTrait PULSE = build(TraitPulse.ID, TraitPulse::new);
    public static final AbstractTrait GUIDANCE = build(TraitGuidance.ID, TraitGuidance::new);
    public static final AbstractTrait TRAUMA = build(TraitTrauma.ID, TraitTrauma::new);
    public static final AbstractTrait INERTIA = build(TraitInertia.ID, TraitInertia::new);
    public static final AbstractTrait PRESSURIZED = build(TraitPressurized.ID, TraitPressurized::new);
    public static final AbstractTrait TEMPER = build(TraitTemper.ID, TraitTemper::new);
    public static final AbstractTrait CONDENSATION = build(TraitCondensation.ID, TraitCondensation::new);
    public static final AbstractTrait FOCUS = build(TraitFocus.ID, TraitFocus::new);
    public static final AbstractTrait BURST = build(TraitBurst.ID, TraitBurst::new);
    public static final AbstractTrait CONDUCTION = build(TraitConduction.ID, TraitConduction::new);
    public static final AbstractTrait KINETIC = build(TraitKinetic.ID, TraitKinetic::new);
    public static final AbstractTrait SNIPING = build(TraitSniping.ID, TraitSniping::new);
    public static final AbstractTrait VORTEX = build(TraitVortex.ID, TraitVortex::new);
    public static final AbstractTrait EBB = build(TraitEbb.ID, TraitEbb::new);
    public static final AbstractTrait SEASONED = build(TraitSeasoned.ID, TraitSeasoned::new);

    private static AbstractTrait build(String identifier, Supplier<AbstractTrait> factory) {
        return Arrays.asList(Config.disabledTraits).contains(identifier) ? null : factory.get();
    }
}
